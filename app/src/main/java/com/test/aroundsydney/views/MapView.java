package com.test.aroundsydney.views;

import com.arellomobile.mvp.MvpView;
import com.test.aroundsydney.models.entitys.Location;

import java.util.List;

public interface MapView extends MvpView {

    void showPins(List<Location> locations);

    void addPin(Location location);

}
