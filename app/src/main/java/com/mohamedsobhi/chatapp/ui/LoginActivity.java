package com.mohamedsobhi.chatapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mohamedsobhi.chatapp.R;
import com.mohamedsobhi.chatapp.SaveSetting;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.email_TextInput)
    TextInputLayout emailTextInput;
    @BindView(R.id.password_TextInput)
    TextInputLayout passwordTextInput;
    @BindView(R.id.fab)
    FloatingActionButton loginFab;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.sign_up_for_account)
    LinearLayout signUpForAccount;
    @BindView(R.id.signUp)
    TextView signUp;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);

            }
        });


        loginFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                if (loginValidations()){

                    String email = emailTextInput.getEditText().getText().toString();
                    String password = passwordTextInput.getEditText().getText().toString();

                    mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            loginFab.setVisibility(View.GONE);
                            progressBar.setVisibility(View.VISIBLE);
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                progressBar.setVisibility(View.GONE);

                                FirebaseUser user = Objects.requireNonNull(task.getResult()).getUser();

                                SaveSetting saveSetting = new SaveSetting(LoginActivity.this);
                                saveSetting.saveData("UserId",user.getUid());

                                Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();

                                Intent intent =new Intent(LoginActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();





                            } else {
                                // If sign in fails, display a message to the user.
                                progressBar.setVisibility(View.GONE);
                                loginFab.setVisibility(View.VISIBLE);


                                Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();

                            }



                        }
                    });
                }

            }
        });
    }

    public static boolean validEmail(String email) {
        String regex = "^([0-9a-zA-Z]([-.\\w]*[0-9a-zA-Z])*@([0-9a-zA-Z][-\\w]*[0-9a-zA-Z]\\.)+[a-zA-Z]{2,9})$";
        return !email.matches(regex);
    }


    private boolean loginValidations() {

        if (emailTextInput.getEditText().getText().toString().trim().equals("")) {
            emailTextInput.getEditText().setError("Enter your email");
            return false;
        }
        if (validEmail(emailTextInput.getEditText().getText().toString().trim())) {
            emailTextInput.getEditText().setError("this email is not valid");
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
