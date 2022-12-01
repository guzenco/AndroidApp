package com.lab.model;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MeteorsApi
{
    @GET("gh4g-9sfh.json?")
    Call<Meteor[]> getMeteors(@Query("$where") String filter);
}
