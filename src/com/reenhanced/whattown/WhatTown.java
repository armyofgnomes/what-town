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
import android.location.LocationProvider;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

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
        
        log("Location providers:");
        dumpProviders();
        
        Criteria criteria = new Criteria();
        best = mgr.getBestProvider(criteria, true);
        log("\nBest provider is: " + best);
        
        log("\nLocations (starting with last known):");
        Location location = mgr.getLastKnownLocation(best);
        dumpLocation(location);
        
    }

	public void onLocationChanged(Location location) {
		dumpLocation(location);
		
	}

	public void onProviderDisabled(String provider) {
		log("\nProvider disabled: " + provider);
		
	}

	public void onProviderEnabled(String provider) {
		log("\nProvider enabled: " + provider);
		
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		log("\nProvider status changed: " + provider + ", status=" + S[status] + ", extras=" + extras);
		
	}
	
	// Protected Methods
    
    @Override
    protected void onResume() {
    	super.onResume();
    	// Start updates (doc recommends delay >= 60000 ms)
    	mgr.requestLocationUpdates(best, 15000, 1, this);
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
    	output.append(string + "\n");
    }
    
    // Write information from all location providers
    private void dumpProviders() {
    	List<String> providers = mgr.getAllProviders();
    	for (String provider : providers) {
    		dumpProvider(provider);
    	}
    }
    
    // Write information from a single location provider
    private void dumpProvider(String provider) {
    	LocationProvider info = mgr.getProvider(provider); 
    	StringBuilder builder = new StringBuilder(); 
    	builder.append("LocationProvider[" )
	    	.append("name=" ) 
	    	.append(info.getName()) 
	    	.append(",enabled=" ) 
	    	.append(mgr.isProviderEnabled(provider)) 
	    	.append(",getAccuracy=" ) 
	    	.append(A[info.getAccuracy() + 1]) 
	    	.append(",getPowerRequirement=" ) 
	    	.append(P[info.getPowerRequirement() + 1]) 
	    	.append(",hasMonetaryCost=" ) 
	    	.append(info.hasMonetaryCost()) 
	    	.append(",requiresCell=" ) 
	    	.append(info.requiresCell()) 
	    	.append(",requiresNetwork=" ) 
	    	.append(info.requiresNetwork()) 
	    	.append(",requiresSatellite=" ) 
	    	.append(info.requiresSatellite()) 
	    	.append(",supportsAltitude=" ) 
	    	.append(info.supportsAltitude()) 
	    	.append(",supportsBearing=" ) 
	    	.append(info.supportsBearing()) 
	    	.append(",supportsSpeed=" ) 
	    	.append(info.supportsSpeed())
	    	.append("]" ); 
    	log(builder.toString());
    }
    
    // Describe the given location, which might be null
    private void dumpLocation(Location location) {
    	if (location == null) {
    		log("\nLocation[unknown]");
    	} else {
    		log("\n" + location.toString() + "\nCity=" + getCity(location));
    	}    	
    }
    
    private String getCity(Location location) {
    	Geocoder geocoder = new Geocoder(getApplicationContext());
    	try {
    		List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);	
    		if (addresses.size() > 0) {
    			return addresses.get(0).getLocality();
    		} else {
    			return "";
    		}
    	} catch (IOException e) {
    		return "";
    	} catch(IllegalArgumentException e) {
    		return "";
    	}
	}
}