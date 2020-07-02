package com.mohamedsobhi.chatapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SignupActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.email_Tedittext)
    EditText emailTedittext;
    @BindView(R.id.userName_edittext)
    EditText userNameEdittext;
    @BindView(R.id.sign_up_button)
    Button signUpButton;
    @BindView(R.id.sign_in)
    TextView signIn;
    @BindView(R.id.password_textInput)
    TextInputLayout passwordTextInput;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        initToolbar();
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (signUpValidations()) {

                }

                String email = emailTedittext.getText().toString();
                String userName = userNameEdittext.getText().toString();
                String password = passwordTextInput.getEditText().getText().toString();

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                signUpButton.setVisibility(View.GONE);

                                progressBar.setVisibility(View.VISIBLE);
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    FirebaseUser user = Objects.requireNonNull(task.getResult()).getUser();
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(userName).build();

                                    assert user != null;
                                    user.updateProfile(profileUpdates);
                                    HashMap<String, String> hashMap = new HashMap<>();

                                    hashMap.put("name", userName);

                                    firebaseFirestore.collection("Users").document(user.getUid()).set(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {
                                                progressBar.setVisibility(View.GONE);


                                                SaveSetting saveSetting = new SaveSetting(SignupActivity.this);
                                                saveSetting.saveData("UserId",user.getUid());

                                                Toast.makeText(SignupActivity.this, "SignUp Successfully", Toast.LENGTH_SHORT).show();

                                                Intent intent =new Intent(SignupActivity.this,MainActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                finish();

                                            }

                                        }
                                    });


                                } else {
                                    // If sign in fails, display a message to the user.
                                    progressBar.setVisibility(View.GONE);
                                    signUpButton.setVisibility(View.VISIBLE);

                                    Toast.makeText(SignupActivity.this, "SignUp Failed", Toast.LENGTH_SHORT).show();

                                }



                            }
                        });

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_notif_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    public static boolean validEmail(String email) {
        String regex = "^([0-9a-zA-Z]([-.\\w]*[0-9a-zA-Z])*@([0-9a-zA-Z][-\\w]*[0-9a-zA-Z]\\.)+[a-zA-Z]{2,9})$";
        return !email.matches(regex);
    }


    private boolean signUpValidations() {

        if (emailTedittext.getText().toString().trim().equals("")) {
            emailTedittext.setError("Enter your email");
            return false;
        }
        if (validEmail(emailTedittext.getText().toString().trim())) {
            emailTedittext.setError("this email is not valid");
            return false;
        }
        if (userNameEdittext.getText().toString().trim().equals("")) {
            userNameEdittext.setError("Enter your email");
            return false;
        }
        if (passwordTextInput.getEditText().getText().toString().trim().equals("")) {
            passwordTextInput.getEditText().setError("Enter a password");
            return false;
        }
        if (passwordTextInput.getEditText().getText().toString().trim().length() < 6) {
            passwordTextInput.getEditText().setError("password must be more than 6 letters");
            return false;
        }

        return true;
    }

}
