package com.csc20038.gps;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.Menu;
import android.view.MenuItem;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.Cursor;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;

public class EditLocation extends AppCompatActivity {

    private SQLiteDatabase db;
    private DatabaseHelper databaseHelper;
    private Cursor cursor;

    long locationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_location);

        // get toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_edit_location);
        setSupportActionBar(toolbar);

        // allow back button on toolbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // set TextView and EditText text to match location info of selected item passed through intent
        locationId = (Long) getIntent().getExtras().get("id");
        databaseHelper = new DatabaseHelper(this);
        try {
            db = databaseHelper.getReadableDatabase();
            cursor = db.query("Record", new String[]{"_id", "Name", "Notes", "Latitude", "Longitude", "Altitude"}, "_id = ?", new String[]{Long.toString(locationId)}, null, null, null);
            cursor.moveToFirst();

            String name = cursor.getString(1);
            EditText editTextName = (EditText) findViewById(R.id.editText_Name);
            editTextName.setText(name);

            String notes = cursor.getString(2);
            EditText editTextNotes = (EditText) findViewById(R.id.editText_Notes);
            editTextNotes.setText(notes);

            double latitude = cursor.getDouble(3);
            TextView textViewLatitude = (TextView) findViewById(R.id.textView_Latitude);
            textViewLatitude.setText(latitude+"");

            double longitude = cursor.getDouble(4);
            TextView textViewLongitude = (TextView) findViewById(R.id.textView_Longitude);
            textViewLongitude.setText(longitude+"");

            double altitude = cursor.getDouble(5);
            TextView textViewAltitude = (TextView) findViewById(R.id.textView_Altitude);
            textViewAltitude.setText(altitude+"");
        } catch(SQLiteException e) {Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT).show();}
    }

    // close cursor and db if necessary
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(cursor != null) cursor.close();
        if(db != null) db.close();
    }

    // get menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_location, menu);
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
                    databaseHelper.updateRecord(db, locationId, name, notes);
                    startActivity(new Intent(getApplicationContext(), SavedLocations.class));
                    Toast.makeText(getApplicationContext(), "Location updated", Toast.LENGTH_LONG).show();
                }
                return true;
            // delete location, using alert dialog to allow user to cancel operation
            case R.id.deleteLocation:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        databaseHelper.deleteRecord(db, locationId);
                        startActivity(new Intent(getApplicationContext(), SavedLocations.class));
                        Toast.makeText(getApplicationContext(), "Location deleted", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getApplicationContext(), "Deletion cancelled", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setTitle("Are you sure?");
                builder.setMessage("Are you sure you want to delete this location?");
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
