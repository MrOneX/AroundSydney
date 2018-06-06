package com.test.aroundsydney.presenters;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import com.arellomobile.mvp.MvpPresenter;
import com.test.aroundsydney.common.AroundSydneyApplication;
import com.test.aroundsydney.models.AppLocationModel;
import com.test.aroundsydney.models.entitys.Location;
import com.test.aroundsydney.views.DetailsView;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import pl.charmas.android.reactivelocation2.ReactiveLocationProvider;

public class DetailsPresenter extends MvpPresenter<DetailsView> {

    @Inject
    public AppLocationModel locationModel;

    @Inject
    ReactiveLocationProvider locationProvider;

    @Inject
    Context context;

    public DetailsPresenter() {
        AroundSydneyApplication.getAppComponent().inject(this);
    }

    public DetailsPresenter(AppLocationModel locationModel) {
        this.locationModel = locationModel;
    }

    @SuppressLint("CheckResult")
    public Observable<Float> getDistanceForLocation(final Location location) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return locationProvider.getLastKnownLocation()
                    .map(new Function<android.location.Location, Float>() {
                        @Override
                        public Float apply(android.location.Location myLocation) {
                            return calculateDistanceBetweenMyLocations(location, myLocation);
                        }
                    });

        }
        return null;
    }

    public void updateLocationData(Location location) {
        locationModel.createOrUpdateLocation(location);
    }

    private Float calculateDistanceBetweenMyLocations(Location location, android.location.Location myLocation) {
        float[] result1 = new float[3];
        android.location.Location.distanceBetween(myLocation.getLatitude(), myLocation.getLongitude(),
                location.latitude, location.longitude, result1);
        return result1[0] / 1000;
    }

}
