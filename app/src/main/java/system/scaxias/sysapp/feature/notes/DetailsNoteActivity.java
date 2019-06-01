package system.scaxias.sysapp.feature.notes;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import system.scaxias.sysapp.R;
import system.scaxias.sysapp.model.Note;

public class DetailsNoteActivity extends AppCompatActivity {

    private Note note = null;
    private DatabaseReference refNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_note);

        TextView title = findViewById(R.id.txtNoteTitle);
        TextView content = findViewById(R.id.txtNoteContent);


        Intent intent = getIntent();
        if (intent.hasExtra("note")) {
            note = (Note) intent.getSerializableExtra("note");
        }

        title.setText(note.getTitle());
        content.setText(note.getContent());

        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();

        refNote = mDatabase.getReference();
    }
    public void editNote(View v){
        Intent intent = new Intent(this, NewNoteActivity.class);
        intent.putExtra("note", note);
        finish();
        startActivity(intent);
    }
    public void deleteNote(View v){
        FirebaseAuth fAuth = FirebaseAuth.getInstance();

        final ProgressDialog progressDialog = new ProgressDialog(DetailsNoteActivity.this);

        progressDialog.setMessage("Deleting note...");
        progressDialog.show();


        refNote.child("Notes").child(fAuth.getCurrentUser().getDisplayName()).child(note.getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Do something after 5s = 5000ms
                        progressDialog.dismiss();
                        finish();
                        startActivity(new Intent(DetailsNoteActivity.this, NotesActivity.class));
                    }
                }, 2000);
            }
        });
    }

}
