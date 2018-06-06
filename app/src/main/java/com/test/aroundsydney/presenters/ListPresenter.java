package com.test.aroundsydney.presenters;

import android.annotation.SuppressLint;
import android.content.Context;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.google.android.gms.location.LocationRequest;
import com.test.aroundsydney.common.AroundSydneyApplication;
import com.test.aroundsydney.common.Utils;
import com.test.aroundsydney.models.AppLocationModel;
import com.test.aroundsydney.models.LocationModel;
import com.test.aroundsydney.models.entitys.Location;
import com.test.aroundsydney.views.LocationListView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;
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

    @Inject
    LocationRequest locationRequest;

    @Inject
    Utils utils;

    List<Location> presenterCache;
    private android.location.Location lastKnownMyLocation;

    public ListPresenter() {
        AroundSydneyApplication.getAppComponent().inject(this);
    }


    public ListPresenter(LocationModel mockedLocationModel, Utils mockedUtils, ReactiveLocationProvider mockedLocationProvider, Context mockedContext, LocationRequest mockedLocationRequest) {
        this.locationModel = mockedLocationModel;
        this.locationProvider = mockedLocationProvider;
        this.context = mockedContext;
        this.locationRequest = mockedLocationRequest;
        this.utils = mockedUtils;
    }


    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        requestLocations();
        initMyLocationProvider();
    }

    @SuppressLint("CheckResult")
    public void initMyLocationProvider() {
        if (utils.checkLocationPermission(context)) {
            locationProvider.getLastKnownLocation()
                    .subscribe(new Consumer<android.location.Location>() {
                        @Override
                        public void accept(android.location.Location location) {
                            lastKnownMyLocation = location;
                            updateListDataWithMyLocation(location);
                        }
                    });

            locationProvider.getUpdatedLocation(locationRequest)
                    .subscribe(new Consumer<android.location.Location>() {
                        @Override
                        public void accept(android.location.Location location) {
                            lastKnownMyLocation = location;
                            updateListDataWithMyLocation(location);
                        }
                    });
        }
    }

    void updateListDataWithMyLocation(android.location.Location myLocation) {
        if (presenterCache != null) {
            getViewState().clearList();
            getViewState().showListData(sortLocations(presenterCache, myLocation));
        }
    }

    @SuppressLint("CheckResult")
    void requestLocations() {
        Flowable<List<Location>> observer = locationModel.getLocations();
        if (observer != null) {
            observer.subscribe(new Consumer<List<Location>>() {
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
    }


    List<Location> sortLocations(List<Location> locations, final android.location.Location myLocation) {
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
