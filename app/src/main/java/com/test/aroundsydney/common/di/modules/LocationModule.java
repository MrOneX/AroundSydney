package com.test.aroundsydney.common.di.modules;

import android.content.Context;

import com.google.android.gms.location.LocationRequest;
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
    AppLocationModel provideLocationModel() {
        return new LocationModel();
    }

    @Singleton
    @Provides
    ReactiveLocationProvider provideReactiveLocationProvider(Context context) {
        return new ReactiveLocationProvider(context);
    }

    @Singleton
    @Provides
    LocationRequest provideLocationRequest() {
        return LocationRequest.create() //standard GMS LocationRequest
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(60 * 1000);

    }
}
