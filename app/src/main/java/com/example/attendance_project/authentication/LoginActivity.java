package com.example.attendance_project.authentication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.attendance_project.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {
    static final int GOOGLE_SIGN_IN = 123;
    ProgressDialog dialog;
    public  User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        user=new User(getApplicationContext());
        if (user.getmAuth().getCurrentUser() != null) {
            FirebaseUser firebaseUser = user.getmAuth().getCurrentUser();
            user.updateUI(firebaseUser);
        }
    }

    public void loginStart(View view) {
        dialog=new ProgressDialog(this);
        dialog.setTitle("Logging In");
        dialog.setMessage("Please wait...");
        dialog.show();
        dialog.setCancelable(false);
        Intent signInIntent = user.getmGoogleSignInClient().getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        dialog.dismiss();
        if (requestCode == GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(LoginActivity.this,"Failed to login.. please Try again", Toast.LENGTH_SHORT).show();
            }
        }

    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        user.getmAuth().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "signInWithCredential:success");
                            FirebaseUser firebaseUser = user.getmAuth().getCurrentUser();
                            Toast.makeText(LoginActivity.this,"Logged In as "+firebaseUser.getDisplayName(),Toast.LENGTH_SHORT).show();

                            user.updateUI(firebaseUser);
                            Intent intent2=new Intent();
                            intent2.putExtra("success",true);
                            setResult(1,intent2);
                            finish();
                        } else {
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            user.updateUI(null);
                        }
                    }
                });
    }

}

