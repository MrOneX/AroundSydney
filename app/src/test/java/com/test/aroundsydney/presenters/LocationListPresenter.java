package com.test.aroundsydney.presenters;

import com.test.aroundsydney.models.LocationModel;
import com.test.aroundsydney.models.entitys.Location;
import com.test.aroundsydney.views.LocationListView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import io.reactivex.Observable;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LocationListPresenter {

    ListPresenter presenter;
    private LocationModel mockedLocationModel;
    private LocationListView mockedListView;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        mockedLocationModel = mock(LocationModel.class);
        presenter = new ListPresenter(mockedLocationModel);
        presenter.locationListener = mock(AppLocationListener.class);

        mockedListView = mock(LocationListView.class);
    }

    @Test
    public void test_requestLocations() {
        final LocationModel mockedLocationModel = mock(LocationModel.class);
        final AppLocationListener mockedLocationListener = mock(AppLocationListener.class);
        final ListPresenter presenter = new ListPresenter(mockedLocationModel);
        presenter.locationListener = mockedLocationListener;

        final LocationListView mockedListView = mock(LocationListView.class);

        when(mockedLocationModel.getLocations()).thenReturn(Observable.<List<Location>>empty());
        presenter.attachView(mockedListView);

        assertThat(presenter.presenterCache, is(notNullValue()));

        verify(mockedLocationModel).getLocations();
        verify(mockedListView).showListData(presenter.presenterCache);
    }

    @Test
    public void test_updateLocationDistance() {
        presenter.updateLocationDistance();

        when(mockedLocationModel.getLocations()).thenReturn(Observable.<List<Location>>empty());
        presenter.attachView(mockedListView);

        verify(mockedListView).clearList();
        verify(mockedListView).showListData(anyList());
    }

}
