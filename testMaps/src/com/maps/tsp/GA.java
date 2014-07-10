package com.maps.tsp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.maps.photolocation.*;

public class GA {

    /* GA parameters */
    private static final double mutationRate = 0.04;
    private static final int tournamentSize = 5;
    private static final boolean elitism = true;
    
    public static int minimumCitiesAmount;
    
    public static int citiesToSubtract = 3;
    
    public static double firstSum;
    public static Tour firstBestTour;

    // Evolves a population over one generation
    public static Population evolvePopulation(Population pop) {
        Population newPopulation = new Population(pop.populationSize(), false);

        // Keep our best individual if elitism is enabled
        int elitismOffset = 0;
        if (elitism) {
            newPopulation.saveTour(0, pop.getFittest());
            elitismOffset = 1;
        }

        // Crossover population
        // Loop over the new population's size and create individuals from
        // Current population
        boolean fittest = true;
        for (int i = elitismOffset; i < newPopulation.populationSize(); i++) {
            // Select parents
            Tour parent1 = tournamentSelection(pop, 0); // best distance
            Tour parent2 = tournamentSelection(pop, 1); // best rating
            //Tour parent3 = tournamentSelection(pop, 2); // best price

            
            // Crossover parents
            
            List<Tour> parents = new ArrayList<Tour>();
            parents.add(parent1);
            parents.add(parent2);
            //parents.add(parent3);
            Tour child = null;
            if (parents.size() == 2){
            	child = crossover(parents);
            } else if (parents.size() > 2){
            	child = multiCrossover(parents);
            }
            
            newPopulation.saveTour(i, child);
        }

        // Mutate the new population a bit to add some new genetic material
        for (int i = elitismOffset; i < newPopulation.populationSize(); i++) {
            mutate(newPopulation.getTour(i));
        }

        return newPopulation;
    }

    public static Tour multiCrossover(List<Tour> parents){
    	Tour child = new Tour();
    	
    	Random r = new Random();
    	int n = child.tourSize() / parents.size();
    	Collections.shuffle(parents);
    	
    	int start = 0, end = n;
    	
    	for (Tour parent : parents){
    		
    		while (start < end){
    			for (int i = 0; i < parent.tourSize(); i++){
    				if (!child.containsPoint(parent.getPoint(i))){
    					child.setPoint(start, parent.getPoint(i));
    					start++;
    					break;
    				}
    			}
    		}
    		
    		end += n;
    	}
    	
    	for (int i = 0; i < child.tourSize(); i++){
    		if (child.getPoint(i) == null){
    			Tour p = parents.get(Math.abs(r.nextInt() % parents.size()));
    			for (int j = 0; j < p.tourSize(); j++){
    				if (!child.containsPoint(p.getPoint(j))){
    					child.setPoint(i, p.getPoint(j));
    					break;
    				}
    			}
    		}
    	}
    	
    	return child;
    }
    
    // Applies crossover to a set of parents and creates offspring
    public static Tour crossover(List<Tour> parents) {
    	Tour parent1 = parents.get(0);
    	Tour parent2 = parents.get(1);
        // Create new child tour
        Tour child = new Tour();

        // Get start and end sub tour positions for parent1's tour
        int startPos = (int) (Math.random() * parent1.tourSize());
        int endPos = (int) (Math.random() * parent1.tourSize());

        if (startPos > endPos){
        	int tmp = endPos;
        	endPos = startPos;
        	startPos = tmp;
        }
        
        // Loop and add the sub tour from parent1 to our child
        for (int i = 0; i < child.tourSize(); i++) {
            // If our start position is less than the end position
            if (startPos < endPos && i > startPos && i < endPos) {
                child.setPoint(i, parent1.getPoint(i));
            } 
        }

        // Loop through parent2's city tour
        for (int i = 0; i < parent2.tourSize(); i++) {
            // If child doesn't have the city add it
            if (!child.containsPoint(parent2.getPoint(i))) {
                // Loop to find a spare position in the child's tour
                for (int j = 0; j < child.tourSize(); j++) {
                    // Spare position found, add city
                    if (child.getPoint(j) == null) {
                        child.setPoint(j, parent2.getPoint(i));
                        break;
                    }
                }
            }
        }
        return child;
    }

    // Mutate a tour using swap mutation
    private static void mutate(Tour tour) {
        // Loop through tour cities
        for(int tourPos1=0; tourPos1 < tour.tourSize(); tourPos1++){
            // Apply mutation rate
            if(Math.random() < mutationRate){
                // Get a second random position in the tour
                int tourPos2 = (int) (tour.tourSize() * Math.random());

                // Get the cities at target position in tour
                Point point1 = tour.getPoint(tourPos1);
                Point point2 = tour.getPoint(tourPos2);

                // Swap them around
                tour.setPoint(tourPos2, point1);
                tour.setPoint(tourPos1, point2);
            }
        }
    }

    // Selects candidate tour for crossover
    private static Tour tournamentSelection(Population pop, int evaluationParameter) {
        // Create a tournament population
        Population tournament = new Population(tournamentSize, false);
        // For each place in the tournament get a random candidate tour and
        // add it
        for (int i = 0; i < tournamentSize; i++) {
            int randomId = (int) (Math.random() * pop.populationSize());
            tournament.saveTour(i, pop.getTour(randomId));
        }
        // Get the fittest tour
        Tour best = null;
        switch (evaluationParameter){
        	case 0:
        		best = tournament.getFittest();
        		break;
        	case 1:
        		best = tournament.getMostRated();
        		break;
        	/*case 2:
        		best = tournament.getCheapest();*/
        }
        return best;
    }
    
    public static Tour getBestTour(List<Tour> tours, int evaluationParameter){
    	Tour best = tours.get(0);
    	
    	for (Tour tour : tours){
    		if (evaluationParameter == 0){
    			if (tour.getDistance() <= best.getDistance()){
    				best = tour;
    			}
    		}
    		
    		if (evaluationParameter == 1){
    			if (tour.getRating() >= best.getRating()){
    				best = tour;
    			}
    		}
    	}
    	
    	return best;
    }
    
}