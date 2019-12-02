package edu.fsu.cen4020.android.procrastinaint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "CalendarActivity";
    private CalendarView my_calendarView;
    private FirebaseAuth auth;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate and add items to actionbar
        getMenuInflater().inflate(R.menu.drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_home:
                startActivity(new Intent(getApplicationContext(), calendar.class));
                return true;
            case R.id.nav_login:
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                return true;
            case R.id.nav_logout:
                if(auth.getCurrentUser() != null){
                    auth.signOut();
                }
                return true;
            case R.id.nav_newevent:
                startActivity(new Intent(getApplicationContext(), EventAdderActivity.class));
                return true;
            case R.id.nav_read_cal:
                startActivity(new Intent(getApplicationContext(), ReadCalendarActivity.class));
                return true;
            case R.id.nav_hentimer:
                startActivity(new Intent(getApplicationContext(), HelperEventNagvigatorTimeActivityInterface.class));
                return true;
            case R.id.nav_timer:
                startActivity(new Intent(getApplicationContext(), timerActivity.class));
                return true;
            case R.id.nav_notes:
                startActivity(new Intent(getApplicationContext(), NotesActivity.class));
                return true;
            case R.id.nav_write_cal:
                startActivity(new Intent(getApplicationContext(), WriteCalendar.class));
                return true;
            case R.id.nav_write_to_firebase:
                startActivity(new Intent(getApplicationContext(), AddUploadedEventsActivity.class));
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[] {Manifest.permission.INTERNET},0);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[] {Manifest.permission.READ_CALENDAR},1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[] {Manifest.permission.WRITE_CALENDAR},1);

        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},2);

        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE },2);

        }





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



        setContentView(R.layout.calendar_layout);
        setTitle("Home");
        my_calendarView = findViewById(R.id.calendarView);
//        menuButton = (ImageButton) findViewById(R.id.menuButton);
        my_calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override // i = year, i1 = month, i2 = day
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {

                Calendar c = Calendar.getInstance();
                c.setTimeZone(TimeZone.getTimeZone("America/New_York"));
                c.setTimeInMillis(18000000);
                c.set(Calendar.YEAR, i);
                c.set(Calendar.MONTH, i1);
                c.set(Calendar.DAY_OF_MONTH, i2);
                Long time = c.getTimeInMillis();
                openDialog(time);
            }
        });

    }

    public void openDialog(Long time){
        Calendar_Dialog dialog = new Calendar_Dialog();
        Bundle args = new Bundle();
        args.putLong("TIME", time);
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), "Calendar Dialog");
    }

    public void onEventAdderButtonClicked(View view){
        Intent intent = new Intent(this, EventAdderActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        auth.addAuthStateListener(authListener);
    }



}
