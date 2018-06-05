package com.test.aroundsydney.common.di.modules;

import com.test.aroundsydney.common.AppLocationListener;
import com.test.aroundsydney.models.AppLocationModel;
import com.test.aroundsydney.models.LocationModel;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class LocationModule {

    @Singleton
    @Provides
    public AppLocationModel provideLocationModel() {
        return new LocationModel();
    }

    @Singleton
    @Provides
    public AppLocationListener provideLocationListener() {
        return new AppLocationListener();
    }


}
