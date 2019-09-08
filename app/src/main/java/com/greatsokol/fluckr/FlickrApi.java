package com.greatsokol.fluckr;

import android.content.Context;
import android.text.Layout;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
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


    static void LoadPicturesList(final View viewToShowSnackbar, final FlickrImageListAdapter adapter){
        if (adapter.isLastPage() || adapter.isLoadingNow()) return;
        final WeakReference<View> refViewToShowSnackbar = new WeakReference<>(viewToShowSnackbar);
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
                        if (refViewToShowSnackbar.get()!=null)
                            Snackbar.make(refViewToShowSnackbar.get(),
                                    "This is JSON error",
                                    Snackbar.LENGTH_LONG).show();
                    }
                }
                adapter.stopLoading();
            }

            @Override
            public void OnError() {
                if (refViewToShowSnackbar.get()!=null)
                    Snackbar.make(refViewToShowSnackbar.get(),
                            "This is network error",
                            Snackbar.LENGTH_LONG).show();
            }
        }, request);
    }
}
