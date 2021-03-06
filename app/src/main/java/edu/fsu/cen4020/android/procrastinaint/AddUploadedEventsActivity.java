package edu.fsu.cen4020.android.procrastinaint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import static edu.fsu.cen4020.android.procrastinaint.ReadCalendarActivity.epochToDate;
import static edu.fsu.cen4020.android.procrastinaint.ReadCalendarActivity.epochToTime;

public class AddUploadedEventsActivity extends AppCompatActivity {

    private static final String TAG = AddUploadedEventsActivity.class.getCanonicalName();
    private ArrayList<Event> eventArrayList = new ArrayList<Event>();
    private HashMap<Event, Boolean> localEventHM= new HashMap<Event, Boolean>();
    private Long currentTime;

    private EditText searchEventEditText;
    DatabaseReference mRef =  FirebaseDatabase.getInstance().getReference("Events");
    RecyclerView firebaseEventsRecyclerView;
    EventRecyclerViewAdapter adapter;

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
        setTitle("Back Up From Cloud");
        setContentView(R.layout.activity_add_uploaded_events);
        Log.d(TAG, "initRecyclerView: init recyclerview");
        RecyclerView eventRecyclerView = (RecyclerView) findViewById(R.id.firebaseEventRecyclerView);
        adapter = new EventRecyclerViewAdapter(this, eventArrayList);
        eventRecyclerView.setAdapter(adapter);
        eventRecyclerView.setLayoutManager(new LinearLayoutManager(this));



        // https://stackoverflow.com/questions/26422948/how-to-do-swipe-to-delete-cardview-in-android-using-support-library
        // This is used for the swipe functionailty

        SwipeableRecyclerViewTouchListener swipeTouchListener = new SwipeableRecyclerViewTouchListener(eventRecyclerView, new SwipeableRecyclerViewTouchListener.SwipeListener() {
            @Override
            public boolean canSwipeLeft(int position) {
                return false;
            }

            @Override
            public boolean canSwipeRight(int position) {
                return true;
            }

            @Override
            public void onDismissedBySwipeLeft(RecyclerView recyclerView, int[] reverseSortedPositions) {

            }

            @Override
            public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
                for(int position : reverseSortedPositions){
                    Event event = eventArrayList.get(position);

                    saveEvent(event);
                    Log.i(TAG, "onDismissedBySwipeRight: SavedEvent");
                    eventArrayList.remove(position);
                    adapter.notifyItemRemoved(position);
                }
                adapter.notifyDataSetChanged();
            }
        });
        eventRecyclerView.addOnItemTouchListener(swipeTouchListener);


    }



    // https://www.youtube.com/watch?v=jEmq1B1gveM
    // Read from the FireBase

    @Override
    protected void onStart() {
        super.onStart();
        currentTime = System.currentTimeMillis();
        getContentProviderEvents();


        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventArrayList.clear();
                for(DataSnapshot eventSnapShot : dataSnapshot.getChildren()){
                    Event event = eventSnapShot.getValue(Event.class);
                    if (localEventHM.containsKey(event)) {
                        continue;
                    }
                    else{
                        eventArrayList.add(event);
                    }

                }
                adapter.notifyDataSetChanged();

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }




    private void getContentProviderEvents(){
        String[] projection = {
                MainCP.TITLE,
                MainCP.RRule,
                MainCP.DURATION,
                MainCP.DTSTART,
                MainCP.DTEND,
                MainCP.LAST_DATE};

        String selection =
                MainCP.DTSTART + " >= ? OR " +
                        MainCP.LAST_DATE +
                        " >= ?";
        String[] selectionArgs = new String[]{
                currentTime.toString(),
                currentTime.toString()
        };

        Cursor cursor = getContentResolver().query(
                MainCP.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                MainCP.DTSTART

        );

        if (cursor.getCount()!= 0){
            if(cursor.moveToFirst()){
                do{
                    String title = cursor.getString(cursor.getColumnIndex(MainCP.TITLE));
                    String rRule = cursor.getString(cursor.getColumnIndex(MainCP.RRule));
                    String duration = cursor.getString(cursor.getColumnIndex(MainCP.DURATION));
                    Long DTSTART = cursor.getLong(cursor.getColumnIndex(MainCP.DTSTART));
                    Long DTEND = cursor.getLong(cursor.getColumnIndex(MainCP.DTEND));
                    Long LAST_DATE = cursor.getLong(cursor.getColumnIndex(MainCP.LAST_DATE));
                    Event event = new Event(title, null, rRule, duration, DTSTART, DTEND, LAST_DATE);
                    localEventHM.put(event, true);
                }while(cursor.moveToNext());
            }
        }

    }







    public void saveEvent(Event item){
        // Track if event is reoccuring or singular


        if(item.isRecurring()){
            ArrayList<Event> newEvents = item.recurringToSingular(currentTime);

            for(Event item2 : newEvents) {

                ContentValues values = new ContentValues();
                values.put(MainCP.TITLE, item2.getTitle());
                values.put(MainCP.DTSTART, item2.getDTSTART());
                values.put(MainCP.DTEND, item2.getDTEND());
                values.put(MainCP.LAST_DATE, item2.getLAST_DATE());
                values.put(MainCP.NEW, 1);
                getContentResolver().insert(MainCP.CONTENT_URI, values);
            }

        }else{
            Log.i(TAG, "saveButton singularEvent " +
                    "\nTitle =" + item.getTitle() +
                    "\nDTStart ="  + item.getDTSTART() +
                    "\nDTEND = " + item.getDTEND());

            ContentValues values = new ContentValues();
            values.put(MainCP.TITLE, item.getTitle());
            values.put(MainCP.DTSTART, item.getDTSTART());
            values.put(MainCP.DTEND, item.getDTEND());
            values.put(MainCP.LAST_DATE, item.getLAST_DATE());
            values.put(MainCP.NEW, 1);
            getContentResolver().insert(MainCP.CONTENT_URI, values);
        }

    }
}
