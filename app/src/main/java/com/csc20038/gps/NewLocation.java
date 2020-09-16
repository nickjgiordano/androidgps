package com.csc20038.gps;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.database.sqlite.SQLiteDatabase;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;

public class NewLocation extends AppCompatActivity {

    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String ALTITUDE = "altitude";

    private SQLiteDatabase db;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_location);

        // get toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_new_location);
        setSupportActionBar(toolbar);

        // allow back button on toolbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // set TextView text to match location info passed through intent
        Intent intent = getIntent();
        TextView textViewLatitude = (TextView) findViewById(R.id.textView_Latitude);
        textViewLatitude.setText( intent.getStringExtra(LATITUDE) );
        TextView textViewLongitude = (TextView) findViewById(R.id.textView_Longitude);
        textViewLongitude.setText( intent.getStringExtra(LONGITUDE) );
        TextView textViewAltitude = (TextView) findViewById(R.id.textView_Altitude);
        textViewAltitude.setText( intent.getStringExtra(ALTITUDE) );
    }

    // close db if necessary
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(db != null) db.close();
    }

    // get menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_location, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // react to menu clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // save location to database, using toasts upon data validation errors or successful addition
            case R.id.saveLocation:
                EditText editTextName = (EditText) findViewById(R.id.editText_Name);
                String name = editTextName.getText().toString();
                if( name.isEmpty() ) {
                    Toast.makeText(this, "You must specify a location name!", Toast.LENGTH_LONG).show();
                } else {
                    EditText editTextNotes = (EditText) findViewById(R.id.editText_Notes);
                    String notes = editTextNotes.getText().toString();
                    Intent intent = getIntent();
                    double latitude = Double.parseDouble(intent.getStringExtra(LATITUDE));
                    double longitude = Double.parseDouble(intent.getStringExtra(LONGITUDE));
                    double altitude = Double.parseDouble(intent.getStringExtra(ALTITUDE));
                    databaseHelper = new DatabaseHelper(this);
                    db = databaseHelper.getReadableDatabase();
                    databaseHelper.insertRecord(db, name, notes, latitude, longitude, altitude);
                    startActivity(new Intent(this, MainActivity.class));
                    Toast.makeText(getApplicationContext(), "Saved new location", Toast.LENGTH_LONG).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
