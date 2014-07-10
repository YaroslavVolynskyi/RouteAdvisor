package com.maps.photolocation;

import java.util.ArrayList;
import java.util.List;

import com.maps.tsp.GA;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

public class MyPickerDialogFragment extends DialogFragment implements NumberPicker.OnValueChangeListener{
	
	static PhotoIntentActivity activity;
	
	public static MyPickerDialogFragment newInstance(int title, PhotoIntentActivity myActivity, int maximumValue){
		activity = myActivity;
		maxValue = maximumValue;
		MyPickerDialogFragment frag = new MyPickerDialogFragment();
        Bundle args = new Bundle();
        args.putInt("title", title);
        frag.setArguments(args);
        return frag;
	}
	
	static int maxValue;
	int value;
	
	@Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
         value = newVal;
     }
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		final List<Integer> mSelectedItems = new ArrayList<Integer>();  // Where we track the selected items
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    
	    View view = activity.getLayoutInflater().inflate(R.layout.min_cities_dialog, null);
	    
	    builder.setView(view);
	    
        final NumberPicker np = (NumberPicker) view.findViewById(R.id.numberPicker);
        
        np.setMaxValue(maxValue);
        np.setMinValue(0);
        np.setWrapSelectorWheel(true);
        np.setOnValueChangedListener(this);
        
        if (maxValue == activity.maxCitiesValue){
        	SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        	int defaultValue = 0;
        	np.setValue(sharedPref.getInt(getString(R.string.max_cities), defaultValue));
            /*if (GA.minimumCitiesAmount >= 0){
            	np.setValue(GA.minimumCitiesAmount);
            }*/
	    } else if (maxValue == activity.maxRadiusValue){
		    SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
		    int defaultValue = 0;
		    np.setValue(sharedPref.getInt(getString(R.string.current_radius), defaultValue));
		     /*if (Places.radius >= 0){
			 	np.setValue(Places.radius);
			 }*/
	    }
        
	    int title = getArguments().getInt("title");
	    
	    builder.setTitle(title)
	           .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	            	   //GA.minimumCitiesAmount = value;
	            	   if (maxValue == activity.maxCitiesValue){
	            		   //GA.minimumCitiesAmount = np.getValue();
	            		   SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
	            		   SharedPreferences.Editor editor = sharedPref.edit();
	            		   editor.putInt(getString(R.string.max_cities), np.getValue());
	            		   editor.commit();
	            		   
	            	   } else if (maxValue == activity.maxRadiusValue){
	            		   //Places.radius = np.getValue();
	            		   SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
	            		   SharedPreferences.Editor editor = sharedPref.edit();
	            		   editor.putInt(getString(R.string.current_radius), np.getValue());
	            		   editor.commit();
	            	   }
	               }
	           })
	           .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	            	   
	               }
	           });

	    return builder.create();
	}

}
