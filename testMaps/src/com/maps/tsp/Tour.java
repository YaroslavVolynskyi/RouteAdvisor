package com.maps.tsp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.maps.photolocation.*;

public class Tour{

    // Holds our tour of cities
    public List<Point> tour = new ArrayList<Point>();
    // Cache
    private double fitness = 0;
    private int distance = 0;
    
    int tourCitiesAmount;
    
    // Constructs a blank tour
    public Tour(){
    	SharedPreferences sharedPref = GA_Manager.activity.getPreferences(Context.MODE_PRIVATE);
    	int defaultValue = 5;
    	GA.minimumCitiesAmount = sharedPref.getInt(GA_Manager.activity.getString(R.string.max_cities), defaultValue);
    	
    	
    	if (GA.minimumCitiesAmount > TourManager.numberOfCities()){
    		tourCitiesAmount = TourManager.numberOfCities() / 2;
    	} else {
    		tourCitiesAmount = GA.minimumCitiesAmount;
    	}
        for (int i = 0; i < tourCitiesAmount; i++) {
            tour.add(null);
        }
    }
    
    public Tour(List<Point> tour){
        this.tour = tour;
    }

    // Creates a random individual
    public void generateIndividual() {
    	
    	//int n = 100;
    	List<Integer> indexes = new ArrayList<Integer>();
    	for (int i = 0; i < TourManager.numberOfCities(); i++){
    		indexes.add(i);
    	}
    	Collections.shuffle(indexes);
        // Loop through all our destination cities and add them to our tour
        for (int cityIndex = 0; cityIndex < tourCitiesAmount; cityIndex++) {
        	setPoint(cityIndex, TourManager.getPoint(indexes.get(cityIndex)));
        }
        // Randomly reorder the tour
        Collections.shuffle(tour);
    }

    // Gets a city from the tour
    public Point getPoint(int tourPosition) {
        return (Point)tour.get(tourPosition);
    }

    // Sets a city in a certain position within a tour
    public void setPoint(int tourPosition, Point point) {
        tour.set(tourPosition, point);
        // If the tours been altered we need to reset the fitness and distance
        fitness = 0;
        distance = 0;
    }
    
    // Gets the tours fitness
    public double getFitness() {
        if (fitness == 0) {
            fitness = 1/(double)getDistance();
        }
        return fitness;
    }
    
    /*public int getPrice(){
    	int price = 0;
    	for (Point p : tour){
    		price += p.price;
    	}
    	return price;
    }*/
    
    public double getRating(){
    	double rating = 0;
    	for (Point p : tour){
    		if (p.rating >= 0){
    			rating += p.rating;
    		}
    	}
    	return rating;
    }
    
    // Gets the total distance of the tour
    public int getDistance(){
        if (distance == 0) {
            int tourDistance = 0;
            
            float[] results = new float[3];
            // Loop through our tour's cities
            for (int cityIndex=0; cityIndex < tourSize(); cityIndex++) {
                // Get city we're travelling from
                Point fromPoint = getPoint(cityIndex);
                // City we're travelling to
                Point destinationPoint;
                // Check we're not on our tour's last city, if we are set our 
                // tour's final destination city to our starting city
                if(cityIndex + 1 < tourSize()){
                    destinationPoint = getPoint(cityIndex + 1);
                }
                else{
                    destinationPoint = getPoint(0);
                }
                // Get the distance between the two cities
                Location.distanceBetween(fromPoint.latLng.latitude, fromPoint.latLng.longitude, destinationPoint.latLng.latitude, destinationPoint.latLng.longitude, results);
                tourDistance += results[0];
            }
            distance = tourDistance;
        }
        return distance;
    }

    // Get number of cities on our tour
    public int tourSize() {
        return tour.size();
    }
    
    // Check if the tour contains a city
    public boolean containsPoint(Point point){
        return tour.contains(point);
    }
    
    @Override
    public String toString() {
        String tourString = "|";
        for (int i = 0; i < tourSize(); i++) {
            tourString += getPoint(i)+"|";
        }
        return tourString;
    }
}