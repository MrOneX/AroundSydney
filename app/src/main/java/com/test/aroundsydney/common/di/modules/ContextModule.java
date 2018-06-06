package com.test.aroundsydney.common.di.modules;

import android.content.Context;

import com.test.aroundsydney.common.Utils;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ContextModule {
    private Context mContext;

    public ContextModule(Context context) {
        mContext = context;
    }

    @Provides
    @Singleton
    Context provideContext() {
        return mContext;
    }

    @Provides
    @Singleton
    Utils provideUtils() {
        return new Utils();
    }
}
