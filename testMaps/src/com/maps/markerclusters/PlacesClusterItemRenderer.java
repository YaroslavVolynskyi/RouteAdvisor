package com.maps.markerclusters;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

public class PlacesClusterItemRenderer extends DefaultClusterRenderer<MyClusterItem> {
    
	private final IconGenerator mIconGenerator;
    private final IconGenerator mClusterIconGenerator;
    
    Context context;
    GoogleMap googleMap;
    ClusterManager mClusterManager;

    public PlacesClusterItemRenderer(Context context, GoogleMap googleMap, ClusterManager mClusterManager) {
        super(context, googleMap, mClusterManager);

        this.context = context;
        this.googleMap = googleMap;
        this.mClusterManager = mClusterManager;
        
        mIconGenerator = new IconGenerator(context);
        mIconGenerator.setStyle(IconGenerator.STYLE_BLUE);
        
        mClusterIconGenerator = new IconGenerator(context);
        mClusterIconGenerator.setStyle(IconGenerator.STYLE_RED);
    }
    
    @Override
    protected void onBeforeClusterItemRendered(MyClusterItem item, MarkerOptions markerOptions) {
        markerOptions
	        .position(item.point.latLng)
			.alpha(0.8f)
			.flat(true)
			.icon(BitmapDescriptorFactory.fromBitmap(item.point.icon))
			.title(item.point.name);
        	//.icon(BitmapDescriptorFactory.fromBitmap(mIconGenerator.makeIcon(String.valueOf(item.markerCount))));
        	//.title();
    }

    @Override
    protected void onBeforeClusterRendered(Cluster<MyClusterItem> cluster, MarkerOptions markerOptions) {
    	
        markerOptions.icon(BitmapDescriptorFactory
        				.fromBitmap(mClusterIconGenerator
        				.makeIcon(String.valueOf(cluster.getSize()))));
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster cluster) {
        // Always render clusters.
        return cluster.getSize() > 1;
    }
}
