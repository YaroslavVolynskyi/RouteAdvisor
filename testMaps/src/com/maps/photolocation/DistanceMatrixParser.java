package com.maps.photolocation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class DistanceMatrixParser {
	
	PhotoIntentActivity activity;
	
	public DistanceMatrixParser(PhotoIntentActivity activity){
		this.activity = activity;
	}
	
	public double[][] distances, durations;
	
	public void getDistancesAndDurations(String result, int size){
		/*durations = getDistDur(result, size, "duration");
		distances = getDistDur(result, size, "distance");*/
		activity.distances = getDistDur(result, size, "distance");
		activity.durations = getDistDur(result, size, "distance");
		
		Log.d("distParser", "ok");
	}
	
	private double[][] getDistDur(String result, int size, String distanceOrDuration){
		double[][] values = new double[size][size];
		JSONObject initialObject = null;
		try {
			initialObject = new JSONObject(result);
			JSONArray rows = initialObject.getJSONArray("rows");
			JSONArray elements;
			JSONObject distance;
			for (int i = 0; i < rows.length(); i++){
				distance = rows.getJSONObject(i);
				elements = distance.getJSONArray("elements");
				for (int j = 0; j < elements.length(); j++){
					distance = elements.getJSONObject(j);
					distance = distance.getJSONObject(distanceOrDuration);
					values[i][j] = Double.parseDouble(distance.getString("value"));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return values;
	}
}
