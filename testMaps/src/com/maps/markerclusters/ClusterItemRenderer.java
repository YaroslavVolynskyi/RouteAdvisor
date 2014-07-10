package com.maps.markerclusters;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

public class ClusterItemRenderer extends DefaultClusterRenderer<MyClusterItem> {
    
	private final IconGenerator mIconGenerator;
    private final IconGenerator mClusterIconGenerator;
    
    Context context;
    GoogleMap googleMap;
    ClusterManager mClusterManager;

    public ClusterItemRenderer(Context context, GoogleMap googleMap, ClusterManager mClusterManager) {
        super(context, googleMap, mClusterManager);

        this.context = context;
        this.googleMap = googleMap;
        this.mClusterManager = mClusterManager;
        
        mIconGenerator = new IconGenerator(context);
        mIconGenerator.setStyle(IconGenerator.STYLE_BLUE);
        
        mClusterIconGenerator = new IconGenerator(context);
        mClusterIconGenerator.setStyle(IconGenerator.STYLE_PURPLE);
    }

    @Override
    protected void onBeforeClusterItemRendered(MyClusterItem item, MarkerOptions markerOptions) {
    	//if (item.step != null){
	        markerOptions
	        	.position(item.step.location)
	        	.title(item.step.distance)
	        	.snippet(item.step.instructions)
	        	//.icon(BitmapDescriptorFactory.defaultMarker());
	        	.icon(BitmapDescriptorFactory.fromBitmap(mIconGenerator.makeIcon(String.valueOf(item.markerCount))));
    	/*} else {
    		markerOptions
	        	.icon(BitmapDescriptorFactory.fromBitmap(mIconGenerator.makeIcon(String.valueOf(item.markerCount))));
    	}*/
        	//.title();
    }

    @Override
    protected void onBeforeClusterRendered(Cluster<MyClusterItem> cluster, MarkerOptions markerOptions) {
    	Object[] items = new Object[cluster.getSize()];
    	
		cluster.getItems().toArray(items);
		MyClusterItem last = (MyClusterItem) items[items.length - 1];
		MyClusterItem first = (MyClusterItem) items[0];
		
        markerOptions.icon(BitmapDescriptorFactory
        				.fromBitmap(mClusterIconGenerator
        						.makeIcon(String.valueOf(first.markerCount + " ... " + last.markerCount))));
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster cluster) {
        // Always render clusters.
        return cluster.getSize() > 1;
    }
}
