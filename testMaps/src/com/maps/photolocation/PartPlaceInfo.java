package com.maps.photolocation;

public class PartPlaceInfo {

	public String reference, description;
	
	public PartPlaceInfo(String reference, String description){
		this.description = description;
		this.reference = reference;
	}
	
	public PartPlaceInfo(){
	}
	
	@Override
	public String toString(){
		return description;
	}
}
