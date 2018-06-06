package com.test.aroundsydney.presenters;

import android.content.Context;

import com.google.android.gms.location.LocationRequest;
import com.test.aroundsydney.common.Utils;
import com.test.aroundsydney.models.LocationModel;
import com.test.aroundsydney.models.entitys.Location;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import io.reactivex.Observable;
import pl.charmas.android.reactivelocation2.ReactiveLocationProvider;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DetailsPresenterTest {


    private LocationModel mockedLocationModel;
    private DetailsPresenter presenter;
    private ReactiveLocationProvider mockedLocationProvider;
    private Context mockedContext;
    private Utils mockedUtils;

    @Before
    public void init() {
        mockedLocationModel = mock(LocationModel.class);
        mockedLocationProvider = mock(ReactiveLocationProvider.class);
        mockedContext = mock(Context.class);
        mockedUtils = mock(Utils.class);
        presenter = new DetailsPresenter(mockedLocationModel, mockedLocationProvider, mockedContext, mockedUtils);
    }

    @Test
    public void test_updateLocationData() {
        presenter.updateLocationData(any(Location.class));
        verify(mockedLocationModel).createOrUpdateLocation(any(Location.class));
    }

    @Test
    public void test_getDistanceForLocation() {
        when(mockedLocationProvider.getLastKnownLocation())
                .thenReturn(Observable.<android.location.Location>empty());

        when(mockedUtils.checkLocationPermission(mockedContext)).thenReturn(false);
        presenter.getDistanceForLocation(any(Location.class));
        verify(mockedLocationProvider, never()).getLastKnownLocation();

        when(mockedUtils.checkLocationPermission(mockedContext)).thenReturn(true);
        presenter.getDistanceForLocation(any(Location.class));
        verify(mockedLocationProvider).getLastKnownLocation();
    }
}
