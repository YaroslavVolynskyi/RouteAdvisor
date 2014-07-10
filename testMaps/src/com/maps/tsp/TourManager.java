package com.maps.tsp;

import java.util.ArrayList;
import java.util.List;

import com.maps.photolocation.*;

public class TourManager {

    // Holds our cities
    public static List<Point> destinationPoints = new ArrayList<Point>();

    // Adds a destination city
    public static void addCity(Point point) {
        destinationPoints.add(point);
    }
    
    // Get a city
    public static Point getPoint(int index){
        return (Point)destinationPoints.get(index);
    }
    
    // Get the number of destination cities
    public static int numberOfCities(){
        return destinationPoints.size();// GA.citiesToSubtract;
    }
}