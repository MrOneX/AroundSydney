package com.test.aroundsydney.models;

import com.test.aroundsydney.models.entitys.Location;

import java.util.List;

import io.reactivex.Observable;

public interface AppLocationModel {

    Observable<List<Location>> getLocations();

    void createOrUpdateLocation(Location location);

    float calculateDistanceBetweenMyLocations(Location location);

    List<Location> filterLocationForDuplicate(List<Location> locations);


}
