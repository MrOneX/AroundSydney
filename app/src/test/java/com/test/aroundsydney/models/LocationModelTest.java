package com.test.aroundsydney.models;

import com.test.aroundsydney.common.AppLocationListener;
import com.test.aroundsydney.models.entitys.Location;
import com.test.aroundsydney.models.repositories.local.LocalDBRepository;
import com.test.aroundsydney.models.repositories.remote.AmazonRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LocationModelTest {


    private AmazonRepository mockedAmazonRepository;
    private LocalDBRepository mockedLocalRepository;
    private LocationModel locationModel;

    @Before
    public void init() {
        mockedAmazonRepository = mock(AmazonRepository.class);
        mockedLocalRepository = mock(LocalDBRepository.class);
        AppLocationListener mockedLocationListener = mock(AppLocationListener.class);
        locationModel = new LocationModel(mockedAmazonRepository, mockedLocalRepository, mockedLocationListener);
    }


    @Test
    public void test_requestAllLocations() {
        when(mockedAmazonRepository.getLocations()).thenReturn(Observable.<List<Location>>empty());
        when(mockedLocalRepository.getLocationsAndSubscribe()).thenReturn(Observable.<List<Location>>empty());

        locationModel.requestRemoteLocations();

        verify(mockedAmazonRepository).getLocations();
        verify(mockedLocalRepository).getLocationsAndSubscribe();

        assertFalse(locationModel.isDataChanged);
        assertFalse(locationModel.isRemoteLocationLoading);
    }


    @Test
    public void test_getLocations() {
        locationModel.fullDataObserver = Observable.empty();

        locationModel.isRemoteLocationLoading = true;
        locationModel.isDataChanged = true;
        assertThat(locationModel.getLocations(), is(notNullValue()));

        locationModel.isRemoteLocationLoading = true;
        locationModel.isDataChanged = false;
        assertThat(locationModel.getLocations(), is(notNullValue()));

        locationModel.isRemoteLocationLoading = false;
        locationModel.isDataChanged = false;
        assertThat(locationModel.getLocations(), is(notNullValue()));
    }

    @Test
    public void test_createOrUpdateLocation() {
        locationModel.createOrUpdateLocation(new Location("Fake Name 1", 0, 0));
        verify(mockedLocalRepository).saveLocation(any(Location.class));
        assertTrue(locationModel.isDataChanged);

    }

    @Test
    public void test_filterLocationForDuplicate() {
        //all objects different
        ArrayList<Location> listWithoutDuplicates = new ArrayList<>();
        listWithoutDuplicates.add(new Location("Fake Name 1", 0, 0));
        listWithoutDuplicates.add(new Location("Fake Name 2", 1, 0));
        listWithoutDuplicates.add(new Location("Fake Name 3", 1, 1));
        int sizeBeforeFilter = listWithoutDuplicates.size();
        assertEquals(sizeBeforeFilter, locationModel.filterLocationForDuplicate(listWithoutDuplicates).size());

        //few objects the same
        ArrayList<Location> listWithDuplicates = new ArrayList<>();
        listWithDuplicates.add(new Location("Fake Name 1", 0, 0));
        listWithDuplicates.add(new Location("Fake Name 2", 1, 0));
        listWithDuplicates.add(new Location("Fake Name 3", 1, 1));
        listWithDuplicates.add(new Location("Fake Name 3", 1, 1));

        Location location = new Location("Fake Name 3", 1, 1);
        location.isItemFromRemote = true;
        listWithDuplicates.add(location);

        int sizeBeforeFilter1 = listWithDuplicates.size();
        assertTrue(sizeBeforeFilter1 != locationModel.filterLocationForDuplicate(listWithDuplicates).size());

    }
}
