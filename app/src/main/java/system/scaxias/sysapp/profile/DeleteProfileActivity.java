package system.scaxias.sysapp.profile;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import system.scaxias.sysapp.R;

public class DeleteProfileActivity extends AppCompatActivity {
    private FirebaseUser user;
    private FirebaseDatabase mDatabase;
    private DatabaseReference refBd;
    private FirebaseAuth fAuth;
    EditText password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_profile);

        password = findViewById(R.id.txtConfirmPassword);
    }

    public void deleteAccount(View view) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        refBd = mDatabase.getReference();
        fAuth = FirebaseAuth.getInstance();
        String pass = password.getText().toString().trim();

        final ProgressDialog progressDialog = new ProgressDialog(DeleteProfileActivity.this);

        if(pass.isEmpty()){
            password.setError("Password is requerid!");
            password.requestFocus();
            return;
        }

        progressDialog.setMessage("Deleting Account...");
        progressDialog.show();


        AuthCredential credential = EmailAuthProvider
                .getCredential(FirebaseAuth.getInstance().getCurrentUser().getEmail(), password.getText().toString());


        user.reauthenticate(credential).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                refBd.child("User").child(fAuth.getCurrentUser().getUid()).removeValue();
                refBd.child("Notes").child(fAuth.getCurrentUser().getDisplayName()).removeValue();

                user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {


                            progressDialog.dismiss();
                            FirebaseAuth mAuth;
                            mAuth = FirebaseAuth.getInstance();
                            mAuth.signOut();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(DeleteProfileActivity.this, "Error to delete user!", Toast.LENGTH_SHORT).show();
                    }
                });

        }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(DeleteProfileActivity.this, "Password is invalid!", Toast.LENGTH_SHORT).show();
            }
        });



    }

}








