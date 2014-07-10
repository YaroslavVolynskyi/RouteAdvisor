package com.maps.markerclusters;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.maps.photolocation.Point;

public class MyClusterItem implements ClusterItem{

	private final LatLng mPosition;
	Point point;
	public Step step;
	public int markerCount;
	
	public MyClusterItem(Step step, int markerCount){
		this.step = step;
		mPosition = step.location;
		this.markerCount = markerCount;
	}
	
	public MyClusterItem(Point point){
		this.point = point;
		mPosition = point.latLng;
	}
	
    public MyClusterItem(double lat, double lng) {
        mPosition = new LatLng(lat, lng);
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

}