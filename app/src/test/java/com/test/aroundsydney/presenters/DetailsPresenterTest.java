package com.test.aroundsydney.presenters;

import com.test.aroundsydney.models.LocationModel;
import com.test.aroundsydney.models.entitys.Location;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DetailsPresenterTest {


    private LocationModel locationModel;
    private DetailsPresenter presenter;

    @Before
    public void init() {
        locationModel = mock(LocationModel.class);
        presenter = new DetailsPresenter(locationModel);
    }

    @Test
    public void test_getDistanceForLocation() {
        presenter.getDistanceForLocation(any(Location.class));
        verify(locationModel).calculateDistanceBetweenMyLocations(any(Location.class));
    }

    @Test
    public void test_updateLocationData() {
        presenter.updateLocationData(any(Location.class));
        verify(locationModel).createOrUpdateLocation(any(Location.class));
    }
}
