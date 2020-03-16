package com.tetravalstartups.scout99.common.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tetravalstartups.scout99.R;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private String selected_youare, selected_heardfrom;
    private AutoCompleteTextView actYouAre, actHeardFrom;
    private MaterialButton btnSignUp;
    private TextInputEditText tiFullName, tiEmail, tiPassword, tiPhoneNumber;
    private ProgressDialog progressDialog;
    private ImageView imgGoBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        setContentView(R.layout.activity_sign_up);
        init();
    }

    private void init() {
        String[] YOU_ARE = new String[]{"Corporate", "Small Business", "Business Developer", "Individual/Freelance", "Student", "Other"};
        String[] HEARD_FROM = new String[]{"LinkedIn", "Instagram", "Facebook", "Web Search", "Just Dial", "Sulekha", "Other"};
        ArrayAdapter<String> youareAdapter = new ArrayAdapter<>(SignUpActivity.this, R.layout.support_simple_spinner_dropdown_item, YOU_ARE);
        ArrayAdapter<String> heardfromAdapter = new ArrayAdapter<>(SignUpActivity.this, R.layout.support_simple_spinner_dropdown_item, HEARD_FROM);
        actYouAre = findViewById(R.id.actYouAre);
        actHeardFrom = findViewById(R.id.actHeardFrom);
        imgGoBack = findViewById(R.id.imgGoBack);
        imgGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });
        actYouAre.setAdapter(youareAdapter);
        actHeardFrom.setAdapter(heardfromAdapter);
        actYouAre.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selected_youare = parent.getItemAtPosition(position).toString();
            }
        });
        actHeardFrom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selected_heardfrom = parent.getItemAtPosition(position).toString();
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);

        tiFullName = findViewById(R.id.tiFullName);
        tiEmail = findViewById(R.id.tiEmail);
        tiPassword = findViewById(R.id.tiPassword);
        tiPhoneNumber = findViewById(R.id.tiPhoneNumber);

        btnSignUp = findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == btnSignUp){
            doUIValidation();
        }
    }

    private void doUIValidation() {
        String full_name = tiFullName.getText().toString();
        String email = tiEmail.getText().toString();
        String password = tiPassword.getText().toString();
        String phone_number = tiPhoneNumber.getText().toString();
        String you_are = actYouAre.getText().toString();
        String heard_from = actHeardFrom.getText().toString();

        if (TextUtils.isEmpty(full_name)){
            tiFullName.requestFocus();
            tiFullName.setError("Full Name is required!");
            return;
        }
        if (TextUtils.isEmpty(email)){
            tiEmail.requestFocus();
            tiEmail.setError("We will not spam you!");
            return;
        }
        if (TextUtils.isEmpty(password) || password.length() <6){
            tiPassword.requestFocus();
            tiPassword.setError("Six character password required!");
            return;
        }
        if (TextUtils.isEmpty(phone_number)){
            tiPhoneNumber.requestFocus();
            tiPhoneNumber.setError("We will not spam you!");
            return;
        }
        if (TextUtils.isEmpty(you_are)){
            Toast.makeText(this, "Some fields are empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(heard_from)){
            Toast.makeText(this, "Some fields are empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        openConsentDialog(full_name, email, password, phone_number, selected_youare, selected_heardfrom);
    }

    private void openConsentDialog(final String full_name, final String email, final String password, final String phone_number, final String you_are, final String heard_from) {
        LayoutInflater factory = LayoutInflater.from(this);
        final View consentDialogView = factory.inflate(R.layout.privacy_policy_dialog, null);
        final AlertDialog consentDialog = new AlertDialog.Builder(this).create();
        consentDialog.setButton(Dialog.BUTTON_POSITIVE,"Accept",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                progressDialog.show();
                doSignUp(full_name, email, password, phone_number, you_are, heard_from);
            }
        });
        consentDialog.setButton(Dialog.BUTTON_NEGATIVE,"DECLINE",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        consentDialog.setView(consentDialogView);
        consentDialog.show();
    }

    private void doSignUp(final String full_name, final String email, String password, final String phone_number, final String you_are, final String heard_from) {
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    User user = new User();
                    user.setUser_id(firebaseAuth.getCurrentUser().getUid());
                    user.setUser_full_name(full_name);
                    user.setUser_email(email);
                    user.setUser_phone_number(phone_number);
                    user.setUser_is(you_are);
                    user.setUser_heard_from(heard_from);
                    user.setUser_status("Active");
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("users")
                            .document(firebaseAuth.getCurrentUser().getUid())
                            .set(user)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        progressDialog.dismiss();
                                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                        firebaseUser.sendEmailVerification();
                                        signupSuccessDialog();
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(SignUpActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(SignUpActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void signupSuccessDialog() {
        LayoutInflater factory = LayoutInflater.from(this);
        final View consentDialogView = factory.inflate(R.layout.signup_success_dialog, null);
        final AlertDialog consentDialog = new AlertDialog.Builder(this).create();
        consentDialog.setCancelable(false);
        consentDialog.setButton(Dialog.BUTTON_POSITIVE,"Continue",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                finish();
            }
        });
        consentDialog.setView(consentDialogView);
        consentDialog.show();
    }
}
