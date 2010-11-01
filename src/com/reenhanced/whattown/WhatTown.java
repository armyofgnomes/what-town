package com.reenhanced.whattown;

import java.io.IOException;
import java.util.List;
import android.app.Activity;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;
import android.widget.Toast;

public class WhatTown extends Activity implements LocationListener {
	private LocationManager mgr;
	private TextView output;
	private String best;
	
	// Define human readable names
	private static final String[] A = { "invalid", "n/a", "fine", "coarse" };
	private static final String[] P = { "invalid", "n/a", "low", "medium", "high" };
	private static final String[] S = { "out of service", "temporarily unavailable", "available" };

	// Public Methods
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mgr = (LocationManager) getSystemService(LOCATION_SERVICE);
        output = (TextView) findViewById(R.id.output);
        output.setMovementMethod(new ScrollingMovementMethod());
        
        Criteria criteria = new Criteria();
        best = mgr.getBestProvider(criteria, true);

        Location location = mgr.getLastKnownLocation(best);
        dumpLocation(location);
        
    }

	public void onLocationChanged(Location location) {
		dumpLocation(location);
		
	}

	public void onProviderDisabled(String provider) {
		
	}

	public void onProviderEnabled(String provider) {
		
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {

	}
	
	// Protected Methods
    
    @Override
    protected void onResume() {
    	super.onResume();
    	// Start updates (doc recommends delay >= 60000 ms)
    	// Delayed by 5 minutes and within 5km
    	// We only want to update when the user enters a new city
    	mgr.requestLocationUpdates(best, 300000, 5000, this);
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	// Stop updates to save power while app paused
    	mgr.removeUpdates(this);
    }
    
    // Private Methods
    
    // Write a string to the output window
    private void log(String string) {
    	//output.append(string + "\n");
    	Toast toast = Toast.makeText(getApplicationContext(), string, Toast.LENGTH_SHORT);
    	toast.show();
    }
    
    // Describe the given location, which might be null
    private void dumpLocation(Location location) {
    	if (location == null) {
    		log("Unable to determine city");
    	} else {
    		getCity(location);
    	}
    }
    
    private void getCity(final Location location) {
    	Geocoder geocoder = new Geocoder(getApplicationContext());
    	try {
    		List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);	
    		if (addresses.size() > 0) {
    			log("Entering " + addresses.get(0).getLocality());
    		} else {
    			log("Unable to determine city");
    		}
    	} catch (IOException e) {

    	} catch(IllegalArgumentException e) {

    	}
	}
}