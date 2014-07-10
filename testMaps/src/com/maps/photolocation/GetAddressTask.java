package com.maps.photolocation;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.maps.locationsecond.LocationUtils;

/**
 * An AsyncTask that calls getFromLocation() in the background.
 * The class uses the following generic types:
 * Location - A {@link android.location.Location} object containing the current location,
 *            passed as the input parameter to doInBackground()
 * Void     - indicates that progress units are not used by this subclass
 * String   - An address passed to onPostExecute()
 */
public class GetAddressTask extends AsyncTask<PhotoIntentActivity, Void, String> {

    // Store the context passed to the AsyncTask when the system instantiates it.
    Context localContext;

    PhotoIntentActivity activity;
    // Constructor called by the system to instantiate the task
    public GetAddressTask(PhotoIntentActivity context) {

        // Required by the semantics of AsyncTask
        super();

        // Set a Context for the background task
        localContext = context;
        activity = context;
    }

    /**
     * Get a geocoding service instance, pass latitude and longitude to it, format the returned
     * address, and return the address to the UI thread.
     */
    @Override
    protected String doInBackground(PhotoIntentActivity... params) {
        /*
         * Get a new geocoding service instance, set for localized addresses. This example uses
         * android.location.Geocoder, but other geocoders that conform to address standards
         * can also be used.
         */
    	//PhotoIntentActivity activity = params[0];
    	
        Geocoder geocoder = new Geocoder(localContext, Locale.getDefault());

        // Get the current location from the input parameter list
        Location location = activity.getLocation();

        // Create a list to contain the result address
        List <Address> addresses = null;

        // Try to get an address for the current location. Catch IO or network problems.
        try {

            /*
             * Call the synchronous getFromLocation() method with the latitude and
             * longitude of the current location. Return at most 1 address.
             */
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            // Catch network or other I/O problems.
            } catch (IOException exception1) {

                // Log an error and return an error message
                Log.e(LocationUtils.APPTAG, localContext.getString(R.string.IO_Exception_getFromLocation));

                // print the stack trace
                exception1.printStackTrace();

                // Return an error message
                return (localContext.getString(R.string.IO_Exception_getFromLocation));

            // Catch incorrect latitude or longitude values
            } catch (IllegalArgumentException exception2) {

                // Construct a message containing the invalid arguments
                String errorString = localContext.getString(
                        R.string.illegal_argument_exception,
                        location.getLatitude(),
                        location.getLongitude()
                );
                // Log the error and print the stack trace
                Log.e(LocationUtils.APPTAG, errorString);
                exception2.printStackTrace();

                //
                return errorString;
            }
            // If the reverse geocode returned an address
            if (addresses != null && addresses.size() > 0) {

                // Get the first address
                Address address = addresses.get(0);

                // Format the first line of address
                String addressText = localContext.getString(R.string.address_output_string,
                        address.getMaxAddressLineIndex() > 0 ?
                                /*address.getAddressLine(0)*/"" : "",
                        address.getLocality(),
                        address.getCountryName()
                );

                // Return the text
                return addressText;

            // If there aren't any addresses, post a message
            } else {
              return localContext.getString(R.string.no_address_found);
            }
    }

    /**
     * A method that's called once doInBackground() completes. Set the text of the
     * UI element that displays the address. This method runs on the UI thread.
     */
    @Override
    protected void onPostExecute(String address){
    	activity.searchingField.setText(address);
    	String[] parts = address.split(" ");
    	//activity.search(parts[0]);
    }
}