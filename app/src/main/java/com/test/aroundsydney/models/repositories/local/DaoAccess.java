package com.test.aroundsydney.models.repositories.local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.test.aroundsydney.models.entitys.Location;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Maybe;

@Dao
public interface DaoAccess {

    @Query("SELECT * FROM Location")
    Flowable<List<Location>> getAllLocationsWithSubscription();

    @Query("SELECT * FROM Location")
    Maybe<List<Location>> getAllLocations();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLocationOrReplace(Location location);

    @Insert
    void insertLocations(List<Location> location);


}