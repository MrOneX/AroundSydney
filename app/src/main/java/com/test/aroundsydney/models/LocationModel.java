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
import java.util.concurrent.Callable;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public class LocationModel implements AppLocationModel {

    public Observable<List<Location>> fullDataObserver;

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
        requestAllLocations();
    }

    public LocationModel(AmazonRepository amazonRepository, LocalDBRepository localDBRepository, AppLocationListener locationListener) {
        this.amazonRepository = amazonRepository;
        this.localDBRepository = localDBRepository;
        this.locationListener = locationListener;
    }


    @SuppressLint("CheckResult")
    public Observable<List<Location>> requestAllLocations() {
        isRemoteLocationLoading = true;
        locationsCache.clear();

        fullDataObserver = localDBRepository
                .getLocations()
                .mergeWith(amazonRepository.getLocations());

        fullDataObserver.subscribe(new Consumer<List<Location>>() {
            @Override
            public void accept(List<Location> locations) {
                // save all retrieved location to cache
                locationsCache.addAll(locations);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) {

            }
        }, new Action() {
            @Override
            public void run() {
                // on complete loading all data
                isRemoteLocationLoading = false;
                isDataChanged = false;
            }
        });
        return fullDataObserver;
    }


    @Override
    public Observable<List<Location>> getLocations() {
        if (isRemoteLocationLoading) {
            // return already created observer for retrieve all locations
            return fullDataObserver;
        } else {
            if (isDataChanged) {
                // locations data updated, request all again
                return requestAllLocations();
            } else {
                // data was loaded to cache and no changes after, return cached data
                return Observable.fromCallable(new Callable<List<Location>>() {
                    @Override
                    public List<Location> call() {
                        return locationsCache;
                    }
                });
            }
        }
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
