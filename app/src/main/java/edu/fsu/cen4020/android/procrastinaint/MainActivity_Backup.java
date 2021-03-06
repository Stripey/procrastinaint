package edu.fsu.cen4020.android.procrastinaint;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity_Backup extends AppCompatActivity {

    private String TAG = MainActivity_Backup.class.getCanonicalName();
    public int counter;

    private Button loginButton;
    private Button registerButton;
    private Button EventAdderButton;
    private Button notesButton;
    private Button rwCalendarButton;
    private Button signOutButton;
    private Button temp_go_to_calendar_view;
    private TextView emailTextView;
    private Button timerButton;
    private Button HEN;
    private Button viewContentProviderEvents;
    private Button viewFirebaseEventsButton;

    // Firebase stuff to make sure user is signed in
    // Guide from https://www.androidlearning.com/android-login-registration-firebase-authentication/
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Parts assignment
        loginButton = (Button) findViewById(R.id.signUpButton);
        registerButton = (Button) findViewById(R.id.registerButton);
        EventAdderButton = (Button) findViewById(R.id.AddEventButton);
        notesButton = (Button) findViewById(R.id.notesButton);
        rwCalendarButton = (Button) findViewById(R.id.rwCalendarButton);
        emailTextView = (TextView) findViewById(R.id.emailText);
        signOutButton = (Button) findViewById(R.id.signOutButton);
        timerButton = (Button) findViewById(R.id.timerButton);
        temp_go_to_calendar_view = (Button) findViewById(R.id.temp_go_to_calendar_view);
        viewContentProviderEvents = (Button) findViewById(R.id.viewEventsButton);
        HEN = (Button) findViewById(R.id.hen);
        viewFirebaseEventsButton = (Button) findViewById(R.id.addUploadedEventsButton);
        // Check Permissions
        // https://developer.android.com/training/permissions/requesting.html
        // https://codinginflow.com/tutorials/android/run-time-permission-request
        // THis was used for the request permission snippet
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(MainActivity_Backup.this,
                    new String[] {Manifest.permission.INTERNET},0);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(MainActivity_Backup.this,
                    new String[] {Manifest.permission.READ_CALENDAR},1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(MainActivity_Backup.this,
                    new String[] {Manifest.permission.WRITE_CALENDAR},1);

        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(MainActivity_Backup.this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},2);

        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(MainActivity_Backup.this,
                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE },2);

        }



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
                Log.i(TAG, "onClick: edu.fsu.cen4020.android.procrastinaint.NoteEditorActivity.Event Add Button Called");
                onEventAdderButtonClicked(view);
            }
        });

        notesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onNotesButtonClicked(view);
            }
        });

        temp_go_to_calendar_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(MainActivity_Backup.this, calendar.class );
                startActivity(I);
            }
        });


        rwCalendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRWCalendarButtonClicked(view);
            }
        });

        timerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onTimerButtonClicked(view);
            }
        });

        HEN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onHENButtonClicked(view);
            }
        });

        viewContentProviderEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onViewContentProviderEventsButtonClicked(view);
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


        viewFirebaseEventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewFirebaseEvents(view);
            }
        });


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
        Intent intent = new Intent(this, EventAdderActivity.class);
        startActivity(intent);
    }

    public void onNotesButtonClicked(View view){
        Intent intent = new Intent(this, NotesActivity.class);
        startActivity(intent);
    }

    public void onRWCalendarButtonClicked(View view){
        Intent intent = new Intent(this, ReadCalendarActivity.class);
        startActivity(intent);
    }

    public void onTimerButtonClicked(View view){
        Intent intent = new Intent(this, timerActivity.class);
        startActivity(intent);

    }

    public void onHENButtonClicked(View view){
        Intent intent = new Intent(this, HelperEventNagvigatorTimeActivityInterface.class);
        startActivity(intent);

    }

    public void onViewContentProviderEventsButtonClicked(View view){
        Intent intent = new Intent(this, viewEvents.class);
        startActivity(intent);

    }

    public void viewFirebaseEvents(View view){
        Intent intent = new Intent(this, AddUploadedEventsActivity.class);
        startActivity(intent);
    }

    public void signOut(){
        auth.signOut();
        startActivity(getIntent());
    }


}
