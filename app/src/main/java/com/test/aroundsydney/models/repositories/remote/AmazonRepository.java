package com.test.aroundsydney.models.repositories.remote;

import com.test.aroundsydney.common.AroundSydneyApplication;
import com.test.aroundsydney.models.entitys.AmazonLocationResponse;
import com.test.aroundsydney.models.entitys.Location;

import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


public class AmazonRepository {

    @Inject
    public AmazonRepositoryApi amazonRepositoryApi;

    public AmazonRepository() {
        AroundSydneyApplication.getAppComponent().inject(this);
    }

    //save response for reduce remote api call, can be improved with timestamp synchronization
    private AmazonLocationResponse amazonLocationResponse;

    public Observable<List<Location>> getLocations() {

        if (amazonLocationResponse == null) {
            return amazonRepositoryApi.getLocations()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map(new Function<AmazonLocationResponse, List<Location>>() {
                        @Override
                        public List<Location> apply(AmazonLocationResponse locationResponse) {
                            for (Location item : locationResponse.locations) {
                                item.isItemFromRemote = true;
                            }
                            amazonLocationResponse = locationResponse;
                            return amazonLocationResponse.locations;
                        }
                    });
        } else {
            return Observable.fromCallable(new Callable<List<Location>>() {
                @Override
                public List<Location> call() {
                    return amazonLocationResponse.locations;
                }
            });
        }
    }

}
