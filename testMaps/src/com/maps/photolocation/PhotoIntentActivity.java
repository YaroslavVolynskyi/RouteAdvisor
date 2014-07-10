package com.maps.photolocation;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.maps.locationsecond.LocationUtils;
import com.maps.markerclusters.ClusterItemRenderer;
import com.maps.markerclusters.MyClusterItem;
import com.maps.markerclusters.MyClusterListener;
import com.maps.markerclusters.PlacesClusterItemRenderer;
import com.maps.markerclusters.Step;
import com.maps.photolocation.R;
import com.maps.photolocation.SwipeDismissListViewTouchListener.DismissCallbacks;
import com.maps.tsp.GA_Manager;

import android.annotation.SuppressLint;
import android.app.Activity;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SlidingDrawer;
import android.widget.Toast;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;


public class PhotoIntentActivity extends Activity implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener, 
		LocationListener, 
		LocationSource, com.google.android.gms.location.LocationListener{

	public GoogleMap googleMap;
	MapFragment mapFragment;
	
	
	private ImageView mImageView;
	
	Button.OnClickListener mTakePicOnClickListener = 
			new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				//dispatchTakePictureIntent(ACTION_TAKE_BIG_PHOTO);
				photo = new Photo(context, mImageView);
				photo.dispatchTakePictureIntent(ACTION_TAKE_BIG_PHOTO);
			}
		};
		
    private void initializeLocation(){
    	mLocationRequest = LocationRequest.create();

        /*
         * Set the update interval
         */
        mLocationRequest.setInterval(LocationUtils.UPDATE_INTERVAL_IN_MILLISECONDS);

        // Use high accuracy
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Set the interval ceiling to one minute
        mLocationRequest.setFastestInterval(LocationUtils.FAST_INTERVAL_CEILING_IN_MILLISECONDS);

        // Note that location updates are off until the user turns them on
        mUpdatesRequested = false;

        // Open Shared Preferences
        mPrefs = getSharedPreferences(LocationUtils.SHARED_PREFERENCES, Context.MODE_PRIVATE);

        // Get an editor
        mEditor = mPrefs.edit();

        /*
         * Create a new location client, using the enclosing class to
         * handle callbacks.
         */
        mLocationClient = new LocationClient(this, this, this);
    }
    
    private void initializeDrawer(Bundle savedInstanceState){
    	mTitle = mDrawerTitle = getTitle();
        mDrawerTitles = getResources().getStringArray(R.array.drawer_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mDrawerTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
        }
    }
	
	Photo photo;
	Activity context;
		
	/** Called when the activity is first created. */
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		//getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		context = this;
			
		currentPoints = new ArrayList<Point>();
		
		lastSelectedItems = new ArrayList<Integer>();
		
		mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
		googleMap = mapFragment.getMap();
		
		initializeLocation();
        initializeDrawer(savedInstanceState);
        
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        
        searchMyLocationButton = (ImageButton) findViewById(R.id.searchMyLocationButton);
        searchMyLocationButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (gpsInternetAvailable()){
					findMyLocation();
				}	
			}
		});
        
        searchingButton = (Button) findViewById(R.id.searchButton);
        searchingButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (currentPoints != null && currentPoints.size() >= 2){
					if (gpsInternetAvailable()){
						search(searchingField.getText().toString());
					}
				}
			}
		});
        
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        
		slidingDrawer = (SlidingDrawer) findViewById(R.id.SlidingDrawerMain);
		
		slidingDrawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {
			@Override
			public void onDrawerOpened() {
			}
		});
	
		slidingDrawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {
			@Override
			public void onDrawerClosed() {
			}
		});
		
		expListView = (ExpandableListView) findViewById(R.id.contentExpandableListViewMain);
		
		
		expListView.setOnGroupClickListener(new OnGroupClickListener() {
			
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
				
				MyExpandableListAdapter myAdapter = (MyExpandableListAdapter) expListView.getExpandableListAdapter();
				Group g = (Group) myAdapter.getGroup(groupPosition);
				moveCamera(g.point.latLng, 14);
				
				return false;
			}
		});
		
		expandableListAdapter = new MyExpandableListAdapter(this, new ArrayList<Group>());
		
		expListView.setAdapter(expandableListAdapter);
		
		SwipeDismissListViewTouchListener touchListener =
	         new SwipeDismissListViewTouchListener(
	                 expListView,
	                 new DismissCallbacks() {
	                	 @Override
	                     public void onDismiss(ExpandableListView listView, int[] reverseSortedPositions) {
	                		 adapter = (MyExpandableListAdapter) expListView.getExpandableListAdapter();
	                         for (int position : reverseSortedPositions) {
	                        	 adapter.myRemoveAt(position);
	                        	 //calculatePlacesDistances();
	                         }
	                         adapter.notifyDataSetChanged();
	                     }

						@Override
						public boolean canDismiss(int position) {
							return true;
						}
	                 });
		expListView.setOnTouchListener(touchListener);
		expListView.setOnScrollListener(touchListener.makeScrollListener());
		 
		searchingField = (AutoCompleteTextView) findViewById(R.id.searchingField);
	    searchingField.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.list_item));
	    
	    searchingField.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				searchingField.setText("");
				slidingDrawer.close();
			}
		});
	    searchingField.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				searchingField.setText("");
				slidingDrawer.close();
			}
		});
	    
	    searchingField.setOnItemClickListener(new OnItemClickListener() {
	        	
	        @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        	    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchingField.getWindowToken(), 0);
                
                PartPlaceInfo selectedPlace = (PartPlaceInfo) adapterView.getItemAtPosition(position);
                expandableListAdapter.addPlaceByReference(selectedPlace.reference);
                expandableListAdapter.notifyDataSetChanged();
        	}
		});
	    	    
	    searchPlacesButton = (Button) findViewById(R.id.searchPlacesButton);
        searchPlacesButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (gpsInternetAvailable()){
					InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(searchingField.getWindowToken(), 0);
					
					showDialog();   // search places, /*calculateDistances*/
				}
			}
		});
        
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        
        initializeLocationManager();
        
        setUpClusterer();
    }
	
	public ClusterManager<MyClusterItem> mClusterManager, placesClusterManager;
	
	private void setUpPlacesClusterer(){
		setUpMapIfNeeded();
		
		placesClusterManager = new ClusterManager<MyClusterItem>(this, googleMap);
        placesClusterManager.setRenderer(new PlacesClusterItemRenderer(context, googleMap, placesClusterManager));
        googleMap.setOnCameraChangeListener(placesClusterManager);
        googleMap.setOnMarkerClickListener(placesClusterManager);
        googleMap.setOnInfoWindowClickListener(placesClusterManager);
        
        MyClusterListener listener = new MyClusterListener();
        
        placesClusterManager.setOnClusterClickListener(listener);
        placesClusterManager.setOnClusterInfoWindowClickListener(listener);
        placesClusterManager.setOnClusterItemClickListener(listener);
        placesClusterManager.setOnClusterItemInfoWindowClickListener(listener);

        
        placesClusterManager.cluster();
	}
	
	private void setUpClusterer() {
	    setUpMapIfNeeded();

	    mClusterManager = new ClusterManager<MyClusterItem>(this, googleMap);
        mClusterManager.setRenderer(new ClusterItemRenderer(context, googleMap, mClusterManager));
        googleMap.setOnCameraChangeListener(mClusterManager);
        googleMap.setOnMarkerClickListener(mClusterManager);
        googleMap.setOnInfoWindowClickListener(mClusterManager);
        
        MyClusterListener listener = new MyClusterListener();
        
        mClusterManager.setOnClusterClickListener(listener);
        mClusterManager.setOnClusterInfoWindowClickListener(listener);
        mClusterManager.setOnClusterItemClickListener(listener);
        mClusterManager.setOnClusterItemInfoWindowClickListener(listener);

        
        mClusterManager.cluster();
	}

	
	
	private void initializeLocationManager(){
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        
        if(locationManager != null){
            boolean gpsIsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean networkIsEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
             
            if(gpsIsEnabled){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000L, 10F, this);
            } else if(networkIsEnabled){
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000L, 10F, this);
            }
            else{
                //Show an error dialog that GPS is disabled...
            }
        }
        else{
            //Show some generic error dialog because something must have gone wrong with location manager.
        }
     
        setUpMapIfNeeded();
	}
	
	public MyExpandableListAdapter adapter;
	List<Integer> lastSelectedItems;
	
	public double[][] distances, durations;
	
	public void calculatePlacesDistances(){
		DistanceMatrix distMatrix = new DistanceMatrix((PhotoIntentActivity)context);
		if (currentPoints != null){
			distMatrix.getDistances(currentPoints, "walking");
		}
	}
	
	private void showDialog() {
	    DialogFragment newFragment = MyAlertDialogFragment.newInstance(R.string.places_types_title, this);
	    newFragment.show(getFragmentManager(), "dialog");
	}
	
	public List<Point> currentPoints;
	public MyExpandableListAdapter expandableListAdapter;
	ExpandableListView expListView;
	public SlidingDrawer slidingDrawer;
	Button searchingButton, searchPlacesButton, getDistancesButton;
	ImageButton searchMyLocationButton;
	public AutoCompleteTextView searchingField;
	
	List<Group> groups;
	
	public void searchPlaces(){
		Location location = getLocation();
	    moveCamera(location);
	    
	    ArrayList<String> types = new ArrayList<String>();
	    Resources resources = getResources();
	    String[] typesArray = resources.getStringArray(R.array.places_types);
	    
	    if (lastSelectedItems != null){
		    for (Integer item : lastSelectedItems){
		    	types.add(typesArray[item]);
		    }
	    }
	    //Log.d("wtf", "placesBefore");
	    Places places = new Places(this, new LatLng(location.getLatitude(), location.getLongitude()), types);
	    places.getAllPlaces(null);
	}		
	
	public List<Point> possibleRoutePoints;
	public List<Point> oldPoints;
	
	public void search(String cityName){  // get final places by GA and draw route on the map in postexecute;
		
		GA_Manager gaManager = new GA_Manager();
		oldPoints = currentPoints;
		
		gaManager.getBestTour(currentPoints, (PhotoIntentActivity)context);
		
		//moveCamera(route.points.get(0).location);
	}
	
	private void findMyLocation(){
		googleMap.setMyLocationEnabled(true);
		
		Location location = getLocation();
	    
		getAddress();
		
	    moveCamera(location);
	}
	
	public void moveCamera(LatLng latLng, int zoomLevel){
		int newZoom = (int) googleMap.getCameraPosition().zoom;
		if (zoomLevel < newZoom){
			zoomLevel = newZoom;
		}
		CameraPosition cameraPosition = new CameraPosition.Builder()
	    	.target(latLng)
		    .zoom(zoomLevel)                      // Sets the zoom
		    /*.bearing(90)                // Sets the orientation of the camera to east
		    .tilt(30)*/                  // Sets the tilt of the camera to 30 degrees
		    .build();                   // Creates a CameraPosition from the builder
		googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	}
	
	public void moveCamera(Location location){
		CameraPosition cameraPosition = new CameraPosition.Builder()
	    	.target(new LatLng(location.getLatitude(), location.getLongitude()))
		    .zoom(12)                      // Sets the zoom
		    /*.bearing(90)                // Sets the orientation of the camera to east
		    .tilt(30)*/                  // Sets the tilt of the camera to 30 degrees
		    .build();                   // Creates a CameraPosition from the builder
		googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	}
	
	private boolean gpsInternetAvailable(){
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        //menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    public int maxRadiusValue = 10000, maxCitiesValue = 50;
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         // The action bar home/up action should open or close the drawer.
         // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch(item.getItemId()) {
	        
        	case R.id.radius:
        		DialogFragment radiusFragment = MyPickerDialogFragment.newInstance(R.string.radius_title, this, maxRadiusValue);
        	    radiusFragment.show(getFragmentManager(), "Radius");
        	    
        		return true;
        		
        	case R.id.minimumCities:
        		DialogFragment newFragment = MyPickerDialogFragment.newInstance(R.string.minimum_cities_title, this, maxCitiesValue);
        	    newFragment.show(getFragmentManager(), "MinimumCities");
        		
        		return true;
        	
	        case R.id.about:
	        	Toast.makeText(getBaseContext(),"This is Billion Dollar App", Toast.LENGTH_SHORT).show();
	        	return true;
	        
	        default:
	            return super.onOptionsItemSelected(item);
        }
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position){
	            case 0:{
	            	changeSattelite();
	            	break;
	            }
	            case 1:{
	            	clearRoute();
	            	break;
	            }
            }
            
            mDrawerList.setItemChecked(position, true);
            mDrawerLayout.closeDrawer(mDrawerList);
        }
    }

    private void clearRoute(){
    	Toast.makeText(context, "Clear", Toast.LENGTH_SHORT).show();
    	if (mClusterManager != null){
	    	mClusterManager.clearItems();
	    	mClusterManager.cluster();
    	}
    	
    	if (currentPoints != null){
	    	for (Point p : currentPoints){
	    		p.marker.remove();
	    	}
	    	
	    	currentPoints.clear();
    	}
    	
    	if (stepsToSave != null){
    		stepsToSave.clear();
    	}
    	//markerOptionsList.clear();
    	if (polylineOptionsList != null){
    		polylineOptionsList.clear();
    	}
    	
    	if (googleMap != null){
    		googleMap.clear();
    	}
    }
    
    boolean satellite = false;
    
    private void changeSattelite(){
    	if (!satellite){
    		googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
    		satellite = true;
    	} else {
    		googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    		satellite = false;
    	}
    }
    
    private void selectItem(int position) {
        // update the main content by replacing fragments
    	mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

	private CharSequence mTitle;
	private CharSequence mDrawerTitle;
	private String[] mDrawerTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;	
	
    ///////////////////////////
    ///////////////////////////
	private void addHomeMarker(){
		Location location = getLocation();
	
	    Marker marker = googleMap.addMarker(new MarkerOptions()
			.position(new LatLng(location.getLatitude(), location.getLongitude()))
			.alpha(0.8f)
			.snippet("no info")
			.flat(true)
			.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
			.title("ME"));
	    
	    CameraPosition cameraPosition = new CameraPosition.Builder()
	    	.target(new LatLng(location.getLatitude(), location.getLongitude()))
		    .zoom(15)                   // Sets the zoom
		    /*.bearing(90)                // Sets the orientation of the camera to east
		    .tilt(30)                   // Sets the tilt of the camera to 30 degrees*/
		    .build();                   // Creates a CameraPosition from the builder
	    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	}
	
	
	public static final int ACTION_TAKE_BIG_PHOTO = 1;
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case ACTION_TAKE_BIG_PHOTO: {
				if (resultCode == RESULT_OK) {
					mImageView.setImageBitmap(photo.handleBigCameraPhoto());
					mImageView.setVisibility(View.VISIBLE);
				}
				break;
			} // ACTION_TAKE_BIG_PHOTO
			case LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST :{

                switch (resultCode) {
                    // If Google Play services resolved the problem
                    case Activity.RESULT_OK:
                        Log.d(LocationUtils.APPTAG, getString(R.string.resolved));
                    break;

                    // If any other result was returned by Google Play services
                    default:
                        Log.d(LocationUtils.APPTAG, getString(R.string.no_resolution));
                    break;
                }
			}
		} 
	}

	public ArrayList<PolylineOptions> polylineOptionsList;
	public ArrayList<MarkerOptions> markerOptionsList;
	
	public ArrayList<Step> stepsToSave;
	
	String polOptionsList = "polOptionsList";
	String markOptionsList = "markOptionsList";
	String currentPointsKey = "curPoints";
	String stepsKey = "stepsKey";
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		
		if (polylineOptionsList != null){
			outState.putParcelableArrayList(polOptionsList, polylineOptionsList);
		}
		
		if (currentPoints != null){
			outState.putParcelableArrayList(currentPointsKey, (ArrayList<Point>)currentPoints);
		}
		
		if (stepsToSave != null){
			outState.putParcelableArrayList(stepsKey, stepsToSave);
		}
		
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
			
		polylineOptionsList = (ArrayList<PolylineOptions>) savedInstanceState.get(polOptionsList);
		if (polylineOptionsList != null){
			for (PolylineOptions pO : polylineOptionsList){
				googleMap.addPolyline(pO);
			}
		}
		
		currentPoints = (ArrayList<Point>) savedInstanceState.get(currentPointsKey);
		List<Group> groups = new ArrayList<Group>();
		if (currentPoints != null){
			for (Point p : currentPoints){
				googleMap.addMarker(p.markerOptions);
				groups.add(new Group(p));
			}
			expandableListAdapter.addGroups(groups);
		}
		
		stepsToSave = (ArrayList<Step>) savedInstanceState.get(stepsKey);
		setUpClusterer();
		int i = 1;
		if (stepsToSave != null){
			for(Step s : stepsToSave){
				mClusterManager.addItem(new MyClusterItem(s, i));
				i++;
			}
			mClusterManager.cluster();
		}
	}

	public static boolean isIntentAvailable(Context context, String action) {
		final PackageManager packageManager = context.getPackageManager();
		final Intent intent = new Intent(action);
		List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}

	private LocationRequest mLocationRequest;
    private LocationClient mLocationClient;
    SharedPreferences mPrefs;
    SharedPreferences.Editor mEditor;
    boolean mUpdatesRequested = false;
    
    @Override
    public void onStop() {

        // If the client is connected
        if (mLocationClient.isConnected()) {
            stopPeriodicUpdates();
        }

        // After disconnect() is called, the client is considered "dead".
        mLocationClient.disconnect();

        super.onStop();
    }
    
    @Override
    public void onPause() {

        // Save the current setting for updates
        mEditor.putBoolean(LocationUtils.KEY_UPDATES_REQUESTED, mUpdatesRequested);
        mEditor.commit();
        
        if(locationManager != null)
        {
            locationManager.removeUpdates(this);
        }
        

        super.onPause();
    }

    
    @Override
    public void onStart() {

        super.onStart();
        mLocationClient.connect();

    }
    
    @Override
    public void onResume() {
        super.onResume();

        setUpMapIfNeeded();
        
        if(locationManager != null){
        	googleMap.setMyLocationEnabled(true);
        }
        
        // If the app already has a setting for getting location updates, get it
        if (mPrefs.contains(LocationUtils.KEY_UPDATES_REQUESTED)) {
            mUpdatesRequested = mPrefs.getBoolean(LocationUtils.KEY_UPDATES_REQUESTED, false);

        // Otherwise, turn off location updates until requested
        } else {
            mEditor.putBoolean(LocationUtils.KEY_UPDATES_REQUESTED, false);
            mEditor.commit();
        }

    }
    
    private boolean servicesConnected() {

        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d(LocationUtils.APPTAG, getString(R.string.play_services_available));

            // Continue
            return true;
        // Google Play services was not available for some reason
        } else {
            // Display an error dialog
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0);
            if (dialog != null) {
                /*ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(dialog);
                errorFragment.show(getSupportFragmentManager(), LocationUtils.APPTAG);*/
            }
            return false;
        }
    }

    /**
     * Invoked by the "Get Location" button.
     *
     * Calls getLastLocation() to get the current location
     *
     * @param v The view object associated with this method, in this case a Button.
     */
    public void getLocation(View v) {

        // If Google Play Services is available
        if (servicesConnected()) {
            // Get the current location
            Location currentLocation = mLocationClient.getLastLocation();
        }
    }
    
    public Location getLocation(){
    	Location currentLocation = null;
    	if (servicesConnected()) {
            // Get the current location
            currentLocation = mLocationClient.getLastLocation();
    	}
    	
    	return currentLocation;
    }

   
    public void getAddress() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD && !Geocoder.isPresent()) {
            Toast.makeText(this, R.string.no_geocoder_available, Toast.LENGTH_LONG).show();
            return;
        }

        if (servicesConnected()) {
            (new GetAddressTask(this)).execute(this);
        }
    }

    /**
     * Invoked by the "Start Updates" button
     * Sends a request to start location updates
     *
     * @param v The view object associated with this method, in this case a Button.
     */
    public void startUpdates(View v) {
        mUpdatesRequested = true;

        if (servicesConnected()) {
            startPeriodicUpdates();
        }
    }

    /**
     * Invoked by the "Stop Updates" button
     * Sends a request to remove location updates
     * request them.
     *
     * @param v The view object associated with this method, in this case a Button.
     */
    public void stopUpdates(View v) {
        mUpdatesRequested = false;

        if (servicesConnected()) {
            stopPeriodicUpdates();
        }
    }

    /*
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    @Override
    public void onConnected(Bundle bundle) {
        if (mUpdatesRequested) {
            startPeriodicUpdates();
        }
    }

    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onDisconnected() {
    }

    /*
     * Called by Location Services if the attempt to
     * Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {

                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

                /*
                * Thrown if Google Play services canceled the original
                * PendingIntent
                */

            } catch (IntentSender.SendIntentException e) {

                // Log the error
                e.printStackTrace();
            }
        } else {

            // If no resolution is available, display a dialog to the user with the error.
            showErrorDialog(connectionResult.getErrorCode());
        }
    }

    
    /**
     * In response to a request to start updates, send a request
     * to Location Services
     */
    private void startPeriodicUpdates() {
        mLocationClient.requestLocationUpdates(mLocationRequest, this);
    }

    /**
     * In response to a request to stop updates, send a request to
     * Location Services
     */
    private void stopPeriodicUpdates() {
        mLocationClient.removeLocationUpdates(this);
    }

    

    /**
     * Show a dialog returned by Google Play services for the
     * connection error code
     *
     * @param errorCode An error code returned from onConnectionFailed
     */
    private void showErrorDialog(int errorCode) {

        // Get the error dialog from Google Play services
        Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
            errorCode,
            this,
            LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

        // If Google Play services can provide an error dialog
        if (errorDialog != null) {
/*            // Create a new DialogFragment in which to show the error dialog
            ErrorDialogFragment errorFragment = new ErrorDialogFragment();

            // Set the dialog in the DialogFragment
            errorFragment.setDialog(errorDialog);

            // Show the error dialog in the DialogFragment
            errorFragment.show(getSupportFragmentManager(), LocationUtils.APPTAG);*/
        }
    }

    private OnLocationChangedListener mListener;
    private LocationManager locationManager;
    
    private void setUpMapIfNeeded() {
        if (googleMap == null) 
        {
            mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
    		googleMap = mapFragment.getMap();
            
            if (googleMap != null) 
            {
                setUpMap();
            }
 
            //This is how you register the LocationSource
            googleMap.setLocationSource(this);
        }
    }
     
    /**
     * This is where we can add markers or lines, add listeners or move the camera.
     * <p>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap(){
        googleMap.setMyLocationEnabled(true);
    }
    
    @Override
    public void activate(OnLocationChangedListener listener){
        mListener = listener;
    }
     
    @Override
    public void deactivate(){
        mListener = null;
    }
 
    @Override
    public void onLocationChanged(Location location){
        if( mListener != null )
        {
            mListener.onLocationChanged( location );
 
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
        }
    }
 
    @Override
    public void onProviderDisabled(String provider){
        Toast.makeText(this, "provider disabled", Toast.LENGTH_SHORT).show();
    }
 
    @Override
    public void onProviderEnabled(String provider){
        Toast.makeText(this, "provider enabled", Toast.LENGTH_SHORT).show();
    }
 
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras){
        Toast.makeText(this, "status changed", Toast.LENGTH_SHORT).show();
    }

}