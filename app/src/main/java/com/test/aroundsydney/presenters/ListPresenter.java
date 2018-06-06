package com.test.aroundsydney.presenters;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.test.aroundsydney.common.AroundSydneyApplication;
import com.test.aroundsydney.models.AppLocationModel;
import com.test.aroundsydney.models.entitys.Location;
import com.test.aroundsydney.views.LocationListView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;
import pl.charmas.android.reactivelocation2.ReactiveLocationProvider;

@InjectViewState
public class ListPresenter extends MvpPresenter<LocationListView> {

    @Inject
    AppLocationModel locationModel;

    @Inject
    ReactiveLocationProvider locationProvider;

    @Inject
    Context context;

    private List<Location> presenterCache;
    private android.location.Location lastKnownMyLocation;

    public ListPresenter() {
        AroundSydneyApplication.getAppComponent().inject(this);
    }

    ListPresenter(AppLocationModel locationModel) {
        this.locationModel = locationModel;
    }


    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        requestLocations();
        initMyLocationProvider();
    }

    @SuppressLint("CheckResult")
    public void initMyLocationProvider() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationProvider.getLastKnownLocation()
                    .subscribe(new Consumer<android.location.Location>() {
                        @Override
                        public void accept(android.location.Location location) {
                            lastKnownMyLocation = location;
                            if (presenterCache != null) {
                                getViewState().clearList();
                                getViewState().showListData(sortLocations(presenterCache, location));
                            }
                        }
                    });
        }
    }

    @SuppressLint("CheckResult")
    private void requestLocations() {
        locationModel.getLocations().subscribe(new Consumer<List<Location>>() {
            @Override
            public void accept(List<Location> locations) {
                presenterCache = locations;
                getViewState().clearList();
                if (lastKnownMyLocation == null) {
                    getViewState().showListData(locations);
                } else {
                    getViewState().showListData(sortLocations(presenterCache, lastKnownMyLocation));
                }
            }
        });
    }


    private List<Location> sortLocations(List<Location> locations, final android.location.Location myLocation) {
        Comparator<Location> comp = new Comparator<Location>() {

            @Override
            public int compare(Location o, Location o2) {
                float[] result1 = new float[3];
                android.location.Location.distanceBetween(myLocation.getLatitude(), myLocation.getLongitude(),
                        o.latitude, o.longitude, result1);
                Float distance1 = result1[0] / 1000;
                o.distance = distance1;

                float[] result2 = new float[3];
                android.location.Location.distanceBetween(myLocation.getLatitude(), myLocation.getLongitude(),
                        o2.latitude, o2.longitude, result2);
                Float distance2 = result2[0] / 1000;
                o2.distance = distance2;

                return distance1.compareTo(distance2);
            }
        };
        Collections.sort(locations, comp);
        return locations;
    }
}
