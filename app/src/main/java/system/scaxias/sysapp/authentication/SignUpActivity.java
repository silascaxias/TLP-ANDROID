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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import system.scaxias.sysapp.home.HomeActivity;
import system.scaxias.sysapp.R;
import system.scaxias.sysapp.model.User;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{
    private FirebaseAuth mAuth;
    private EditText txtEmail, txtPassword, txtNameUser, txtTel;
    private Integer level = 0;
    private DatabaseReference refeDatabase;
    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();

        fAuth = FirebaseAuth.getInstance();
        refeDatabase = FirebaseDatabase.getInstance().getReference();

        txtNameUser = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        txtTel = findViewById(R.id.txtTel);

        findViewById(R.id.btnSignUp).setOnClickListener(this);
        findViewById(R.id.txtLogin).setOnClickListener(this);

    }

    private void registerUser() {
        String email = txtEmail.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();
        String name = txtNameUser.getText().toString().trim();
        String tel = txtTel.getText().toString().trim();

        if(email.isEmpty()){
            txtEmail.setError("Email is required");
            txtEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            txtEmail.setError("Enter a valid email!");
            txtEmail.requestFocus();
            return;
        }

        if(password.isEmpty()){
            txtPassword.setError("Password is required!");
            txtPassword.requestFocus();
            return;
        }

        if(password.length() < 6){
            txtPassword.setError("Minimum password lenght should be 6!");
            txtPassword.requestFocus();
            return;
        }
        if(name.isEmpty()){
            txtNameUser.setError("Name is required!");
            txtNameUser.requestFocus();
            return;
        }
        if(tel.isEmpty()){
            txtTel.setError("Tel is required!");
            txtTel.requestFocus();
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this);

        progressDialog.setMessage("Creating user...");
        progressDialog.show();


        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if(task.isSuccessful()){

                    User user = new User();

                    user.setUserId(fAuth.getCurrentUser().getUid());
                    user.setEmail(txtEmail.getText().toString());
                    user.setName(txtNameUser.getText().toString());
                    user.setTel(txtTel.getText().toString());
                    user.setLevel(level);


                    refeDatabase.child("User").child(fAuth.getCurrentUser().getUid()).setValue(user);

                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                            .setDisplayName(txtNameUser.getText().toString())
                            .build();

                    currentUser.updateProfile(profile);
                    finish();

                    startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
                }else{
                    if(task.getException() instanceof FirebaseAuthUserCollisionException){
                        Toast.makeText(getApplicationContext(), "You are already registered", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(SignUpActivity.this, "ERROR: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.btnSignUp:
                registerUser();
                break;
            case R.id.txtLogin:
                finish();
                startActivity(new Intent(this, LoginActivity.class));
        }
    }

}
