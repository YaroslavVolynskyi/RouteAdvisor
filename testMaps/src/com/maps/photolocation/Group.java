package com.maps.photolocation;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Group {

	public String name;
	public final List<String> children = new ArrayList<String>();
	public URL iconURL;
	public Point point;

	public Group(String string) {
		this.name = string;
	}
	
	public Group(Point point){
		this.point = point;
		this.name = point.name;
		this.iconURL = iconURL;
		children.add("rating: " + point.rating);
		children.add("location: " + point.latLng.latitude + ", " + point.latLng.longitude);
		
		String types = "type: "; 
		for(int i = 0; i < point.types.size() - 1; i++){
			types += point.types.get(i) + ", ";
		}
		types += point.types.get(point.types.size() - 1);
		children.add(types);
	}
} 