package com.maps.photolocation;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

public class PlacesParser {
	
	public PlacesParser(){
		
	}
	
	public Point getPoint(String result, Context context) throws JSONException{
		Point point = new Point(context);
		JSONObject initialObject = new JSONObject(result);
		JSONObject place = initialObject.getJSONObject("result");
		/*try {
			place = new JSONObject(result);
		} catch (JSONException e1) {
		}*/
    	JSONObject locationObject = null;
		try {
			locationObject = initialObject.getJSONObject("result");
		} catch (JSONException e1) {
		}
    	
    	JSONArray typesValues = null;
    	try{
    		 typesValues = place.getJSONArray("types");
    	} catch (JSONException ex){
    	}
    	
    	if (typesValues != null){
            for (int j = 0; j < typesValues.length(); j++){
            	try {
					point.types.add(typesValues.getString(j));
				} catch (JSONException e) {
				}
            }
    	}
    	
    	try{
    		point.iconURL = new URL(place.getString("icon"));
    	} catch(JSONException ex){
    	} catch (MalformedURLException e) {
			e.printStackTrace();
		}
    	
    	try{
    		point.reference = place.getString("reference");
    	} catch(JSONException ex){
    	}
    	
    	try{
    		point.name = place.getString("name");
    	} catch(JSONException ex){
    	}
    	
    	try{
    		point.rating = place.getDouble("rating");
    	} catch (JSONException ex){
    		point.rating = -1;
    	}
    	
    	locationObject = locationObject.getJSONObject("geometry");
        JSONObject location = locationObject.getJSONObject("location");
        point.latLng = new LatLng(location.getDouble("lat"), location.getDouble("lng"));
		
		return point;
	}
	
	public List<Point> getPoints(List<String> results, Context context) throws JSONException{
		List<Point> points = new ArrayList<Point>();
		
		for (String result : results){
			JSONObject json = new JSONObject(result);
	        JSONArray resultsArray = json.getJSONArray("results");
	        for (int i = 0; i < resultsArray.length(); i++){
	        	Point point = new Point(context);
	        	JSONObject place = resultsArray.getJSONObject(i);
	        	JSONObject locationObject = resultsArray.getJSONObject(i);
	        	
	        	JSONArray typesValues = null;
	        	try{
	        		 typesValues = place.getJSONArray("types");
	        	} catch (JSONException ex){
	        	}
	        	if (typesValues != null){
		            for (int j = 0; j < typesValues.length(); j++){
		            	point.types.add(typesValues.getString(j));
		            }
	        	}
	        	
	        	try{
	        		point.iconURL = new URL(place.getString("icon"));
	        	} catch(JSONException ex){
	        	} catch (MalformedURLException e) {
					e.printStackTrace();
				}
	        	
	        	try{
	        		point.reference = place.getString("reference");
	        	} catch(JSONException ex){
	        	}
	        	
	        	try{
	        		point.name = place.getString("name");
	        	} catch(JSONException ex){
	        	}
	        	
	        	try{
	        		point.rating = place.getDouble("rating");
	        	} catch (JSONException ex){
	        		point.rating = -1;
	        	}
	        	
	        	locationObject = locationObject.getJSONObject("geometry");
	            JSONObject location = locationObject.getJSONObject("location");
	            point.latLng = new LatLng(location.getDouble("lat"), location.getDouble("lng"));
	            
	            points.add(point);
	        }
		}
		return points;
	}
	
}
