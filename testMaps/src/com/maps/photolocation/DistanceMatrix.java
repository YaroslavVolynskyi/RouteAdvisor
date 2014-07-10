package com.maps.photolocation;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.internal.di;

import android.app.ProgressDialog;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

public class DistanceMatrix {
	
	PhotoIntentActivity activity;
	
	public DistanceMatrix(PhotoIntentActivity activity){
		this.activity = activity;
	}
	
	ProgressDialog progressDialog;
	//int delay = 10000;
	int delay = 0;
	
	int currentIndex;
	String bigResult;
	int maxElements = 10;
	
	/*private void getAproximateDistances(List<Point> points){
		
	}*/
	
	public void getDistances(List<Point> points, String mode){
		progressDialog = new ProgressDialog(activity);
		progressDialog.setMessage("Getting distances, Please wait...");
		progressDialog.setIndeterminate(true);
		progressDialog.show();
		
		currentIndex = 0;
		bigResult = "";
		
		//if (points.size() <= maxElements){
			//handler.postDelayed(new distancesGetter(points, mode, currentIndex), 0);
		//} else {
			//getAproximateDistances(points);
			new getAproximateDistances(points).execute();
		//}
	}
	
	public Handler handler = new Handler();
	
	private class distancesGetter implements Runnable{

		List<Point> points;
		String mode;
		int currentIndex;
		
		public distancesGetter(List<Point> points, String mode, int currentIndex) {
			this.points = points;
			this.mode = mode;
			this.currentIndex = currentIndex;
		}
		
		@Override 
		public void run(){
			String url = makeUrl(points, mode, currentIndex);
			new getDistancesAsyncTask(url, points, currentIndex, mode).execute();
		}
	}
	
	public String makeUrl(List<Point> points, String mode, int currentIndex){
		StringBuilder url = new StringBuilder();
		
		/*List<Point> currentPoints = new ArrayList<Point>();
		for (int i = currentIndex; i < currentIndex + maxElements; i++){
			currentPoints.add(points.get(currentIndex));
		}*/
		
		try {
			url.append("https://maps.googleapis.com/maps/api/distancematrix/json?origins=");
			
			StringBuilder origins = new StringBuilder();
			//StringBuilder destinations = new StringBuilder();
			for(int i = 0; i < points.size(); i++){
				origins.append(points.get(i).latLng.latitude + "," + points.get(i).latLng.longitude);
				if (i < points.size() - 1){
					origins.append("|");
				} 
			}
			
			url.append(URLEncoder.encode(origins.toString(), "UTF-8"));
			url.append("&destinations=");
			//url.append(URLEncoder.encode(destinations.toString(), "UTF-8"));
			url.append(URLEncoder.encode(origins.toString(), "UTF-8"));
			
			url.append("&mode=");
			url.append(URLEncoder.encode(mode, "UTF-8"));
			url.append("&language=");
			url.append(URLEncoder.encode(Route.LANGUAGE_ENGLISH, "UTF-8"));
			url.append("&sensor=false&key=");
			//url.append("@string/server_key");
			url.append(URLEncoder.encode("AIzaSyBKJD8p-cFipXucUy3XyDRXePkJhvRaI2Q", "UTF-8"));
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return url.toString();
	}
	
	private class getAproximateDistances extends AsyncTask<Void, Void, Void>{

		List<Point> points;
		
		getAproximateDistances(List<Point> points) {
			this.points = points;
		}
		
		@Override
		protected Void doInBackground(Void... arg0) {
			float[] results = new float[3];
			double[][] distances = new double[points.size()][points.size()];
			double[][] durations = new double[points.size()][points.size()];
			double velocity = 10;
			
			for (int start = 0; start < points.size(); start++){
				for (int end = 0; end < points.size(); end++){
					Location.distanceBetween(points.get(start).latLng.latitude, points.get(start).latLng.longitude, points.get(end).latLng.latitude, points.get(end).latLng.longitude, results);
					distances[start][end] = results[0];
					if (results[0] != 0){
						durations[start][end] = results[0] / velocity;
					}
				}
			}
			
			activity.distances = distances;
			activity.durations = durations;
			
			Log.d("","");
			return null;
		}
		
		@Override 
		protected void onPostExecute(Void result){
			progressDialog.hide();
		}
	}
	
	private class getDistancesAsyncTask extends AsyncTask<Void, Void, String>{
		
		String url, mode;
		int size, currentIndex;
		List<Point> points;
		
		getDistancesAsyncTask(String url, List<Point> points, int currentIndex, String mode){
			this.url = url;
			this.mode = mode;
			this.size = points.size();
			this.points = points;
			this.currentIndex = currentIndex;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params) {
			JSONParser jParser = new JSONParser();
			String json = jParser.getJSON(url);
			Log.d("DistanceMatrix", url);
			return json;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			
			DistanceMatrixParser distMatrParser = new DistanceMatrixParser(activity);
			distMatrParser.getDistancesAndDurations(result, size);
			progressDialog.hide();
			
			/*if (currentIndex > points.size() - 1){
				Log.d("DistanceMatrixResult", bigResult);
				DistanceMatrixParser distMatrParser = new DistanceMatrixParser();
				distMatrParser.getDistancesAndDurations(bigResult, size);
			} else {
				bigResult += result;
				currentIndex += maxElements;
				handler.postDelayed(new distancesGetter(points, mode, currentIndex), delay);
			}
			progressDialog.hide();*/
		}
	}
}



















