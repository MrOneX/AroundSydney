package com.test.aroundsydney.presenters;

import com.google.android.gms.maps.model.LatLng;
import com.test.aroundsydney.models.LocationModel;
import com.test.aroundsydney.models.entitys.Location;
import com.test.aroundsydney.views.MapView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import io.reactivex.Observable;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MapPresenterTest {


    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void test_requestLocations() {
        final LocationModel mockedLocationModel = mock(LocationModel.class);
        final MapPresenter presenter = new MapPresenter(mockedLocationModel);

        presenter.locationModel = mockedLocationModel;
        when(mockedLocationModel.getLocations()).thenReturn(Observable.<List<Location>>empty());

        presenter.requestLocations();
        verify(mockedLocationModel).getLocations();
    }

    @Test
    public void test_addLocation() {
        final LocationModel mockedLocationModel = mock(LocationModel.class);
        final MapPresenter presenter = new MapPresenter(mockedLocationModel);

        final MapView mockedMapView = mock(MapView.class);
        presenter.attachView(mockedMapView);

        // ArgumentCaptor
        presenter.addCustomLocation(new LatLng(0, 0), "Fake name");

        ArgumentCaptor<Location> captor = ArgumentCaptor.forClass(Location.class);
        verify(mockedMapView).addPin(captor.capture());

        assertEquals("Fake name", captor.getValue().name);
        assertEquals(0, captor.getValue().latitude, 0.1);
        assertEquals(0, captor.getValue().longitude, 0.1);

        Location capturedLocation = captor.getValue();

        verify(mockedLocationModel).createOrUpdateLocation(capturedLocation);
    }

}
