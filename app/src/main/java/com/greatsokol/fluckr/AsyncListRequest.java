package com.greatsokol.fluckr;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

class AsyncListRequest extends AsyncTask<Void, Void, ArrayList<ImageListItem>> {
    private static final String TAG = "AsyncFlickrListRequest";
    private static final String API_KEY = "dcfa7bcdfe436387cefa172c2d3dc2ae";
    private static final int FLICKR_PER_PAGE = 24;
    private String mCacheDir;
    private AsyncListRequest.OnAnswerListener mListener;
    private String mSearchForString;
    private int mPage;
    private Date mDate;

    public abstract static class OnAnswerListener{
        public abstract void OnAnswerReady(ArrayList<ImageListItem> items);
        public abstract void OnError();
    }

    AsyncListRequest(@NonNull final AsyncListRequest.OnAnswerListener listener,
                     Date date,
                     int page,
                     String cacheDir,
                     String searchFor) {
        mListener = listener;
        mSearchForString = searchFor;
        mCacheDir = cacheDir;
        mPage = page;
        mDate = date;
    }


    @Override
    protected ArrayList<ImageListItem> doInBackground(Void... voids) {
        String list_request;
        final String extras = "description,url_t,url_m,url_n,url_b,url_k,url_h";
        if(mSearchForString.equals(""))
            list_request = String.format( Locale.getDefault(),
                    "https://www.flickr.com/services/rest/?method=" +
                            "flickr.interestingness.getList" +
                            "&api_key=%s" +
                            "&date=%s"+
                            "&per_page=%d" +
                            "&page=%d" +
                            "&extras=%s"+
                            "&format=json" +
                            "&nojsoncallback=1",
                    API_KEY,
                    ConstsAndUtils.DateToStr_yyyy_mm_dd(mDate),
                    FLICKR_PER_PAGE, mPage, extras);
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
                    API_KEY, FLICKR_PER_PAGE, mPage, extras, mSearchForString);

        try {
            JSONObject jsonObject = JSONReader.readJsonFromUrl(list_request);
            if (jsonObject==null)
                return null;
            final ArrayList<ImageListItem> items = new ArrayList<>();
            JSONObject jsonRoot = jsonObject.getJSONObject("photos");
            final int pagesTotal = jsonRoot.getInt("pages");
            final int currPage = jsonRoot.getInt("page");
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

                    Bitmap bmp = ImageLoader.loadPicture(thumbnailUrl, mCacheDir, ImageLoader.THUMB_SIZE);
                    if (bmp!=null) {
                        items.add(new ImageListItem(
                                mDate,
                                pagesTotal,
                                currPage,
                                i,
                                title, details,
                                bmp, thumbnailUrl, fullsizeUrl));
                    } else Log.d(TAG, "Can't load picture");
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
    protected void onPostExecute(ArrayList<ImageListItem> list) {
        super.onPostExecute(list);
        if (list != null) mListener.OnAnswerReady(list);
        else mListener.OnError();
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
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
