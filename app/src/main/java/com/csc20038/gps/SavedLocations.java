package com.csc20038.gps;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.Cursor;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class SavedLocations extends AppCompatActivity {

    private SQLiteDatabase db;
    private DatabaseHelper databaseHelper;
    private Cursor cursor;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_locations);

        // get toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_saved_locations);
        setSupportActionBar(toolbar);

        // allow back button on toolbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // query database to populate listView
        listView = (ListView) findViewById(R.id.ListView_records);
        databaseHelper = new DatabaseHelper(this);
        try{
            db = databaseHelper.getReadableDatabase();
            cursor = db.query("Record", new String[]{"_id", "Name", "Notes", "Latitude", "Longitude", "Altitude"}, null, null, null, null, null);
            SimpleCursorAdapter listAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, cursor, new String[]{"Name"}, new int[]{android.R.id.text1}, 0);
            listView.setAdapter(listAdapter);

        } catch(SQLiteException e) {Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT).show();}

        // make ListView items clickable to edit location
        AdapterView.OnItemClickListener itemClickListener =
                new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> listView,
                                            View itemView,
                                            int position,
                                            long id) {
                        Intent intent = new Intent(getApplicationContext(), EditLocation.class);
                        intent.putExtra("id", id);
                        startActivity(intent);
                    }
                };
        listView.setOnItemClickListener(itemClickListener);
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
        getMenuInflater().inflate(R.menu.menu_saved_locations, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // react to menu clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // send email containing full contents of location database
            case R.id.email:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"t.kyriacou@keele.ac.uk"});
                intent.putExtra(Intent.EXTRA_SUBJECT, ("Location GPS information"));
                intent.putExtra(Intent.EXTRA_TEXT, databaseHelper.getDatabaseContentsAsString(db) );
                intent.setType("message/rfc822");
                startActivity( Intent.createChooser(intent, "Choose an Email client:") );
                return true;
            // delete all locations, using alert dialog to allow user to cancel operation
            case R.id.deleteAll:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try{
                            databaseHelper.deleteAll(db); // deletes all records
                            //databaseHelper.createDb(db); // comment or uncomment this line to toggle whether db gets recreated with test data
                            cursor = db.query("Record", new String[]{"_id", "Name", "Notes", "Latitude", "Longitude", "Altitude"}, null, null, null, null, null);
                            SimpleCursorAdapter listAdapter = new SimpleCursorAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, cursor, new String[]{"Name"}, new int[]{android.R.id.text1}, 0);
                            listView.setAdapter(listAdapter);
                            Toast.makeText(getApplicationContext(), "Deletion successful", Toast.LENGTH_LONG).show();
                        } catch(SQLiteException e) {Toast.makeText(getApplicationContext(), "Database unavailable", Toast.LENGTH_SHORT).show();}
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getApplicationContext(), "Deletion cancelled", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setTitle("Are you sure?");
                builder.setMessage("Are you sure you want to delete all locations?");
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
