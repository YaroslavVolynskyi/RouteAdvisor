package com.maps.tsp;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.ads.a;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.maps.photolocation.*;

public class GA_Manager {

	List<Point> points;
	public static PhotoIntentActivity activity;
	
	ProgressDialog progressDialog;
	
	public void getBestTour(List<Point> points, PhotoIntentActivity myActivity){
		activity = myActivity;
		
		progressDialog = new ProgressDialog(activity);
		progressDialog.setMessage("Searching best route, Please wait...");
		progressDialog.setIndeterminate(true);
		progressDialog.show();
		
		Population pop = new Population(50, true);
        
        Tour firstBest = pop.tours[0];
        Tour first = pop.tours[1];
        
        GA.firstSum = Population.round((1 - (double)firstBest.getDistance() / (double)first.getDistance()) * 100, 2) +
				Population.round((1 - (double)first.getRating() / (double)firstBest.getRating()) * 100, 2);
        GA.firstBestTour = firstBest;
        
        /*Tour best1 = pop.getBest();
        Tour best;*/
		
		new getBestTourAsyncTask(points).execute();
	}
	
	private class getBestTourAsyncTask extends AsyncTask<Void, Void, List<Point>>{
		List<Point> points;
		
		public getBestTourAsyncTask(List<Point> points) {
			this.points = points;
		}

		@Override
		protected List<Point> doInBackground(Void... params) {
			double rating1, rating2; 
			int distance1, distance2, k = 10;
			
			double okPercent = 0;
			
			List<Tour> tours = new ArrayList<Tour>();
			
			Population pop = new Population(50, true);
			
			for (int j = 0; j < k; j++){
				TourManager.destinationPoints = points;
			
		        // Initialize population
		        pop = new Population(50, true);
		        
		        
		        distance1 = pop.getBest().getDistance();
		        rating1 = pop.getBest().getRating();
		        
		        // Evolve population for 100 generations
		        pop = GA.evolvePopulation(pop);
		        
		        for (int i = 0; i < 100; i++) {
		            pop = GA.evolvePopulation(pop);
		        }
		
		        
		        distance2 = pop.getBest().getDistance();
		        rating2 = pop.getBest().getRating();

		        Log.d("qwerty", "d1 = " + distance1 + ",  d2 = " + distance2);
		        Log.d("qwerty", "r1 = " + rating1 + ",   r2 = " + rating2);
		        
		        if (distance2 < distance1 && rating2 > rating1){
		        	okPercent++;
		        	tours.add(pop.getBest());
		        }
		        
			}
			
			//okPercent = okPercent / k * 100;
			System.out.println("ok = " + okPercent + " %");
			
			Tour bestTour = null;
			/*if (tours.size() > 0){
				bestTour = GA.getBestTour(tours, 0);
			} else {
				bestTour = pop.getBest();
			}*/
			bestTour = pop.getBest();
			System.out.println("Best Tour");
			System.out.println("\trating = " + bestTour.getRating() + "\n\tdistance = " + bestTour.getDistance());
			
			return bestTour.tour;
		}
		
		@Override
		protected void onPostExecute(List<Point> result) {
			super.onPostExecute(result);
			activity.expandableListAdapter.groups = new ArrayList<Group>();
			
			List<Group> groups = new ArrayList<Group>();
	    	for (Point p : result){
	    		groups.add(new Group(p));
	    	}
			activity.expandableListAdapter.addGroups(groups);
			
			for (int i = 0; i < activity.oldPoints.size(); i++){
				if (!result.contains(activity.oldPoints.get(i))){
					//removeFromOptions(activity.oldPoints.get(i).marker);
					
					activity.oldPoints.get(i).marker.remove();
				}
			}
			
			activity.expandableListAdapter.notifyDataSetChanged();
			
			progressDialog.hide();
			activity.currentPoints = result;
			
			Route route = new Route(activity);
			route.drawRoute(activity.googleMap, activity, result, "walking", true, "English", true);
		}
		
		private void removeFromOptions(Marker marker){
			for (int i = 0; i < activity.markerOptionsList.size(); i++){
				/*if (activity.markerOptionsList.get(i).getPosition().latitude == marker.getPosition().latitude
				    && activity.markerOptionsList.get(i).getPosition().longitude == marker.getPosition().longitude){*/
				if (activity.markerOptionsList.get(i).getPosition().equals(marker.getPosition())){
					activity.markerOptionsList.remove(i);
				}
			}
		}
	}
	
}
