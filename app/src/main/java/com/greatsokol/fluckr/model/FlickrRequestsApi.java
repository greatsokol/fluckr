package com.greatsokol.fluckr.model;

import com.greatsokol.fluckr.model.api.FlickrImageList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface FlickrRequestsApi {
    interface Interestingness {
        @GET("/services/rest/?method=flickr.interestingness.getList")
        Call<FlickrImageList> getList(
                @Query("api_key") String apiKey,
                @Query("date") String listDate,
                @Query("per_page") int imagesPerPage,
                @Query("page") int pageNumber,
                @Query("extras") String extras,
                @Query("format") String format,
                @Query("nojsoncallback") int noJsonCallback);
    }

    interface Search {
        @GET("/services/rest/?method=flickr.photos.search")
        Call<FlickrImageList> getList(
                @Query("api_key") String apiKey,
                @Query("text") String text,
                @Query("per_page") int imagesPerPage,
                @Query("page") int pageNumber,
                @Query("extras") String extras,
                @Query("format") String format,
                @Query("nojsoncallback") int noJsonCallback);
    }
}
