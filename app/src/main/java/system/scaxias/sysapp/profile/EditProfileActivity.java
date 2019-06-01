package system.scaxias.sysapp.profile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import system.scaxias.sysapp.authentication.LoginActivity;
import system.scaxias.sysapp.R;

public class EditProfileActivity extends AppCompatActivity {

    private static final int CHOOSE_IMAGE = 101;
    private FirebaseAuth mAuth;
    private StorageReference mStorage;
    private ProgressDialog progressDialog;
    private TextView txtVerified;
    private ImageView imageView;
    private EditText txtName;
    private Uri uriProfileImage;
    private Boolean changed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        getSupportActionBar().hide();
        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();

        txtName = findViewById(R.id.txtName);
        imageView = findViewById(R.id.imageView);

        FirebaseUser userImage = mAuth.getCurrentUser();


        uriProfileImage = userImage.getPhotoUrl();

        txtVerified = findViewById(R.id.txtEmail);
        progressDialog = new ProgressDialog(this);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();
            }
        });

        loadUserInformation();

        findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(EditProfileActivity.this, ProfileActivity.class));
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

        // Envia a imagem para o STORAGE depois que o usuario escolhe
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != Activity.RESULT_CANCELED){
            progressDialog.setMessage("Uploading Image...");
            progressDialog.show();

            if(requestCode == CHOOSE_IMAGE){
                uriProfileImage = data.getData();
                uploadImageToFirebaseStorage();
                changed = true;
            }
        }
    }

    // Envia imagem para o Storage do Firebase
    private void uploadImageToFirebaseStorage() {


        final StorageReference profileImageRef = mStorage.child("Photos").child(mAuth.getCurrentUser().getUid()).child(uriProfileImage.getLastPathSegment());

        final UploadTask uploadTask = profileImageRef.putFile(uriProfileImage);


        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if(!task.isSuccessful()){
                    throw task.getException();
                }
                return profileImageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()){
                    uriProfileImage = task.getResult();
                    System.out.println(uriProfileImage);
                    Glide.with(EditProfileActivity.this).load(uriProfileImage).into(imageView);
                    progressDialog.dismiss();
                    Toast.makeText(EditProfileActivity.this, "Uploading Finished", Toast.LENGTH_SHORT).show();
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(EditProfileActivity.this, "Uploading failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
//    //Carrega informações do usuario e confirma a verificacao de email
    private void loadUserInformation() {
        final FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            if (user.getPhotoUrl() != null) {
                Glide.with(this)
                        .load(user.getPhotoUrl().toString())
                        .into(imageView);
            } else {
                Toast.makeText(this, "Usuario sem foto!", Toast.LENGTH_SHORT).show();
            }
            if (user.getDisplayName() != null) {
                txtName.setText(user.getDisplayName());
            }
            if (user.isEmailVerified()) {
                txtVerified.setText(getString(R.string.txt_if_verify));
            } else {
                txtVerified.setText(getString(R.string.txt_if_not_verify));
                txtVerified.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(EditProfileActivity.this, "Verification Email Sent", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }
    }

    // Atualiza o nome e foto de perfil do usuario
    public void saveUserInformation(View v) {
        String displayName = txtName.getText().toString();

        if (displayName.isEmpty()) {
            txtName.setError(getString(R.string.error_name_required));
            txtName.requestFocus();
            return;
        }
        FirebaseUser user = mAuth.getCurrentUser();
        UserProfileChangeRequest profile = null;
        if (user != null) {
            profile =  new UserProfileChangeRequest.Builder()
                        .setDisplayName(displayName)
                        .setPhotoUri(uriProfileImage) // Define a foto do usuario
                        .build();

            user.updateProfile(profile)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(EditProfileActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(EditProfileActivity.this, "Error to update", Toast.LENGTH_SHORT).show();
                            }
                            finish();
                            startActivity(new Intent(EditProfileActivity.this, ProfileActivity.class));
                        }
                    });
            }
    }

    private void showImageChooser(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intent.createChooser(intent, getString(R.string.toast_select_photo)), CHOOSE_IMAGE);

     }
}
