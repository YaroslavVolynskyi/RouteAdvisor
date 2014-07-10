package com.maps.photolocation;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.maps.markerclusters.MyClusterItem;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Point implements Parcelable{
	
	Location location;
	
	public LatLng latLng;
	public String name, reference;
	public URL iconURL;
	public List<String> types;
	public double rating;
	public Marker marker;
	Point point;
	public MarkerOptions markerOptions;
	
	public Point(Location location, Context context){
		this(context);
		this.location = location;
		latLng = new LatLng(location.getLatitude(), location.getLongitude());
	}
	
	public Point(LatLng latLng, Context context){
		this(context);
		this.latLng = latLng;
	}
	
	Context context;
	
	public Point(Context context){
		point = this;
		this.context = context;
		types = new ArrayList<String>();
	}
	
	public Bitmap icon;
	GoogleMap googleMap;
	
	public void drawPointMarker(GoogleMap googleMap, Context context){
		this.context = context;
		this.googleMap = googleMap;
		new drawMarkerAsyncTask().execute(iconURL);
	}
	
	@Override
	public String toString(){
		String summary = "";
		summary += "name = " + name + "\n";
		summary += "rating = " + rating + "\n";
		//summary += "lat = " + latLng.latitude + ", lng = " + latLng.longitude + "\n";
		//summary += "iconURL = " + iconURL + "\n";
		//summary += "types: " + "\n";
		//for (int i = 0; i < types.size(); i++){
			//summary += "\t" + types.get(i) + "\n";
		//}
		return summary;
	}
	
	private class drawMarkerAsyncTask extends AsyncTask<URL, Void, Bitmap>{

		@Override
	    protected void onPreExecute() {
	    }
	    
	    @Override
	    protected Bitmap doInBackground(URL... urls) {
	    	icon = null;
			try {
				icon = BitmapFactory.decodeStream(iconURL.openConnection().getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
			return icon;
	    }
	    
	    @Override
	    protected void onPostExecute(Bitmap result) {
	    	markerOptions = new MarkerOptions()
				.position(latLng)
				.alpha(0.8f)
				.flat(true)
				.icon(BitmapDescriptorFactory.fromBitmap(icon))
				.title(name);
	    	
	    	marker = googleMap.addMarker(markerOptions);
	    	
	    	/*if ((PhotoIntentActivity)context).markerOptionsList == null){
	    		((PhotoIntentActivity)context).markerOptionsList = new ArrayList<MarkerOptions>();
	    	}*/
	    	if (((PhotoIntentActivity)context).markerOptionsList == null){
	    		((PhotoIntentActivity)context).markerOptionsList = new ArrayList<MarkerOptions>();
	    	}
	    	((PhotoIntentActivity)context).markerOptionsList.add(markerOptions);
	    	//((PhotoIntentActivity)context).mClusterManager.cluster();*/
	    }
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		
		String[] params = new String[3 + types.size()];
		params[0] = name;
		params[1] = reference;
		params[2] = iconURL.toString();
		int i = 3;
		for (String s : types){
			params[i] = s;
			i++;
		}
		
		dest.writeStringArray(params);
		
		Bundle bundle = new Bundle();
		bundle.putParcelable(mOptKey, markerOptions);
		bundle.putParcelable(latLngKey, latLng);
		bundle.putDouble(ratingKey, rating);
		
		dest.writeBundle(bundle);
	}
	
	String mOptKey = "markerOptions", latLngKey = "latLng", ratingKey = "rating";
			
	public Point(Parcel parcel, Context context){
		this.context = context;
		
		String[] params = null;
		parcel.readStringArray(params);
		this.name = params[0];
		this.reference = params[1];
		/*try {
			this.iconURL = new URL(params[2]);
		} catch (MalformedURLException e) {
		}*/
		
		types = new ArrayList<String>();
		for (int i = 2; i < params.length; i++){
			types.add(params[i]);
		}
		
		Bundle b = parcel.readBundle();
		markerOptions = (MarkerOptions) b.get(mOptKey);
		latLng = (LatLng) b.get(latLngKey);
		rating = (double) b.getDouble(ratingKey);
	}
}






