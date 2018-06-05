package com.test.aroundsydney.views;

import com.arellomobile.mvp.MvpView;
import com.test.aroundsydney.models.entitys.Location;

import java.util.List;

public interface LocationListView extends MvpView {


    void showListData(List<Location> locations);

    void clearList();
}
