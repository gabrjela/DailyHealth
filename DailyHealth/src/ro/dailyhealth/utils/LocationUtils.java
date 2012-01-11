package ro.dailyhealth.utils;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

public class LocationUtils {
	static String country = null;
	static double lat, lon;
	
	public static double[] geocode(String city, Context context) {
		 Geocoder gcoder = new Geocoder(context);
		 try {
			List<Address> results = gcoder.getFromLocationName(city, 5);
			Iterator<Address> locations = results.iterator();
			while(locations.hasNext()) {
				Address location = locations.next();
				lat = location.getLatitude();
				lon = location.getLongitude();
				country = location.getCountryName();
				if (country.equalsIgnoreCase("Romania")) {
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		 
		 return new double[] {lat, lon};
		
	}
}
