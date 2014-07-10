package com.maps.photolocation;

import java.util.ArrayList;
import java.util.List;

public class RoutesAdvisor {

	PointAdvisor pointAdvisor;
	
	public List<Route> getRoutes(String userName, String cityName){
		List<Route> routes = new ArrayList<Route>();
		
		pointAdvisor = new PointAdvisor();
		List<User> friends = DBManager.getFriends(userName);
		//List<Point> points = DBManager.getAllCityPoints(cityName);
		//pointAdvisor.getRoute(friends, points);
		
		return routes;
	}
}
