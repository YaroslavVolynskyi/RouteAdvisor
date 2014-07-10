package com.maps.markerclusters;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Html;

import com.google.android.gms.maps.model.LatLng;

/**
 * Class that represent every step of the directions. It store distance,
 * location and instructions
 */
public class Step implements Parcelable{
	public String distance;
	public LatLng location;
	public String instructions;

	public Step(JSONObject stepJSON) {
		JSONObject startLocation;
		try {
			distance = stepJSON.getJSONObject("distance").getString("text");
			startLocation = stepJSON.getJSONObject("start_location");
			location = new LatLng(startLocation.getDouble("lat"), startLocation.getDouble("lng"));
			try {
				instructions = URLDecoder.decode(
									Html.fromHtml(stepJSON.getString("html_instructions"))
									.toString(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int describeContents() {
		return 0;
	}

	String latLngKey = "latLngKey";
	
	@Override
	public void writeToParcel(Parcel parcel, int arg1) {
		String[] params = new String[2];
		params[0] = distance;
		params[1] = instructions;
		Bundle bundle = new Bundle();
		bundle.putParcelable(latLngKey, location);
		
		parcel.writeBundle(bundle);
		parcel.writeStringArray(params);
	}
	
	public Step(Parcel parcel){
		String params[] = null;
		parcel.readStringArray(params);
		Bundle b = parcel.readBundle();
		
		distance = params[0];
		instructions = params[1];
		
		location = (LatLng) b.get(latLngKey);
	}
	
	
	
}