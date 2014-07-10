package com.maps.photolocation;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;


public class MyAlertDialogFragment extends DialogFragment {

	static PhotoIntentActivity activity;
	
    public static MyAlertDialogFragment newInstance(int title, PhotoIntentActivity myActivity) {
    	activity = myActivity;
        MyAlertDialogFragment frag = new MyAlertDialogFragment();
        Bundle args = new Bundle();
        args.putInt("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		final List<Integer> mSelectedItems = new ArrayList<Integer>();  // Where we track the selected items
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    
	    int title = getArguments().getInt("title");
	    
	    builder.setTitle(title)
	           .setMultiChoiceItems(R.array.places_types, null,
	                      new DialogInterface.OnMultiChoiceClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int which,
	                       boolean isChecked) {
	                   if (isChecked) {
	                       // If the user checked the item, add it to the selected items
	                       mSelectedItems.add(which);
	                   } else if (mSelectedItems.contains(which)) {
	                       // Else, if the item is already in the array, remove it 
	                       mSelectedItems.remove(Integer.valueOf(which));
	                   }
	               }
	           })
	           .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	            	   //Log.d("wtf", "okButton");
	            	   activity.lastSelectedItems = new ArrayList<Integer>();
	            	   for (Integer item : mSelectedItems){
	            		   activity.lastSelectedItems.add(item);
	            	   }
	            	   
	            	   activity.searchPlaces();
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