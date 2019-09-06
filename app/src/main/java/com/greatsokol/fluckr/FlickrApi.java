package com.greatsokol.fluckr;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class FlickrApi {
    private static final String API_KEY = "dcfa7bcdfe436387cefa172c2d3dc2ae";
    private static final String API_REQUEST =
                "https://www.flickr.com/services/rest/?method=" +
                        "flickr.interestingness.getList" +
                        "&api_key=%s" +
                        "&per_page=%d" +
                        "&page=%d" +
                        "&format=json" +
                        "&nojsoncallback=1";

    public static final int FLICKR_PER_PAGE = 20;


    public static boolean LoadPage(int page_number){
        String request = String.format(API_REQUEST, API_KEY, FLICKR_PER_PAGE, page_number);

        try {
            JSONObject jsonObject = JsonReader.readJsonFromUrl(request);
            JSONArray jsonPhotos = jsonObject.getJSONArray("photo/photos");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }
}
