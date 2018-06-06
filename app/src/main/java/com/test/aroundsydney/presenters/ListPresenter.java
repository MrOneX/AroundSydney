package com.test.aroundsydney.presenters;

import android.annotation.SuppressLint;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.test.aroundsydney.common.AppLocationListener;
import com.test.aroundsydney.common.AroundSydneyApplication;
import com.test.aroundsydney.models.AppLocationModel;
import com.test.aroundsydney.models.entitys.Location;
import com.test.aroundsydney.views.LocationListView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;

@InjectViewState
public class ListPresenter extends MvpPresenter<LocationListView> {

    @Inject
    public AppLocationModel locationModel;

    @Inject
    public AppLocationListener locationListener;

    public List<Location> presenterCache;

    public ListPresenter() {
        AroundSydneyApplication.getAppComponent().inject(this);
    }

    public ListPresenter(AppLocationModel locationModel) {
        this.locationModel = locationModel;
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        requestLocations();
    }

    @SuppressLint("CheckResult")
    public void requestLocations() {
        locationModel.getLocations().subscribe(new Consumer<List<Location>>() {
            @Override
            public void accept(List<Location> locations) {
                presenterCache = locations;
                getViewState().clearList();
                if (locationListener.myLocation != null) {
                    getViewState().showListData(sortLocations(locations, locationListener.myLocation));
                } else {
                    getViewState().showListData(locations);
                }
            }
        });
    }


    public void updateLocationDistance() {
        if (presenterCache == null || locationListener.myLocation == null)
            return;
        List<Location> sortedCache = sortLocations(presenterCache, locationListener.myLocation);
        getViewState().clearList();
        getViewState().showListData(sortedCache);
    }


    public List<Location> sortLocations(List<Location> locations, final android.location.Location myLocation) {
        Comparator comp = new Comparator<Location>() {

            @Override
            public int compare(Location o, Location o2) {
                float[] result1 = new float[3];
                android.location.Location.distanceBetween(myLocation.getLatitude(), myLocation.getLongitude(),
                        o.latitude, o.longitude, result1);
                Float distance1 = result1[0] / 1000;
                o.distance = distance1;

                float[] result2 = new float[3];
                android.location.Location.distanceBetween(myLocation.getLatitude(), myLocation.getLongitude(),
                        o2.latitude, o2.longitude, result2);
                Float distance2 = result2[0] / 1000;
                o2.distance = distance2;

                return distance1.compareTo(distance2);
            }
        };
        Collections.sort(locations, comp);
        return locations;
    }
}
