package com.maps.tsp;

public class Population {

    // Holds population of tours
    Tour[] tours;

    // Construct a population
    public Population(int populationSize, boolean initialise) {
        tours = new Tour[populationSize];
        // If we need to initialise a population of tours do so
        if (initialise) {
            // Loop and create individuals
            for (int i = 0; i < populationSize; i++) {
                Tour newTour = new Tour();
                newTour.generateIndividual();
                saveTour(i, newTour);
            }
        }
    }
    
    // Saves a tour
    public void saveTour(int index, Tour tour) {
        tours[index] = tour;
    }
    
    // Gets a tour from population
    public Tour getTour(int index) {
        return tours[index];
    }
    
    public Tour getBest(){

    	Tour best = tours[0];
    	
    	double sum = 0;
    	double bestSum = GA.firstSum;
    	
    	for (Tour tour : tours){
    		sum = round((- (double)tour.getDistance() / (double)GA.firstBestTour.getDistance()) * 100, 2) +
    				round(((double)tour.getRating() / (double)3) * 100, 2);
    		if (sum > bestSum){
    			best = tour;
    			bestSum = sum;
    		}
    	}
    	
    	return best;
    }
    
    public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (double) tmp / factor;
	}
    
    public Tour getMostRated(){
    	Tour mostRated = tours[0];
    	for (Tour tour : tours){
    		if (tour.getRating() >= mostRated.getRating()){
    			mostRated = tour;
    		}
    	}
    	return mostRated;
    }
    
    // Gets the best tour in the population
    public Tour getFittest() {
        Tour fittest = tours[0];
        // Loop through individuals to find fittest
        for (int i = 1; i < tours.length; i++) {
            if (fittest.getFitness() <= getTour(i).getFitness()) {
                fittest = getTour(i);
            }
        }
        return fittest;
    }

    // Gets population size
    public int populationSize() {
        return tours.length;
    }
}