package com.test.aroundsydney.common.di.modules;

import android.content.Context;

import com.test.aroundsydney.models.AppLocationModel;
import com.test.aroundsydney.models.LocationModel;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import pl.charmas.android.reactivelocation2.ReactiveLocationProvider;

@Module
public class LocationModule {

    @Singleton
    @Provides
    public AppLocationModel provideLocationModel() {
        return new LocationModel();
    }

    @Singleton
    @Provides
    public ReactiveLocationProvider provideReactiveLocationProvider(Context context) {
        return new ReactiveLocationProvider(context);
    }


}
