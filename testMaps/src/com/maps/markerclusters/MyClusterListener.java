package com.maps.markerclusters;

import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.maps.photolocation.Point;

public class MyClusterListener implements 
								ClusterManager.OnClusterClickListener<MyClusterItem>, 
								ClusterManager.OnClusterInfoWindowClickListener<MyClusterItem>, 
								ClusterManager.OnClusterItemClickListener<MyClusterItem>, 
								ClusterManager.OnClusterItemInfoWindowClickListener<MyClusterItem> {

	@Override
	public void onClusterItemInfoWindowClick(MyClusterItem item) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onClusterItemClick(MyClusterItem item) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onClusterInfoWindowClick(Cluster<MyClusterItem> cluster) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean onClusterClick(Cluster<MyClusterItem> cluster) {
		// TODO Auto-generated method stub
		return false;
	}

	
}
