package com.greatsokol.fluckr;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;

public class AsyncJsonReader extends AsyncTask<String, Void, JSONObject> {

    private WeakReference<OnAnswerListener> mListener;

    public abstract class OnAnswerListener{
        public abstract void OnAnswerReady(JSONObject jsonObject);
    }


    public AsyncJsonReader(AsyncJsonReader.OnAnswerListener listener, String requestUrl){
        mListener = new WeakReference<>(listener);
        execute(requestUrl);
    }

    @Override
    protected JSONObject doInBackground(String... strings) {
        try {
            String request = strings[0];
            return JsonReader.readJsonFromUrl(request);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        super.onPostExecute(jsonObject);
        OnAnswerListener l = mListener.get();
        if (l!=null)
            l.OnAnswerReady(jsonObject);
    }
}
