package com.test.aroundsydney.common.di.modules;

import com.test.aroundsydney.models.repositories.local.LocalDBRepository;
import com.test.aroundsydney.models.repositories.remote.AmazonRepository;
import com.test.aroundsydney.models.repositories.remote.AmazonRepositoryApi;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reactivex.annotations.NonNull;
import retrofit2.Retrofit;

@Module
public class RepositoryModule {


    @Singleton
    @NonNull
    @Provides
    LocalDBRepository provideDBRepository() {
        return new LocalDBRepository();
    }

    @Singleton
    @NonNull
    @Provides
    AmazonRepository provideAmazonRepository() {
        return new AmazonRepository();
    }

    @Provides
    @Singleton
    AmazonRepositoryApi provideAmazonRepositoryApi(Retrofit retrofit) {
        return retrofit.create(AmazonRepositoryApi.class);
    }
}
