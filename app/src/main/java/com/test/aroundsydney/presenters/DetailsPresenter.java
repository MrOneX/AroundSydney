package com.test.aroundsydney.presenters;

import com.arellomobile.mvp.MvpPresenter;
import com.test.aroundsydney.common.AroundSydneyApplication;
import com.test.aroundsydney.models.AppLocationModel;
import com.test.aroundsydney.models.entitys.Location;
import com.test.aroundsydney.views.DetailsView;

import javax.inject.Inject;

public class DetailsPresenter extends MvpPresenter<DetailsView> {

    @Inject
    public AppLocationModel locationModel;

    public DetailsPresenter() {
        AroundSydneyApplication.getAppComponent().inject(this);
    }

    public DetailsPresenter(AppLocationModel locationModel) {
        this.locationModel = locationModel;
    }

    public float getDistanceForLocation(Location location) {
        return locationModel.calculateDistanceBetweenMyLocations(location);
    }

    public void updateLocationData(Location location) {
        locationModel.createOrUpdateLocation(location);
    }

}
