package com.test.aroundsydney.models;

import com.test.aroundsydney.models.entitys.Location;
import com.test.aroundsydney.models.repositories.local.LocalDBRepository;
import com.test.aroundsydney.models.repositories.remote.AmazonRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;

import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
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
        locationModel = new LocationModel(mockedAmazonRepository, mockedLocalRepository);
    }


    @Test
    public void test_requestRemoteLocations() {
        when(mockedAmazonRepository.getLocations()).thenReturn(Observable.<List<Location>>empty());

        locationModel.requestRemoteLocations();

        verify(mockedAmazonRepository).getLocations();
    }


    @Test
    public void test_getLocations() {
        when(mockedLocalRepository.getLocationsAndSubscribe()).thenReturn(Flowable.<List<Location>>empty());

        locationModel.getLocations();

        verify(mockedLocalRepository).getLocationsAndSubscribe();
    }

    @Test
    public void test_createOrUpdateLocation() {
        Location location = new Location("Fake name", 1, 1);
        locationModel.createOrUpdateLocation(location);

        verify(mockedLocalRepository).saveLocation(location);
    }

    @Test
    public void test_isLocationsEqual() {
        assertTrue(locationModel.isLocationsEqual(new Location("Fake name", 1, 1),
                new Location("Fake name", 1, 1)));

        assertFalse(locationModel.isLocationsEqual(new Location("Fake name", 1, 1),
                new Location("Fake name", 1, 0)));

        assertFalse(locationModel.isLocationsEqual(new Location("Fake name1", 1, 1),
                new Location("Fake name", 1, 1)));

        assertFalse(locationModel.isLocationsEqual(new Location("Fake name1", 1, 1),
                new Location("Fake name", 0, 1)));

    }

    @Test
    public void test_addToLocalRepositoryIfNotExist() {
        // all object are exist on local repository
        ArrayList<Location> localRepositoryLocations = new ArrayList<>();
        localRepositoryLocations.add(new Location("Fake Name 1", 0, 0));
        localRepositoryLocations.add(new Location("Fake Name 2", 1, 0));
        localRepositoryLocations.add(new Location("Fake Name 3", 1, 1));

        ArrayList<Location> remoteRepositoryLocations = new ArrayList<>();
        remoteRepositoryLocations.add(new Location("Fake Name 1", 0, 0));
        remoteRepositoryLocations.add(new Location("Fake Name 2", 1, 0));
        remoteRepositoryLocations.add(new Location("Fake Name 3", 1, 1));

        locationModel.addToLocalRepositoryIfNotExist(localRepositoryLocations, remoteRepositoryLocations);
        verify(mockedLocalRepository, never()).saveLocation(any(Location.class));
        verify(mockedLocalRepository, never()).saveLocations(anyList());

        //all objects are not exist in local repository
        ArrayList<Location> localRepositoryLocations2 = new ArrayList<>();
        localRepositoryLocations2.add(new Location("Fake Name 1", 0, 0));
        localRepositoryLocations2.add(new Location("Fake Name 2", 1, 0));
        localRepositoryLocations2.add(new Location("Fake Name 3", 1, 1));

        ArrayList<Location> remoteRepositoryLocations2 = new ArrayList<>();
        remoteRepositoryLocations2.add(new Location("Fake Name 4", 0, 0));
        remoteRepositoryLocations2.add(new Location("Fake Name 5", 1, 0));
        remoteRepositoryLocations2.add(new Location("Fake Name 6", 1, 1));

        locationModel.addToLocalRepositoryIfNotExist(localRepositoryLocations2, remoteRepositoryLocations2);
        verify(mockedLocalRepository, times(3)).saveLocation(any(Location.class));
        verify(mockedLocalRepository, never()).saveLocations(anyList());

        //local repository is empty
        ArrayList<Location> remoteRepositoryLocations1 = new ArrayList<>();
        remoteRepositoryLocations1.add(new Location("Fake Name 1", 0, 0));
        remoteRepositoryLocations1.add(new Location("Fake Name 2", 1, 0));
        remoteRepositoryLocations1.add(new Location("Fake Name 3", 1, 1));

        locationModel.addToLocalRepositoryIfNotExist(new ArrayList<Location>(), remoteRepositoryLocations1);
        verify(mockedLocalRepository).saveLocations(anyList());
    }
}
