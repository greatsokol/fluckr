package com.greatsokol.fluckr.service;

import com.greatsokol.fluckr.model.FlickrInterestingnessImageListModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface FlickrInterestingnessImageListApi {
    @GET("/services/rest/?method=flickr.interestingness.getList")
    Call<FlickrInterestingnessImageListModel> getList(
            @Query("api_key") String ApiKey,
            @Query("date") String ListDate,
            @Query("per_page") int ImagesPerPage,
            @Query("page") int PageNumber,
            @Query("extras") String Extras,
            @Query("format") String Format,
            @Query("nojsoncallback") int NoJsonCallback);
}
