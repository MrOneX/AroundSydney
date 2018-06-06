package com.test.aroundsydney.models;

import com.test.aroundsydney.models.entitys.Location;

import java.util.List;

import io.reactivex.Flowable;

public interface AppLocationModel {

    Flowable<List<Location>> getLocations();

    void createOrUpdateLocation(Location location);

}
