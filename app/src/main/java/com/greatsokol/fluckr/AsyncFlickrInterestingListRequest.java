package com.greatsokol.fluckr;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import androidx.annotation.NonNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

class AsyncFlickrInterestingListRequest extends AsyncTask<Void, Void, ArrayList<FlickrImageListItem>> {
    private static final String TAG = "AsyncFlickrListRequest";
    private String mCacheDir;
    private AsyncFlickrInterestingListRequest.OnAnswerListener mListener;
    private String mSearchForString;
    private String mApiKey;
    private int mPerPage;
    private int mCurrentPage;

    public abstract static class OnAnswerListener{
        public abstract void OnAnswerReady(ArrayList<FlickrImageListItem> items);
        public abstract void OnGetPagesNumber(int number);
        public abstract void OnError();
    }

    AsyncFlickrInterestingListRequest(@NonNull final AsyncFlickrInterestingListRequest.OnAnswerListener listener, String searchFor,
                                      String ApiKey, int NumberPerPage, int CurrentPage, String cacheDir) {
        mListener = listener;
        mSearchForString = searchFor;
        mCacheDir = cacheDir;
        mApiKey = ApiKey;
        mPerPage = NumberPerPage;
        mCurrentPage = CurrentPage;
    }


    @Override
    protected ArrayList<FlickrImageListItem> doInBackground(Void... voids) {
        String list_request;
        final String extras = "description,url_t,url_m,url_n,url_b,url_k,url_h";
        if(mSearchForString.equals(""))
            list_request = String.format( Locale.getDefault(),
                    "https://www.flickr.com/services/rest/?method=" +
                            "flickr.interestingness.getList" +
                            "&api_key=%s" +
                            "&per_page=%d" +
                            "&page=%d" +
                            "&extras=%s"+
                            "&format=json" +
                            "&nojsoncallback=1",
                    mApiKey, mPerPage, mCurrentPage, extras);
        else
            list_request = String.format( Locale.getDefault(),
                    "https://www.flickr.com/services/rest/?method=" +
                            "flickr.photos.search" +
                            "&api_key=%s" +
                            "&per_page=%d" +
                            "&page=%d" +
                            "&extras=%s"+
                            "&format=json" +
                            "&nojsoncallback=1"+
                            "&text=%s",
                    mApiKey, mPerPage, mCurrentPage, extras, mSearchForString);

        JSONObject jsonObject;
        try {
            jsonObject = JSONReader.readJsonFromUrl(list_request);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }

        try {
            final ArrayList<FlickrImageListItem> items = new ArrayList<>();
            JSONObject jsonRoot = jsonObject.getJSONObject("photos");
            mListener.OnGetPagesNumber(jsonRoot.getInt("pages"));
            JSONArray jsonArray = jsonRoot.getJSONArray("photo");

            int pictures_count = jsonArray.length();
            Log.d(TAG, String.format("%d pictures arrived in JSON",pictures_count));
            for (int i=0; i < pictures_count; i++){
                try{
                    JSONObject onePicObject = (JSONObject) jsonArray.get(i);
                    String title = onePicObject.getString("title");
                    JSONObject jsonDetails = onePicObject.getJSONObject("description");
                    String details = jsonDetails.optString("_content","");

                    String thumbnailUrl = jsonGetFirstAvailableAttribute(onePicObject,
                            new String [] {"url_n", "url_m", "url_t"});
                    String fullsizeUrl = jsonGetFirstAvailableAttribute(onePicObject,
                            new String [] {"url_k", "url_h", "url_b"});



                    Bitmap bmp = ImageLoader.loadPicture(thumbnailUrl, mCacheDir, 320);
                    if (bmp!=null) {
                        items.add(new FlickrImageListItem(
                                title, details,
                                bmp, thumbnailUrl, fullsizeUrl));
                    } else Log.d(TAG, "Cant load picture");
                } catch (Exception e) {
                    e.printStackTrace();
                    // иногда ссылка на картинку неправильная,
                    // но это не повод не прокачивать остальные картинки
                }
            }
            return items;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<FlickrImageListItem> o) {
        super.onPostExecute(o);
        if (o != null) mListener.OnAnswerReady(o);
        else mListener.OnError();
    }

    private String jsonGetFirstAvailableAttribute(JSONObject jsonObject, String[] attrNames){
        for(String attrname : attrNames){
          String strAttribute = jsonObject.optString(attrname, "");
          if(!strAttribute.equals(""))
              return strAttribute;
        }
        return "";
    }
}
