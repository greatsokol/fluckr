package com.greatsokol.fluckr;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

class AsyncFlickrApi extends AsyncTask<Void, Void, ArrayList<FlickrImageListItem>> {
    private String mCacheDir;
    private AsyncFlickrApi.OnAnswerListener mListener;
    private String mSearchForString;
    private String mApiKey;
    private int mPerPage;
    private int mCurrentPage;

    public abstract static class OnAnswerListener{
        public abstract void OnAnswerReady(ArrayList<FlickrImageListItem> items);
        public abstract void OnGetPagesNumber(int number);
        public abstract void OnError();
    }

    AsyncFlickrApi(@NonNull final AsyncFlickrApi.OnAnswerListener listener, String searchFor,
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
                    mApiKey, mPerPage, mCurrentPage, "description");
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
                    mApiKey, mPerPage, mCurrentPage, "description", mSearchForString);

        JSONObject jsonObject;
        try {
            jsonObject = JSONReader.readJsonFromUrl(list_request);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            mListener.OnError();
            return null;
        }

        //boolean bWasError = false;
        try {
            final ArrayList<FlickrImageListItem> items = new ArrayList<>();
            JSONObject jsonRoot = jsonObject.getJSONObject("photos");
            mListener.OnGetPagesNumber(jsonRoot.getInt("pages"));
            JSONArray jsonArray = jsonRoot.getJSONArray("photo");

            for (int i=0; i < jsonArray.length(); i++){
                try{
                    JSONObject onePicObject = (JSONObject) jsonArray.get(i);
                    String id = onePicObject.getString("id");
                    //String owner = onePicObject.getString("owner");
                    String secret = onePicObject.getString("secret");
                    String server = onePicObject.getString("server");
                    String farm = onePicObject.getString("farm");
                    String title = onePicObject.getString("title");
                    JSONObject jsonDetails = onePicObject.getJSONObject("description");
                    String details = jsonDetails.getString("_content");

                    String pic_request =
                            String.format("https://farm%s.staticflickr.com/%s/%s_%s_%s.jpg",
                                    farm,
                                    server,
                                    id,
                                    secret,
                                    "n");
                    String cacheFilePath = mCacheDir + "/" +
                            pic_request.replace(':', '_').
                                    replace('?','_').
                                    replace('&','_').
                                    replace('/','_').
                                    replace('.','_');
                    Bitmap bmp = ImageLoader.loadPicture(pic_request, cacheFilePath);
                    items.add(new FlickrImageListItem(title, details, bmp, cacheFilePath));
                } catch (Exception e) {
                    e.printStackTrace();
                    // иногда ссылка на картинку неправильная,
                    // но это не повод не прокачивать остальные картинки
                    //bWasError = true;
                }
            }
            //if(bWasError)mListener.OnError(); // ошибку показывать не буду
            return items;
        } catch (Exception e) {
            e.printStackTrace();
            mListener.OnError();
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<FlickrImageListItem> o) {
        super.onPostExecute(o);
        if (o != null) mListener.OnAnswerReady(o);
    }
}