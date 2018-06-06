package com.test.aroundsydney.presenters;

import android.annotation.SuppressLint;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.google.android.gms.maps.model.LatLng;
import com.test.aroundsydney.common.AroundSydneyApplication;
import com.test.aroundsydney.models.AppLocationModel;
import com.test.aroundsydney.models.entitys.Location;
import com.test.aroundsydney.views.MapView;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;

@InjectViewState
public class MapPresenter extends MvpPresenter<MapView> {

    @Inject
    public AppLocationModel locationModel;

    public MapPresenter() {
        AroundSydneyApplication.getAppComponent().inject(this);
    }

    public MapPresenter(AppLocationModel locationModel) {
        this.locationModel = locationModel;
    }

    @SuppressLint("CheckResult")
    public void requestLocations() {
        locationModel.getLocations().subscribe(new Consumer<List<Location>>() {
            @Override
            public void accept(List<Location> locations) {
                getViewState().showPins(locations);
            }
        });
    }

    public void addCustomLocation(LatLng latLng, String name) {
        Location location = new Location(name, latLng.latitude, latLng.longitude);
        locationModel.createOrUpdateLocation(location);
    }

}
