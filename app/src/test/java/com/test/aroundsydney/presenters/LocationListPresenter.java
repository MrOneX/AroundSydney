package com.test.aroundsydney.presenters;

import android.content.Context;

import com.google.android.gms.location.LocationRequest;
import com.test.aroundsydney.common.Utils;
import com.test.aroundsydney.models.LocationModel;
import com.test.aroundsydney.models.entitys.Location;
import com.test.aroundsydney.views.LocationListView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import pl.charmas.android.reactivelocation2.ReactiveLocationProvider;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LocationListPresenter {

    ListPresenter presenter;
    private LocationModel mockedLocationModel;
    private LocationListView mockedListView;
    private Utils mockedUtils;
    private ReactiveLocationProvider mockedLocationProvider;
    private Context mockedContext;
    private LocationRequest mockedLocationRequest;

    @Before
    public void init() {
        mockedLocationModel = mock(LocationModel.class);
        mockedUtils = mock(Utils.class);
        mockedLocationProvider = mock(ReactiveLocationProvider.class);
        mockedContext = mock(Context.class);
        mockedLocationRequest = new LocationRequest();

        presenter = new ListPresenter(mockedLocationModel, mockedUtils, mockedLocationProvider, mockedContext, mockedLocationRequest);
        presenter.presenterCache = new ArrayList<>();

        mockedListView = mock(LocationListView.class);
    }

    @Test
    public void test_initMyLocationProvider() {
        when(mockedLocationProvider.getLastKnownLocation())
                .thenReturn(Observable.<android.location.Location>empty());

        when(mockedLocationProvider.getUpdatedLocation(any(LocationRequest.class)))
                .thenReturn(Observable.<android.location.Location>empty());

        when(mockedUtils.checkLocationPermission(mockedContext)).thenReturn(false);
        presenter.initMyLocationProvider();
        verify(mockedLocationProvider, never()).getLastKnownLocation();
        verify(mockedLocationProvider, never()).getUpdatedLocation(any(LocationRequest.class));

        when(mockedUtils.checkLocationPermission(mockedContext)).thenReturn(true);
        presenter.initMyLocationProvider();
        verify(mockedLocationProvider).getLastKnownLocation();
        verify(mockedLocationProvider).getUpdatedLocation(any(LocationRequest.class));
    }

    @Test
    public void test_requestLocations() {
        when(mockedLocationModel.getLocations()).thenReturn(Flowable.<List<Location>>empty());

        presenter.requestLocations();
        verify(mockedLocationModel).getLocations();
    }


}
