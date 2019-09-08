package com.greatsokol.fluckr;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    static final int FLICKR_PER_PAGE = 40;


    static void LoadPicturesList(final FlickrImageListAdapter adapter){
        if (adapter.isLastPage() || adapter.isLoadingNow()) return;
        adapter.startLoading();
        final int page_number = adapter.getCurrentPage();
        final String request = String.format(Locale.getDefault(), API_REQUEST, API_KEY, FLICKR_PER_PAGE, page_number);
        AsyncJsonReader reader = new AsyncJsonReader(new AsyncJsonReader.OnAnswerListener(){
            @Override
            public void OnAnswerReady(JSONObject jsonObject) {
                if(jsonObject != null){
                    try {
                        final ArrayList<FlickrImageListItem> items = new ArrayList<>();
                        JSONObject jsonRoot = jsonObject.getJSONObject("photos");
                        adapter.setTotalPage(jsonRoot.getInt("pages"));
                        JSONArray jsonArray = jsonRoot.getJSONArray("photo");
                        for (int i=0; i < jsonArray.length(); i++){
                            JSONObject onePicObject = (JSONObject) jsonArray.get(i);
                            String id = onePicObject.getString("id");
                            String owner = onePicObject.getString("owner");
                            String secret = onePicObject.getString("secret");
                            String server = onePicObject.getString("server");
                            String farm = onePicObject.getString("farm");
                            String title = onePicObject.getString("title");
                            items.add(new FlickrImageListItem(title));
                        }
                        adapter.addItems(items);
                        adapter.setCurrentPage(page_number+1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapter.stopLoading();
            }
        }, request);
    }
}
