package com.csc20038.gps;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    LocationManager locationManager;
    LocationListener locationListener = new MyLocationListener();

    String latitude;
    String longitude;
    String altitude;

    // called upon activity creation
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        // get location, catching exception if there's an error
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        try{locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);}
        catch (SecurityException e) {System.out.println( e.toString() );}
    }

    // inner class
    public class MyLocationListener implements LocationListener {
        // sets text view text whenever location changes
        @Override
        public void onLocationChanged(Location location) {
            final TextView textView_Latitude = (TextView) findViewById(R.id.textView_Latitude);
            final TextView textView_Longitude = (TextView) findViewById(R.id.textView_Longitude);
            final TextView textView_Altitude = (TextView) findViewById(R.id.textView_Altitude);
            latitude = location.getLatitude()+"";
            longitude = location.getLongitude()+"";
            altitude = location.getAltitude()+"";
            textView_Latitude.setText(latitude);
            textView_Longitude.setText(longitude);
            textView_Altitude.setText(altitude);
        }
        public void onStatusChanged(String provider, int status, Bundle extras) {}
        public void onProviderEnabled(String provider) {}
        public void onProviderDisabled(String provider) {}
    }

    // remove location updates when activity is destroyed, to save resources
    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(locationListener);
    }

    // get menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // react to menu clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // add location info to intent before starting new activity
            case R.id.newLocation:
                Intent intent = new Intent(this, NewLocation.class);
                intent.putExtra(NewLocation.LATITUDE, latitude);
                intent.putExtra(NewLocation.LONGITUDE, longitude);
                intent.putExtra(NewLocation.ALTITUDE, altitude);
                startActivity(intent);
                return true;
            // start activity to view locations saved in databaase
            case R.id.savedLocations:
                startActivity( new Intent(this, SavedLocations.class) );
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}