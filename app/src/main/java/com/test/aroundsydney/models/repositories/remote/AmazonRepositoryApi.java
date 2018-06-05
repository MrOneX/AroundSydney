package com.test.aroundsydney.models.repositories.remote;

import com.test.aroundsydney.models.entitys.AmazonLocationResponse;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface AmazonRepositoryApi {

    @GET("/test-locations")
    Observable<AmazonLocationResponse> getLocations();
}
