package com.maps.photolocation;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.ExpandableListView;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;


public class Places {

	Context context;
	PhotoIntentActivity activity;
	LatLng startPoint;
	public int radius;
	
	List<String> results;
	int pages;
	String nextPageToken;
	public List<Point> points;
	ArrayList<String> types;
	
	ArrayList<MarkerOptions> markOptionsList;
	
	public Places(PhotoIntentActivity context, LatLng point, ArrayList<String> types){
		this.activity = context;
		this.context = (Context) context;
		this.startPoint = point;
		results = new ArrayList<String>();
		pages = 0;
		this.types = types;
		
		SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
	    int defaultValue = 5000;
		radius = sharedPref.getInt(activity.getString(R.string.current_radius), defaultValue); 
	}
	
	
	public Handler handler = new Handler();
	public int delay = 500;
	
	private class pageGetter implements Runnable{
		String url;
		
		pageGetter(String nextPageToken){ 
			this.url = makePlacesURL(nextPageToken);
		}
		
		@Override
		public void run() {
			Log.e("url_res", "url " + pages + ":\n" + url);
			new getPlacesAsyncTask().execute(url);
		}
	}
	
	
	private ProgressDialog progressDialog;
	
	public void getAllPlaces(String nextPageToken){
		
		progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Searching places, Please wait...");
        progressDialog.setIndeterminate(true);
        progressDialog.show();
		
        markOptionsList = new ArrayList<MarkerOptions>();
        ((PhotoIntentActivity)context).markerOptionsList = new ArrayList<MarkerOptions>();
        
        handler.postDelayed((new pageGetter(nextPageToken)), 0);
        
        //progressDialog.hide();
	}
	
	private void handlePlacesJSON(List<String> results){
    	PlacesParser placesParser = new PlacesParser();
    	try {
			points = placesParser.getPoints(results, context);
		} catch (JSONException e) {
			e.printStackTrace();
		}
    }
	
	public String makePlacesURL(String nextPageToken){
		StringBuilder url = new StringBuilder();
		
		url.append("https://maps.googleapis.com/maps/api/place/search/json?");
		
		if (nextPageToken == null){
			try{
				if (types.size() >= 1){
					url.append("types=");
					String typesString = "";
					for (int i = 0; i < types.size(); i++){
						typesString += types.get(i);
						
						if (i < types.size() - 1){
							typesString += "|";
						} 
					}
					url.append(URLEncoder.encode(typesString, "UTF-8"));
					url.append("&");
				}
				
				url.append("location=");
				url.append(URLEncoder.encode(String.valueOf(startPoint.latitude), "UTF-8"));
				url.append(",");
				url.append(URLEncoder.encode(String.valueOf(startPoint.longitude), "UTF-8"));
				url.append("&radius=");
				url.append(URLEncoder.encode(String.valueOf(radius), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
		        e.printStackTrace();
		    }
		}
		if (nextPageToken != null){
			if (nextPageToken.length() > 0){
				//url.append("&pagetoken=");
				
				try {
					url.append("&pagetoken=");
					url.append(URLEncoder.encode(nextPageToken, "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
		
		try {
			url.append("&sensor=false&key=");
			//url.append("@string/server_key");
			url.append(URLEncoder.encode("AIzaSyBKJD8p-cFipXucUy3XyDRXePkJhvRaI2Q", "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		
		Log.d("wtf", url.toString());
		return url.toString();
	}
	
	private class getPlacesAsyncTask extends AsyncTask<String, Void, String>{
		
	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	    }
	    
	    @Override
	    protected String doInBackground(String... urls) {
	        /*JSONParser jParser = new JSONParser();
	        String json = jParser.getJSONFromUrl(url);
	        return json;*/
	    	return JSONParser.getJSON(urls[0]);
	    }
	    
	    @Override
	    protected void onPostExecute(String result) {
	        super.onPostExecute(result);   
	        
	        points = null;
	        Log.e("url_res", "result " + pages + ":\n" + result);
	        nextPageToken = new String();
	        try{
	        	JSONObject json = new JSONObject(result);
		    	nextPageToken = json.getString("next_page_token");
	        } catch(JSONException ex){
	        	Log.e("Places", "On post execute: json exception: " + ex.getMessage());
	        }
	        	        
	        results.add(result);
	        if (nextPageToken.equals("") || pages >= 15){
	        	handlePlacesJSON(results);
	        	progressDialog.hide();
	        	
	        	drawPoints(points);
	        	showPlaces(points, activity);
	        	//calculateDistances(points);
	        	
	        } else {
	        	pages++;
	        	handler.postDelayed(new pageGetter(nextPageToken), delay);
	        }
	    }
	    
	    private void calculateDistances(List<Point> points){
	    	DistanceMatrix distMatrix = new DistanceMatrix((PhotoIntentActivity)context);
    		if (points != null){
    			distMatrix.getDistances(points, "walking");
    		}
	    }
	    
	    private void showPlaces(List<Point> points, PhotoIntentActivity activity){
	    	List<Group> groups = new ArrayList<Group>();
	    	for (Point point : points){
	    		groups.add(new Group(point));
	    		activity.currentPoints.add(point);
	    	}
	    	
		    ExpandableListView listView = (ExpandableListView) activity.findViewById(R.id.contentExpandableListViewMain);
		    
		    //MyExpandableListAdapter adapter = new MyExpandableListAdapter(activity, groups);
		    activity.expandableListAdapter.addGroups(groups);
		    activity.expandableListAdapter.notifyDataSetChanged();
		    //listView.setAdapter(activity.expandableListAdapter);
		    
		    activity.slidingDrawer.open();
	    }
	    
	    private void drawPoints(List<Point> points){
	    	for (Point point : points){
    	    	point.drawPointMarker(activity.googleMap, context);
        	}
	    }
	    
	    private void showJSON(String result){
	    	activity.searchingField.setText(result);
	    }
	}
 
}
