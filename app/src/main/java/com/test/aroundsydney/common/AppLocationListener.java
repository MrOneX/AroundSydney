package com.test.aroundsydney.common;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;


public class AppLocationListener implements LocationListener {

    public Location myLocation;

    @Override
    public void onLocationChanged(Location location) {
        myLocation = location;
        LocalBroadcastManager.getInstance(AroundSydneyApplication.getAppComponent().getContext())
                .sendBroadcast(new Intent(Constant.LOCATION_UPDATE_EVENT));
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
