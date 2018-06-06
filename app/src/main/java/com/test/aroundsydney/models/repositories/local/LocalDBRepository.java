package com.test.aroundsydney.models.repositories.local;

import android.arch.persistence.room.Room;

import com.test.aroundsydney.common.AroundSydneyApplication;
import com.test.aroundsydney.models.entitys.Location;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class LocalDBRepository {

    private static final String DATABASE_NAME = "LOCATION_DB";
    private LocationDatabase movieDatabase;

    public LocalDBRepository() {
        movieDatabase = Room.databaseBuilder(AroundSydneyApplication.getAppComponent().getContext(),
                LocationDatabase.class, DATABASE_NAME)
                .build();
    }

    public Flowable<List<Location>> getLocationsAndSubscribe() {
        return movieDatabase.daoAccess().getAllLocationsWithSubscription()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Maybe<List<Location>> getLocations() {
        return movieDatabase.daoAccess().getAllLocations()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    public void saveLocation(final Location location) {
        //encapsulate multithreading in repository
        new Thread(new Runnable() {
            @Override
            public void run() {
                movieDatabase.daoAccess().insertLocationOrReplace(location);
            }
        }).start();
    }

    public void saveLocations(final List<Location> locations) {
        //encapsulate multithreading in repository
        new Thread(new Runnable() {
            @Override
            public void run() {
                movieDatabase.daoAccess().insertLocations(locations);
            }
        }).start();
    }

}
