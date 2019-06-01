package system.scaxias.sysapp.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import system.scaxias.sysapp.R;
import system.scaxias.sysapp.authentication.LoginActivity;
import system.scaxias.sysapp.model.User;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    private ImageView imageView;
    private TextView txtVerified,name, email, tel;
    private DatabaseReference refDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setTitle("Profile");

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        imageView = findViewById(R.id.imageView);
        name =  findViewById(R.id.txtName);
        txtVerified = findViewById(R.id.txtViewVerified);
        email = findViewById(R.id.txtEmail);
        tel = findViewById(R.id.txtTel);

        loadUserInformation();

        // Evento ao clicar no botão salvar
       findViewById(R.id.btnEdit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class));
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null) return;
        finish();
        startActivity(new Intent(this, LoginActivity.class));

    }

    //Carrega informações do usuario e confirma a verificacao de email
    private void loadUserInformation() {

        if (mAuth.getCurrentUser() != null) {
            final FirebaseUser user = mAuth.getCurrentUser();

            refDb = database.getReference().child("User").child(user.getUid());
            if (user.getPhotoUrl() != null) {
                Glide.with(this)
                        .load(user.getPhotoUrl().toString())
                        .into(imageView);
            }else{
                Toast.makeText(this, "Photo not found!", Toast.LENGTH_SHORT).show();
            }

            if (user.getDisplayName() != null) {
                name.setText(user.getDisplayName());
            }

            if(user.isEmailVerified()){
                txtVerified.setText(getString(R.string.txt_if_verify));
            }else{
                txtVerified.setText(getString(R.string.txt_if_not_verify));
                txtVerified.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(ProfileActivity.this, "Verification Email Sent!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }


            refDb.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);

                    email.setText(user.getEmail());
                    tel.setText(user.getTel());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }

    public void deleteAccount(View view) {
        startActivity(new Intent(ProfileActivity.this, DeleteProfileActivity.class));
    }
}
