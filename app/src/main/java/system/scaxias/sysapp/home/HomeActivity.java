package system.scaxias.sysapp.home;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

import system.scaxias.sysapp.authentication.LoginActivity;
import system.scaxias.sysapp.feature.notes.NotesActivity;
import system.scaxias.sysapp.profile.ProfileActivity;
import system.scaxias.sysapp.R;

public class HomeActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setTitle("Home");
        mAuth = FirebaseAuth.getInstance();

    }
    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null) {
            return;
        }
        finish();
        startActivity(new Intent(this, LoginActivity.class));

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menuLogout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.menuProfile:
                startActivity(new Intent(this, ProfileActivity.class));
                break;
        }

        return true;
    }
    public void goNotes(View view){
        startActivity(new Intent(HomeActivity.this, NotesActivity.class));
    }

}
