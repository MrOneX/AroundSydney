package com.test.aroundsydney.common;

import android.app.Application;

import com.test.aroundsydney.common.di.AppComponent;
import com.test.aroundsydney.common.di.DaggerAppComponent;
import com.test.aroundsydney.common.di.modules.ContextModule;
import com.test.aroundsydney.common.di.modules.LocationModule;
import com.test.aroundsydney.common.di.modules.RepositoryModule;
import com.test.aroundsydney.common.di.modules.RetrofitModule;

public class AroundSydneyApplication extends Application {

    private static AppComponent sAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        sAppComponent = DaggerAppComponent.builder()
                .contextModule(new ContextModule(this))
                .locationModule(new LocationModule())
                .repositoryModule(new RepositoryModule())
                .retrofitModule(new RetrofitModule())
                .build();

    }

    public static AppComponent getAppComponent() {
        return sAppComponent;
    }

}
