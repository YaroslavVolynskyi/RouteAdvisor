package com.maps.photolocation;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;

public class DBManager {
	
	public static ArrayList<LatLng> getAllCityPoints(String cityName){
		/*ArrayList<Point> points = new ArrayList<Point>();*/
		ArrayList<LatLng> points = new ArrayList<LatLng>();
		// select points from city;
		/*points.add(new Point(new LatLng(50.4500000, 30.5233333)));
		points.add(new Point(new LatLng(50.4528, 30.5144)));*/
		points.add(new LatLng(50.4500000, 30.5233333));
		points.add(new LatLng(50.4528, 30.5144));
//		points.add(new LatLng(50.4489, 30.5133));
//		points.add(new LatLng(50.4789, 30.5422));
//		points.add(new LatLng(50.418, 30.58));
		
		
		return points;
	}

	public static List<User> getFriends(String userName){
		List<User> friends = new ArrayList<User>();
		
		// select friends by userName;
		
		return friends;
	}
}
