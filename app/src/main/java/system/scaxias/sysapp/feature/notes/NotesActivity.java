package system.scaxias.sysapp.feature.notes;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import system.scaxias.sysapp.R;
import system.scaxias.sysapp.model.Note;

public class NotesActivity extends AppCompatActivity {
    private ListView listNotes;
    private List<Note> notes = new ArrayList<>();
    private DatabaseReference refNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        listNotes = findViewById(R.id.notes_list);

        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();

        refNote = mDatabase.getReference();

        loadList();

        listNotes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(NotesActivity.this, DetailsNoteActivity.class);
                intent.putExtra("note", notes.get(position));
                startActivity(intent);
            }
        });
    }

    private void loadList() {
        FirebaseAuth fAuth = FirebaseAuth.getInstance();

        FirebaseUser user = fAuth.getCurrentUser();
        refNote.child("Notes").child(user.getDisplayName()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notes.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Note n = snapshot.getValue(Note.class);
                    notes.add(n);
                }

                ArrayAdapter<Note> adapter = new ArrayAdapter<>(NotesActivity.this, android.R.layout.simple_list_item_1, notes);
                listNotes.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(NotesActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_context, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.new_note_btn) {
            Intent iNewNote = new Intent(NotesActivity.this, NewNoteActivity.class);
            startActivity(iNewNote);
        }
        return true;
    }



}
