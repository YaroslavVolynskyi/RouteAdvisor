package com.maps.photolocation;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.Html;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.ui.IconGenerator;
import com.maps.markerclusters.MyClusterItem;
import com.maps.markerclusters.Step;

public class Route {

	GoogleMap mMap;
	Context context;
	String lang;

	static String LANGUAGE_ENGLISH = "en";
		
	Random random;
	
	public Route(Context context){
		this.context = context;
		Resources resources = context.getResources();
		colors = resources.getStringArray(R.array.colors_array);
		hue_colors = resources.getStringArray(R.array.hue_colors_array);
	}
	
	public Handler handler = new Handler();

	private class routeDrawer implements Runnable {

		List<Point> points;
		String mode;
		boolean withIndications;
		int currentPointIndex;
		
		routeDrawer(List<Point> points, int currentPointIndex, String mode, boolean withIndications){
			this.points = points;
			this.currentPointIndex = currentPointIndex;
			this.mode = mode;
			this.withIndications = withIndications;
		}
		
		@Override
		public void run() {
			String url = makeUrl(points.get(currentPointIndex), points.get(currentPointIndex + 1), mode);
			new drawRouteAsyncTask(url, withIndications, points, mode).execute();
		}
	}

	int delay = 0;
	int currentPointIndex;
	private ProgressDialog progressDialog;
	String[] colors;
	String[] hue_colors;
	
	ArrayList<PolylineOptions> polylineOptionsList;
	
	public boolean drawRoute(GoogleMap map, Context context, List<Point> points, String mode, boolean withIndications,
			String language, boolean optimize){
		
		polylineOptionsList = new ArrayList<PolylineOptions>();
		
		progressDialog = new ProgressDialog(context);
		progressDialog.setMessage("Drawing route, Please wait...");
		progressDialog.setIndeterminate(true);
		progressDialog.show();
		
		((PhotoIntentActivity)context).stepsToSave = new ArrayList<Step>();
		
		markerCount = 1;
		
		random = new Random();
		
		this.mMap = map;
		this.lang = language;
		this.context = context;
		currentPointIndex = 0;
		handler.postDelayed(new routeDrawer(points, currentPointIndex, mode, withIndications), delay);
		
		return false;
	}
	
	public String makeUrl(Point source, Point dest, String mode) {
		StringBuilder urlString = new StringBuilder();

		if (mode == null)
			mode = "walking";

		urlString.append("http://maps.googleapis.com/maps/api/directions/json");
		urlString.append("?origin=");// from
		urlString.append(source.latLng.latitude + "," + source.latLng.longitude);
		urlString.append("&destination=");// to
		urlString.append(dest.latLng.latitude + "," + dest.latLng.longitude);
		urlString.append("&sensor=false" + "&mode=" + mode + "&alternatives=true" + "&language=" + lang);
		return urlString.toString();
	}

	private List<LatLng> decodePoly(String encoded) {

		List<LatLng> poly = new ArrayList<LatLng>();
		int index = 0, len = encoded.length();
		int lat = 0, lng = 0;

		while (index < len) {
			int b, shift = 0, result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lat += dlat;

			shift = 0;
			result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dlng;

			LatLng p = new LatLng((((double) lat / 1E5)),
					(((double) lng / 1E5)));
			poly.add(p);
		}

		return poly;
	}

	public class drawRouteAsyncTask extends AsyncTask<Void, Void, String> {
		
		String url, mode;
		boolean withIndications;
		List<Point> points;

		drawRouteAsyncTask(String urlPass, boolean withIndications, List<Point> points, String mode){
			this.points = points;
			url = urlPass;
			this.withIndications = withIndications;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params) {
			JSONParser jParser = new JSONParser();
			String json = jParser.getJSON(url);
			Log.d("Route", url);
			return json;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			
			if (result != null) {
				drawPath(result, withIndications);
			}
			
			if (currentPointIndex < points.size() - 2){
				currentPointIndex++;
				handler.postDelayed(new routeDrawer(points, currentPointIndex, mode, withIndications), delay);
			} else {
				progressDialog.hide();
				((PhotoIntentActivity)context).polylineOptionsList = polylineOptionsList;
				markerCount = 1;
				((PhotoIntentActivity)context).mClusterManager.cluster();
			}
		}
	}
	
	int markerCount;

	private void drawPath(String result, boolean withSteps) {
		try {
			final JSONObject json = new JSONObject(result);
			JSONArray routeArray = json.getJSONArray("routes");
			JSONObject routes = routeArray.getJSONObject(0);
			JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
			String encodedString = overviewPolylines.getString("points");
			List<LatLng> list = decodePoly(encodedString);
			
			String currentColor = colors[random.nextInt(colors.length - 1)];
			String currentHueColor = hue_colors[random.nextInt(hue_colors.length - 1)];
			
			for (int z = 0; z < list.size() - 1; z++) {
				LatLng src = list.get(z);
				LatLng dest = list.get(z + 1);
				
				PolylineOptions polOptions = new PolylineOptions()
					.add(new LatLng(src.latitude, src.longitude), new LatLng(dest.latitude, dest.longitude))
					.width(8)
					.color(Color.parseColor(currentColor))
					.geodesic(true);
				
				Polyline line = mMap.addPolyline(polOptions);
				
				polylineOptionsList.add(polOptions);
			}

			if (withSteps) {
				JSONArray arrayLegs = routes.getJSONArray("legs");
				JSONObject legs = arrayLegs.getJSONObject(0);
				JSONArray stepsArray = legs.getJSONArray("steps");
				IconGenerator iconFactory = new IconGenerator(context);
				// put initial point
				for (int i = 0; i < stepsArray.length(); i++) {
					
					Step step = new Step(stepsArray.getJSONObject(i));
					((PhotoIntentActivity)context).stepsToSave.add(step);
					/*mMap.addMarker(new MarkerOptions()
							.position(step.location)
							.title(step.distance)
							.snippet(step.instructions)
							.icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(String.valueOf(markerCount)))));*/
					
					
							//.icon(BitmapDescriptorFactory.defaultMarker(Float.parseFloat(currentHueColor))));
					((PhotoIntentActivity)context).mClusterManager.addItem(new MyClusterItem(step, markerCount));
					markerCount++;
				}
			}

		} catch (JSONException e) {
			Log.e("Route", e.getMessage());
		}
	}

}
