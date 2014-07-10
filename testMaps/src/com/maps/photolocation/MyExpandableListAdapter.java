package com.maps.photolocation;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MyExpandableListAdapter extends BaseExpandableListAdapter {

	// private final SparseArray<Group> groups;
	public List<Group> groups;
	public LayoutInflater inflater;
	public PhotoIntentActivity activity;
	public MyExpandableListAdapter context;

	public MyExpandableListAdapter(PhotoIntentActivity act, ArrayList<Group> groups) {
		context = this;
		activity = act;
		this.groups = groups;
		inflater = act.getLayoutInflater();
	}
	
	public void addGroups(List<Group> groups){
		for (Group g : groups){
			this.groups.add(g);
		}
	}

	public void myRemoveAt(int index){
		Point pointToRemove = groups.get(index).point; 
		pointToRemove.marker.remove();
		Log.d("removepoint", "size = " + activity.currentPoints.size());
		activity.currentPoints.remove(pointToRemove);
		Log.d("removepoint", "size = " + activity.currentPoints.size());
		
		groups.remove(index);
	}
	
	public void removeGroup(int index){
		groups.remove(index);
	}
	
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return groups.get(groupPosition).children.get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public View getChildView(int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		final String children = (String) getChild(groupPosition, childPosition);
		TextView text = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.listrow_details, null);
		}
		text = (TextView) convertView.findViewById(R.id.textView1);
		text.setText(children);
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(activity, children, Toast.LENGTH_SHORT).show();
			}
		});

		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return groups.get(groupPosition).children.size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return groups.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return groups.size();
	}

	@Override
	public void onGroupCollapsed(int groupPosition) {
		super.onGroupCollapsed(groupPosition);
	}

	@Override
	public void onGroupExpanded(int groupPosition) {
		super.onGroupExpanded(groupPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.listrow_group, null);
		}
		
		Group group = (Group) getGroup(groupPosition);
		
		TextView textView = (TextView) convertView.findViewById(R.id.textViewGroup);
		ImageView imageView = (ImageView) convertView.findViewById(R.id.listRowGroupImageView);
		imageView.setImageBitmap(group.point.icon);
		textView.setText(group.name);

		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
	
	public void addPlaceByReference(String reference){
		 new getPlaceByRefence().execute(makePlacesURL(reference));
	}
	
	public String makePlacesURL(String reference){
		StringBuilder url = new StringBuilder();
		url.append("https://maps.googleapis.com/maps/api/place/details/json");
		url.append("?reference=");
		url.append(reference);
		url.append("&sensor=false");
		url.append("&key=");
		url.append("AIzaSyBKJD8p-cFipXucUy3XyDRXePkJhvRaI2Q");
		
		return url.toString();
	}
	
	private class getPlaceByRefence extends AsyncTask<String, Void, String>{
		
	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	    }
	    
	    @Override
	    protected String doInBackground(String... urls) {
	    	return JSONParser.getJSON(urls[0]);
	    }
	    
	    @Override
	    protected void onPostExecute(String result) {
	        super.onPostExecute(result);   
	        
	        PlacesParser placesParser = new PlacesParser();
	        Point newPoint = null;
			try {
				newPoint = placesParser.getPoint(result, activity);
			} catch (JSONException e) {
			}
			newPoint.drawPointMarker(activity.googleMap, activity);
			if (activity.currentPoints == null){
				activity.currentPoints = new ArrayList<Point>();
			}
			activity.currentPoints.add(newPoint);
			//activity.calculatePlacesDistances();
	        if (groups == null)
	        	groups = new ArrayList<Group>();
	        groups.add(new Group(newPoint));
	        
			//context.notifyDataSetChanged();
			activity.slidingDrawer.open();
	    }
	    
	}
}








