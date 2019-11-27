package edu.fsu.cen4020.android.procrastinaint;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class RWCalendarActivity extends AppCompatActivity {


    private String TAG = RWCalendarActivity.class.getCanonicalName();
    private Button readCalander;
    private TextView showText;
    private Spinner calanderSpinner;
    private Long currentTime;
    HashMap<String, Long> calanderValues = new HashMap<>();
    private ArrayList<String[]> eventArrayList = new ArrayList<String[]>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rwcalendar);

        readCalander = (Button) findViewById(R.id.readCalendarButton);
        showText = (TextView) findViewById(R.id.readTextView);
        calanderSpinner = (Spinner) findViewById(R.id.calendarSpinner);
        currentTime = System.currentTimeMillis();
        Log.i(TAG, "onCreate: time is " + currentTime.toString());
        // Query though the content provider and get the names of the calanders
        calanderValues = getCalanders();

        List<String> calandersNames = new ArrayList<String>();

        for(String Key : calanderValues.keySet()){
            calandersNames.add(Key);
        }

        // Spinner Tutorial
        // https://www.tutorialspoint.com/android/android_spinner_control.htm
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, calandersNames);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        calanderSpinner.setAdapter(dataAdapter);


        readCalander.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readEvent(view);

                // Populate the eventRecyclerView after getting events
                initRecyclerView();
            }
        });



    }


    // This was taken from Google documentation on the calander contentprovider
    // https://developer.android.com/guide/topics/providers/calendar-provider#query
    private HashMap<String, Long> getCalanders(){

        // TODO Make Query Asynchronus
        // https://developer.android.com/reference/android/content/AsyncQueryHandler.html

        String[] EVENT_PROJECTION = new String[] {
                CalendarContract.Calendars._ID,                           // 0
                CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
                CalendarContract.Calendars.OWNER_ACCOUNT                  // 3
        };

        // The indices for the projection array above.
        int PROJECTION_ID_INDEX = 0;
        int PROJECTION_ACCOUNT_NAME_INDEX = 1;
        int PROJECTION_DISPLAY_NAME_INDEX = 2;
        int PROJECTION_OWNER_ACCOUNT_INDEX = 3;

        Log.i(TAG, "readCalander: Running Method");
        Cursor cur = null;
        ContentResolver cr = getContentResolver();
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        String selection = null;
        String[] selectionArgs = new String[] {};
        // Submit the query and get a Cursor object back.
        cur = cr.query(uri, EVENT_PROJECTION, null, null, null);

        HashMap<String, Long> calanderValues = new HashMap<>();

        while (cur.moveToNext()) {
            long calID = 0;
            String displayName = null;
            String accountName = null;
            String ownerName = null;

            // Get the field values
            calID = cur.getLong(PROJECTION_ID_INDEX);
            displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
            accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX);
            ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);


            calanderValues.put(displayName, calID);
            Log.i(TAG, "CalID " + calID + "\ndisplayName: " + displayName
                    + "\naccountName" + accountName + "\nOwnderName: " +ownerName); }

        Log.i(TAG, "readCalander finished");
        return calanderValues;
    }

    
    private void readEvent(View view){
        Log.i(TAG, "readEvent: Started");
        String calanderName = calanderSpinner.getSelectedItem().toString();
        Long calanderID = calanderValues.get(calanderName);
        Log.i(TAG, "Name = " + calanderName + "\nreadEvent: id = " + calanderID);


        Cursor cur = null;
        ContentResolver cr = getContentResolver();

        String[] mProjection =
                {
                        "_id",
                        CalendarContract.Events.CALENDAR_ID,
                        CalendarContract.Events.TITLE,
                        CalendarContract.Events.EVENT_LOCATION,
                        CalendarContract.Events.DTSTART,
                        CalendarContract.Events.DTEND,
                        CalendarContract.Events.DURATION,
                        CalendarContract.Events.RRULE,
                        CalendarContract.Events.RDATE,
                        CalendarContract.Events.LAST_DATE,

                };

        Uri uri = CalendarContract.Events.CONTENT_URI;
        String selection = CalendarContract.Events.CALENDAR_ID + " = ? AND (" +
                CalendarContract.Events.DTSTART + " >= ? OR " +
                CalendarContract.Events.LAST_DATE +
                " >= ? )";
        String[] selectionArgs = new String[]{
                calanderID.toString(),
                currentTime.toString(),
                currentTime.toString()
        };

        cur = cr.query(uri, mProjection, selection, selectionArgs, null);

        while (cur.moveToNext()) {
            Log.i(TAG, "readEvent: Starting calander");
            String title = cur.getString(cur.getColumnIndex(CalendarContract.Events.TITLE));
            String DTSTART = cur.getString(cur.getColumnIndex(CalendarContract.Events.DTSTART));
            String DTEND = cur.getString(cur.getColumnIndex(CalendarContract.Events.DTEND));
            String lasteDate = cur.getString(cur.getColumnIndex(CalendarContract.Events.LAST_DATE));
            String CalenderID = cur.getString(cur.getColumnIndex(CalendarContract.Events.CALENDAR_ID));
            String duration = cur.getString(cur.getColumnIndex(CalendarContract.Events.DURATION));
            String rDate = cur.getString(cur.getColumnIndex(CalendarContract.Events.RDATE));
            String rRule = cur.getString(cur.getColumnIndex(CalendarContract.Events.RRULE));
            String startDate = "";
            String endDate = "";
            String startTime = "";
            String endTime = "";

            // https://stackoverflow.com/questions/9754600/converting-epoch-time-to-date-string/9754625

            if (DTSTART != null){
                startDate = epochToDate(Long.parseLong(DTSTART));
                startTime = epochToTime(Long.parseLong(DTSTART));
            }

            if (lasteDate != null){
                endDate = epochToDate(Long.parseLong(lasteDate));
            }

            // Check DT for correct end time
            if(DTEND != null){
                endDate = epochToDate(Long.parseLong(DTEND));
                endTime = epochToTime(Long.parseLong(lasteDate));
            } else {
                // DTEND is null if its a recurring event, then need to get time from duration
                Long newDuration = RFC2445ToMilliseconds(duration);
                endTime = epochToTime(Long.parseLong(DTSTART) + newDuration);
            }


            // Items to store into eventRecyclerView dataset
            Log.i(TAG, "readEvent: \n" +
                    "Title = " + title +
                    "\nStart Date = " + startDate +
                    "\nEnd Date = " + endDate +
                    "\nStart Time= " + startTime +
                    "\nEnd Time = " + endTime);

            // Add this to the dataset for the recyclerview
            String[] recylerViewItems = new String[]{
                    title,
                    startDate,
                    endDate,
                    startTime,
                    endTime,
            };
            eventArrayList.add(recylerViewItems);

//            Log.i(TAG, "readEvent: \n" +
//                    "Title = " + title +
//                    "\nDTSTART: " + startDate +
//                    "\nDTEND: " + endDate +
//                    "\nlast date: " + lasteDate +
//                    "\nCalenderID: " + CalenderID +
//                    "\nDuration = " + duration +
//                    "\nRRule = " + rRule +
//                    "\nRdate = " + rDate);
        }
    }


    // https://stackoverflow.com/questions/20654967/convert-unix-epoch-time-to-formatted-date-unexpected-date
    public static String epochToDate(Long epocSeconds){
        Date updateDate = new Date(epocSeconds);
        SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
        return format.format(updateDate);

    }

    //https://stackoverflow.com/questions/4142313/convert-timestamp-in-milliseconds-to-string-formatted-time-in-java
    public static String epochToTime(Long epocSeconds){
        Date date = new Date(epocSeconds);
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm,a", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        return sdf.format(date);
    }


    // Duration is given in RFC2445 this was found on stackoverflow to convert
    public static long RFC2445ToMilliseconds(String str)
    {


        if(str == null || str.isEmpty())
            throw new IllegalArgumentException("Null or empty RFC string");

        int sign = 1;
        int weeks = 0;
        int days = 0;
        int hours = 0;
        int minutes = 0;
        int seconds = 0;

        int len = str.length();
        int index = 0;
        char c;

        c = str.charAt(0);

        if (c == '-')
        {
            sign = -1;
            index++;
        }

        else if (c == '+')
            index++;

        if (len < index)
            return 0;

        c = str.charAt(index);

        if (c != 'P')
            throw new IllegalArgumentException("Duration.parse(str='" + str + "') expected 'P' at index="+ index);

        index++;
        c = str.charAt(index);
        if (c == 'T')
            index++;

        int n = 0;
        for (; index < len; index++)
        {
            c = str.charAt(index);

            if (c >= '0' && c <= '9')
            {
                n *= 10;
                n += ((int)(c-'0'));
            }

            else if (c == 'W')
            {
                weeks = n;
                n = 0;
            }

            else if (c == 'H')
            {
                hours = n;
                n = 0;
            }

            else if (c == 'M')
            {
                minutes = n;
                n = 0;
            }

            else if (c == 'S')
            {
                seconds = n;
                n = 0;
            }

            else if (c == 'D')
            {
                days = n;
                n = 0;
            }

            else if (c == 'T')
            {
            }
            else
                throw new IllegalArgumentException ("Duration.parse(str='" + str + "') unexpected char '" + c + "' at index=" + index);
        }

        long factor = 1000 * sign;
        long result = factor * ((7*24*60*60*weeks)
                + (24*60*60*days)
                + (60*60*hours)
                + (60*minutes)
                + seconds);

        return result;
    }

    public String epochToDateTime(Long epocDate){

        Date date = new Date(epocDate);
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE,MMMM d,yyyy h:mm,a", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("EST"));
        return sdf.format(date);
    }


    private void initRecyclerView(){
        Log.d(TAG, "initRecyclerView: init recyclerview");
        RecyclerView eventRecyclerView = (RecyclerView) findViewById(R.id.readEventsRecyclerView);
        EventRecyclerViewAdapter adapter = new EventRecyclerViewAdapter(this, eventArrayList);
        eventRecyclerView.setAdapter(adapter);
        eventRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
