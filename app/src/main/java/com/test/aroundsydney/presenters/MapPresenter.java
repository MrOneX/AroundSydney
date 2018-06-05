package com.test.aroundsydney.presenters;

import android.annotation.SuppressLint;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.google.android.gms.maps.model.LatLng;
import com.test.aroundsydney.common.AroundSydneyApplication;
import com.test.aroundsydney.models.AppLocationModel;
import com.test.aroundsydney.models.entitys.Location;
import com.test.aroundsydney.views.MapView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.functions.Action;
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
        final List<Location> locationBuffer = new ArrayList<>();
        Observable<List<Location>> observable = locationModel.getLocations();
        observable.subscribe(new Consumer<List<Location>>() {
            @Override
            public void accept(List<Location> locations) {
                locationBuffer.addAll(locations);

            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) {
                //TODO process error
            }
        }, new Action() {
            @Override
            public void run() {
                getViewState().showPins(locationModel.filterLocationForDuplicate(locationBuffer));
            }
        });
    }


    public void addCustomLocation(LatLng latLng, String name) {
        Location location = new Location(name, latLng.latitude, latLng.longitude);
        locationModel.createOrUpdateLocation(location);
        getViewState().addPin(location);
    }

}
