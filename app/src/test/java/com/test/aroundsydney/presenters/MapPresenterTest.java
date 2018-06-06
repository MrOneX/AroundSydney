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

import io.reactivex.Flowable;
import io.reactivex.Observable;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MapPresenterTest {


    private LocationModel mockedLocationModel;
    private MapPresenter mapPresenter;

    @Before
    public void init() {
        mockedLocationModel = mock(LocationModel.class);
        mapPresenter = new MapPresenter(mockedLocationModel);
    }


    @Test
    public void test_requestLocations() {
        when(mockedLocationModel.getLocations()).thenReturn(Flowable.<List<Location>>empty());

        mapPresenter.requestLocations();
        verify(mockedLocationModel).getLocations();
    }

    @Test
    public void test_addCustomLocation() {
        mapPresenter.addCustomLocation(new LatLng(0, 0), "Fake name");

        ArgumentCaptor<Location> captor = ArgumentCaptor.forClass(Location.class);
        verify(mockedLocationModel).createOrUpdateLocation(captor.capture());

        assertEquals("Fake name", captor.getValue().name);
        assertEquals(0, captor.getValue().latitude, 0.1);
        assertEquals(0, captor.getValue().longitude, 0.1);

        Location capturedLocation = captor.getValue();
        verify(mockedLocationModel).createOrUpdateLocation(capturedLocation);
    }

}
