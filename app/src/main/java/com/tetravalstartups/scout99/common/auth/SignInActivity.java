package com.tetravalstartups.scout99.common.auth;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tetravalstartups.scout99.R;
import com.tetravalstartups.scout99.common.splash.SplashActivity;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    MaterialButton btnSignIn;
    ProgressDialog progressDialog;
    private TextView txtSignUp, txtForgotPassword;
    private TextInputEditText tiEmail, tiPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        init();
    }

    private void init() {
        txtSignUp = findViewById(R.id.txtSignUp);
        txtForgotPassword = findViewById(R.id.txtForgotPassword);
        txtForgotPassword.setOnClickListener(this);
        tiEmail = findViewById(R.id.tiEmail);
        tiPassword = findViewById(R.id.tiPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        btnSignIn.setOnClickListener(this);
        txtSignUp.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
    }

    @Override
    public void onClick(View v) {
        if (v == txtSignUp) {
            startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
        }
        if (v == btnSignIn) {
            doUIValidation();
        }
        if (v == txtForgotPassword) {
            dialogForgotPassword();
        }
    }

    private void doUIValidation() {
        String email = tiEmail.getText().toString();
        String password = tiPassword.getText().toString();
        if (TextUtils.isEmpty(email)) {
            tiEmail.requestFocus();
            tiEmail.setError("Email is required!");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            tiPassword.requestFocus();
            tiPassword.setError("Password is required!");
            return;
        }
        progressDialog.show();
        doSignIn(email, password);
    }

    private void doSignIn(String email, String password) {
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        if (task.isSuccessful()) {
                            if (firebaseUser.isEmailVerified()) {
                                Toast.makeText(SignInActivity.this, "Sign In Successful", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(SignInActivity.this, SplashActivity.class));
                                finish();
                            } else {
                                Toast.makeText(SignInActivity.this, "Please verify your email!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(SignInActivity.this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void dialogForgotPassword() {

        LayoutInflater factory = LayoutInflater.from(this);
        final View consentDialogView = factory.inflate(R.layout.forgot_password_dialog, null);
        final AlertDialog consentDialog = new AlertDialog.Builder(this).create();
        consentDialog.setCancelable(false);
        final TextInputEditText tiFPEmail = consentDialogView.findViewById(R.id.tiFPEmail);
        consentDialog.setButton(Dialog.BUTTON_POSITIVE, "Forgot Password", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                hideKeyboard(SignInActivity.this);
                progressDialog.show();
                String email = tiFPEmail.getText().toString();
                dialog.dismiss();
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    progressDialog.dismiss();
                                    Toast.makeText(SignInActivity.this, "New password link sent to you registered email.", Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(SignInActivity.this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
        consentDialog.setButton(Dialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        consentDialog.setView(consentDialogView);

        consentDialog.show();
    }
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
