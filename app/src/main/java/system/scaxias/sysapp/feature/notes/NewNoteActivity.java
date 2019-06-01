package system.scaxias.sysapp.feature.notes;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;
import java.util.UUID;

import system.scaxias.sysapp.R;
import system.scaxias.sysapp.model.Note;

public class NewNoteActivity extends AppCompatActivity {

    private EditText title, content;
    String nDate;
    private ProgressDialog progressDialog;
    private DatabaseReference fNotesDatabase;
    private FirebaseAuth fAuth;

    private Note noteNew = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);

        fAuth = FirebaseAuth.getInstance();
        fNotesDatabase = FirebaseDatabase.getInstance().getReference();

        title = findViewById(R.id.txtNoteTitle);
        content = findViewById(R.id.txtNoteContent);

        Date d = new Date();
        String date = DateFormat.getDateInstance(DateFormat.MEDIUM).format(d);
        nDate = date;
        progressDialog = new ProgressDialog(this);

        Intent intent = getIntent();
        if (intent.hasExtra("note")) {
            noteNew = (Note) intent.getSerializableExtra("note");


            title.setText(noteNew.getTitle());
            content.setText(noteNew.getContent());



        }

    }
    public void createNote(View view) {
        String nTitle = title.getText().toString().trim();
        String nContent = content.getText().toString().trim();


        if (nTitle.isEmpty()){
            title.setError("Title is required");
            title.requestFocus();
            return;
        }
        if(nContent.isEmpty()){
            content.setError("Content is required");
            content.requestFocus();
            return;
        }
        if(fAuth.getCurrentUser().getDisplayName().isEmpty()){
            Toast.makeText(this, "For create a note, define your profile name!", Toast.LENGTH_SHORT).show();
            return;
        }


        if (noteNew == null) {
            Note note = new Note();
            progressDialog.setMessage("Creating note...");
            progressDialog.show();

            note.setTitle(title.getText().toString());
            note.setContent(content.getText().toString());
            note.setId(UUID.randomUUID().toString());
            note.setDate(nDate);

            fNotesDatabase.child("Notes").child(fAuth.getCurrentUser().getDisplayName()).child(note.getId()).setValue(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                            progressDialog.dismiss();
                            finish();
                            startActivity(new Intent(NewNoteActivity.this, NotesActivity.class));
                        }
                    }, 2000);

                }
            });

        }else{
            Note note = new Note();
            progressDialog.setMessage("Updating note...");
            progressDialog.show();

            note.setId(noteNew.getId());
            note.setTitle(title.getText().toString());
            note.setContent(content.getText().toString());
            note.setDate(nDate);

            fNotesDatabase.child("Notes").child(fAuth.getCurrentUser().getDisplayName()).child(note.getId()).setValue(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                            progressDialog.dismiss();
                            finish();
                            startActivity(new Intent(NewNoteActivity.this, NotesActivity.class));
                        }
                    }, 2000);
                }
            });
            progressDialog.dismiss();
        }



    }

}
