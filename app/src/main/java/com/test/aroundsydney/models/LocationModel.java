package com.test.aroundsydney.models;

import android.annotation.SuppressLint;

import com.test.aroundsydney.common.AroundSydneyApplication;
import com.test.aroundsydney.models.entitys.Location;
import com.test.aroundsydney.models.repositories.local.LocalDBRepository;
import com.test.aroundsydney.models.repositories.remote.AmazonRepository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

public class LocationModel implements AppLocationModel {


    @Inject
    AmazonRepository amazonRepository;

    @Inject
    LocalDBRepository localDBRepository;


    public LocationModel() {
        AroundSydneyApplication.getAppComponent().inject(this);
        requestRemoteLocations();
    }

    public LocationModel(AmazonRepository amazonRepository, LocalDBRepository localDBRepository) {
        this.amazonRepository = amazonRepository;
        this.localDBRepository = localDBRepository;
    }


    @SuppressLint("CheckResult")
    void requestRemoteLocations() {
        Observable<List<Location>> observer = amazonRepository.getLocations();
        if (observer != null) {
            observer.subscribe(new Consumer<List<Location>>() {
                @Override
                public void accept(List<Location> locations) {
                    mergeLocations(locations);
                }
            });
        }
    }


    @SuppressLint("CheckResult")
    void mergeLocations(final List<Location> remoteLocations) {
        localDBRepository.getLocations()
                .subscribe(new Consumer<List<Location>>() {
                    @Override
                    public void accept(List<Location> localLocations) {
                        addToLocalRepositoryIfNotExist(localLocations, remoteLocations);
                    }
                });
    }


    void addToLocalRepositoryIfNotExist(List<Location> localLocations, final List<Location> remoteLocations) {
        if (!localLocations.isEmpty()) {
            for (Location remoteItem : remoteLocations) {
                boolean isFounded = false;
                for (Location localItem : localLocations) {
                    if (isLocationsEqual(localItem, remoteItem)) {
                        isFounded = true;
                    }
                }
                if (!isFounded) {
                    localDBRepository.saveLocation(remoteItem);
                }
            }
        } else {
            localDBRepository.saveLocations(remoteLocations);
        }
    }


    boolean isLocationsEqual(Location location1, Location location2) {
        return (location1.name.equals(location2.name) &&
                location1.longitude == location2.longitude &&
                location1.latitude == location2.latitude);
    }


    @Override
    public Flowable<List<Location>> getLocations() {
        return localDBRepository.getLocationsAndSubscribe();
    }


    @Override
    public void createOrUpdateLocation(Location location) {
        localDBRepository.saveLocation(location);
    }


}
