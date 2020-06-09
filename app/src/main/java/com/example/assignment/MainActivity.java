package com.example.assignment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FragmentManager mFragmentManager;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        mFragmentManager = getSupportFragmentManager();

        // default home fragment
        mFragmentManager.beginTransaction().replace(R.id.content_frame, new LoginFragment()).commit();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { switch(item.getItemId()) {
        case R.id.signOut:
             signOut();
            return true;

    }
        return(super.onOptionsItemSelected(item));
    }
    //sign out method
    public void signOut() {
        auth.signOut();
        mFragmentManager.beginTransaction().replace(R.id.content_frame, new LoginFragment()).addToBackStack("MainActivity").commit();
    }
}
