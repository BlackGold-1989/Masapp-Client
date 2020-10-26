package com.laodev.masapp.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationUtil {

    public static String getAddressFromLatlng(Context context, double lat, double lng) {
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(context, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(lat, lng, 1);
            return addresses.get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Address";
    }

    public static String getAddressFromString(Context context, String latlng) {
        if (latlng == null || latlng.isEmpty()) {
            return "Address";
        }
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(context, Locale.getDefault());
        String[] geo = latlng.split(",");
        try {
            addresses = geocoder.getFromLocation(Double.parseDouble(geo[0]), Double.parseDouble(geo[1]), 1);
            return addresses.get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Address";
    }

    public static String getCityFromLatlng(Context context, double lat, double lng) {
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(context, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(lat, lng, 1);
            return addresses.get(0).getLocality();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "City";
    }

    public static String getCityFromString(Context context, String latlng) {
        if (latlng.isEmpty()) {
            return "City";
        }
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(context, Locale.getDefault());
        String[] geo = latlng.split(",");
        try {
            addresses = geocoder.getFromLocation(Double.parseDouble(geo[0]), Double.parseDouble(geo[1]), 1);
            return addresses.get(0).getLocality();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "City";
    }

    public static String getStateFromLatlng(Context context, double lat, double lng) {
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(context, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(lat, lng, 1);
            return addresses.get(0).getLocality();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "State";
    }

}
