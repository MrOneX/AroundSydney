package com.test.aroundsydney.models.entitys;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class AmazonLocationResponse {

    @SerializedName("locations")
    public List<Location> locations;

    @SerializedName("updated")
    public Date updated;
}
