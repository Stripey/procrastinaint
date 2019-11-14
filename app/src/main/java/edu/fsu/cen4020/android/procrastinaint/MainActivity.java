package edu.fsu.cen4020.android.procrastinaint;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getCanonicalName();

    private Button loginButton;
    private Button registerButton;
    private Button EventAdderButton;
    private Button notesButton;
    private Button rwCalendarButton;
    private Button signOutButton;
    private TextView emailTextView;

    // Firebase stuff to make sure user is signed in
    // Guide from https://www.androidlearning.com/android-login-registration-firebase-authentication/
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Parts assignment
        loginButton = (Button) findViewById(R.id.loginButton);
        registerButton = (Button) findViewById(R.id.registerButton);
        EventAdderButton = (Button) findViewById(R.id.AddEventButton);
        notesButton = (Button) findViewById(R.id.notesButton);
        rwCalendarButton = (Button) findViewById(R.id.rwCalendarButton);
        emailTextView = (TextView) findViewById(R.id.emailText);
        signOutButton = (Button) findViewById(R.id.signOutButton);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: Login Button Called");
                onLoginButtonClicked(view);

            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRegisterButtonClicked(view);
            }
        });

        EventAdderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)  {
                Log.i(TAG, "onClick: Event Add Button Called");
                onEventAdderButtonClicked(view);
            }
        });

        notesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onNotesButtonClicked(view);
            }
        });


        rwCalendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRWCalendarButtonClicked(view);
            }
        });


        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        auth = FirebaseAuth.getInstance();
//        authListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
//
//                // Launch Login activity
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//                startActivity(new Intent(MainActivity.this, LoginActivity.class));
//                finish();
//            }
//        };

        if (auth.getCurrentUser() != null )
        {
            emailTextView.setText(user.getEmail());
        } else{
            emailTextView.setText("Not signed in");

        }


        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });
    }

    // onStart and onStop methods to connect to the firebase


    @Override
    protected void onStart() {
        super.onStart();
//        auth.addAuthStateListener(authListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authListener != null){
//            auth.removeAuthStateListener(authListener);
        }
    }

    public void onLoginButtonClicked(View view){

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void onRegisterButtonClicked(View view){
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }

    public void onEventAdderButtonClicked(View view){

    }

    public void onNotesButtonClicked(View view){

    }

    public void onRWCalendarButtonClicked(View view){
    }

    public void signOut(){
        auth.signOut();
        startActivity(getIntent());
    }


}
