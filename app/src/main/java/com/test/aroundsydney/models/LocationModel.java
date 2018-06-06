package com.test.aroundsydney.models;

import android.annotation.SuppressLint;

import com.test.aroundsydney.common.AppLocationListener;
import com.test.aroundsydney.common.AroundSydneyApplication;
import com.test.aroundsydney.models.entitys.Location;
import com.test.aroundsydney.models.repositories.local.LocalDBRepository;
import com.test.aroundsydney.models.repositories.remote.AmazonRepository;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;

public class LocationModel implements AppLocationModel {


    @Inject
    AmazonRepository amazonRepository;
    @Inject
    LocalDBRepository localDBRepository;
    @Inject
    AppLocationListener locationListener;

    private List<Location> locationsCache = new ArrayList<>();

    public boolean isRemoteLocationLoading;
    public boolean isDataChanged;

    public LocationModel() {
        AroundSydneyApplication.getAppComponent().inject(this);
        requestRemoteLocations();
    }

    public LocationModel(AmazonRepository amazonRepository, LocalDBRepository localDBRepository, AppLocationListener locationListener) {
        this.amazonRepository = amazonRepository;
        this.localDBRepository = localDBRepository;
        this.locationListener = locationListener;
    }


    @SuppressLint("CheckResult")
    private void requestRemoteLocations() {
        amazonRepository.getLocations().subscribe(new Consumer<List<Location>>() {
            @Override
            public void accept(List<Location> locations) {
                mergeLocations(locations);
            }
        });
    }


    @SuppressLint("CheckResult")
    private void mergeLocations(final List<Location> remoteLocations) {
        localDBRepository.getLocations().subscribe(new Consumer<List<Location>>() {
            @Override
            public void accept(List<Location> localLocations) {
                if (!localLocations.isEmpty()) {
                    for (Location remoteItem : remoteLocations) {
                        boolean isFounded = false;
                        for (Location localItem : localLocations) {
                            if (isLocationsEqual(localItem, remoteItem)) {
                                isFounded = true;
                            }
                        }
                        if(!isFounded){
                            localDBRepository.saveLocation(remoteItem);
                        }
                    }
                } else {
                    localDBRepository.saveLocations(remoteLocations);
                }
            }
        });
    }


    @Override
    public Flowable<List<Location>> getLocations() {
        return localDBRepository.getLocationsAndSubscribe();
    }


    @Override
    public void createOrUpdateLocation(Location location) {
        isDataChanged = true;
        location.isItemFromRemote = false;
        localDBRepository.saveLocation(location);
    }


    private boolean isLocationsEqual(Location location1, Location location2) {
        return (location1.name.equals(location2.name) &&
                location1.longitude == location2.longitude &&
                location1.latitude == location2.latitude);
    }


    @Override
    public List<Location> filterLocationForDuplicate(final List<Location> locations) {
        CollectionUtils.filter(locations, new Predicate<Location>() {
            @Override
            public boolean evaluate(Location object) {
                int copesOnList = 0;
                for (Location compareItem : locations) {
                    if (isLocationsEqual(object, compareItem)) {
                        copesOnList++;
                    }
                }
                if (copesOnList <= 1) {
                    return true;
                } else {
                    return !object.isItemFromRemote;
                }
            }
        });
        return locations;
    }

    @Override
    public float calculateDistanceBetweenMyLocations(Location location) {
        if (locationListener.myLocation == null)
            return 0;
        float[] result1 = new float[3];
        android.location.Location.distanceBetween(locationListener.myLocation.getLatitude(), locationListener.myLocation.getLongitude(),
                location.latitude, location.longitude, result1);
        return result1[0] / 1000;
    }
}
