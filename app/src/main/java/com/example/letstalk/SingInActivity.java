package com.example.letstalk;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.letstalk.databinding.ActivitySingInBinding;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class SingInActivity extends AppCompatActivity {
    ActivitySingInBinding binding;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySingInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(SingInActivity.this);
        progressDialog.setTitle("Log In!");
        progressDialog.setMessage("Log in to your account");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);




        binding.btnsignIN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                auth.signInWithEmailAndPassword(binding.etemail.getText().toString(),binding.etpassword.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressDialog.dismiss();
                                if (task.isSuccessful()){
                                    Intent i = new Intent(SingInActivity.this,MainActivity.class);
                                    startActivity(i);
                                }
                                else {
                                    Toast.makeText(SingInActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
        binding.tvsingnup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SingInActivity.this,SignUpActivity.class);
                startActivity(i);
            }
        });

        binding.btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        if (auth.getCurrentUser()!=null){
            Intent i = new Intent(SingInActivity.this,MainActivity.class);
            startActivity(i);
        }
    }

        int RC_SIGN_IN=65;
        private void signIn(){
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent,RC_SIGN_IN);
        }

        @Override
        public void onActivityResult(int requestCode,int resultCode,Intent data){
            super.onActivityResult(requestCode,resultCode,data);

            if(requestCode == RC_SIGN_IN){
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    GoogleSignInAccount account=task.getResult(ApiException.class);
                    Log.d(TAG,"firebaseAuthWithGoogle:"+account.getId());
                    firebaseAuthWithGoogle(account.getIdToken());
                }catch (ApiException e){
                    Log.w(TAG,"Google sign in failed",e);
                }
            }
        }

        private void firebaseAuthWithGoogle(String idToken){
            AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);
            auth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Log.d("TAG","signInWithCredential:success");
                                FirebaseUser user = auth.getCurrentUser();
                                //updateUI(user);
                            }else{
                                Log.w(TAG,"signInWithCredential:failure",task.getException());
                                //Snackbar.make(mBinding.mainLayout,"Authentication Failed.",Snackbar.LENGTH_SHORT).show();
                                //updateUI(null);
                            }
                        }
                    });
        }

}