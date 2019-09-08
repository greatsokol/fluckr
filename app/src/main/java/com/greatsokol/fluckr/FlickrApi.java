package com.greatsokol.fluckr;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

class FlickrApi {
    private static final String API_KEY = "dcfa7bcdfe436387cefa172c2d3dc2ae";
    private static final String API_REQUEST =
                "https://www.flickr.com/services/rest/?method=" +
                        "flickr.interestingness.getList" +
                        "&api_key=%s" +
                        "&per_page=%d" +
                        "&page=%d" +
                        "&format=json" +
                        "&nojsoncallback=1";

    static final int FLICKR_PER_PAGE = 20;

    static class FlickrImageListItem {
        String mTitle;
        FlickrImageListItem(){}
        FlickrImageListItem(String title){setTitle(title);}
        void setTitle(String title) {
            mTitle = title;
        }
        String getTitle() {
            return mTitle;
        }
    }


    static void LoadPicturesList(final FlickrImageListAdapter adapter, final ArrayList<FlickrImageListItem> list, int page_number){
        final String request = String.format(Locale.getDefault(), API_REQUEST, API_KEY, FLICKR_PER_PAGE, page_number);
        AsyncJsonReader reader = new AsyncJsonReader(new AsyncJsonReader.OnAnswerListener(){
            @Override
            public void OnAnswerReady(JSONObject jsonObject) {
                if(jsonObject != null){
                    try {
                        JSONArray jsonArray = jsonObject.getJSONObject("photos").getJSONArray("photo");
                        for (int i=0; i < jsonArray.length(); i++){
                            JSONObject onePicObject = (JSONObject) jsonArray.get(i);
                            String id = onePicObject.getString("id");
                            String owner = onePicObject.getString("owner");
                            String secret = onePicObject.getString("secret");
                            String server = onePicObject.getString("server");
                            String farm = onePicObject.getString("farm");
                            String title = onePicObject.getString("title");
                            list.add(new FlickrImageListItem(title));
                        }
                        adapter.addItems(list);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, request);
    }
}
