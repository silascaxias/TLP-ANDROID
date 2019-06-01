package system.scaxias.sysapp.authentication;


import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import system.scaxias.sysapp.home.HomeActivity;
import system.scaxias.sysapp.R;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private EditText txtEmail, txtPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);

        progressDialog = new ProgressDialog(this);

        findViewById(R.id.txtSignUp).setOnClickListener(this);
        findViewById(R.id.btnLogin).setOnClickListener(this);

    }
    private void userLogin(){

        String email = txtEmail.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();


        if(email.isEmpty()){
            txtEmail.setError("Email is required");
            txtEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            txtEmail.setError("Enter a valid email");
            txtEmail.requestFocus();
            return;
        }

        if(password.isEmpty()){
            txtPassword.setError("Password is required");
            txtPassword.requestFocus();
            return;
        }

        if(password.length() < 6){
            txtPassword.setError("Minimum password lenght should be 6");
            txtPassword.requestFocus();
            return;
        }
        progressDialog.setMessage("Loging...");
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if(task.isSuccessful()){
                    finish();
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() == null) return;

        finish();
        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.txtSignUp:
                finish();
                startActivity(new Intent(this, SignUpActivity.class));
                break;
            case R.id.btnLogin:
                userLogin();
                break;
        }
    }

}
