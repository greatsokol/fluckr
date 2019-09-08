package com.greatsokol.fluckr;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.nio.charset.Charset;

public class AsyncJsonReader extends AsyncTask<String, Void, JSONObject> {

    private WeakReference<OnAnswerListener> mListener;

    public abstract static class OnAnswerListener{
        public abstract void OnAnswerReady(JSONObject jsonObject);
        public abstract void OnError();
    }


    AsyncJsonReader(AsyncJsonReader.OnAnswerListener listener, String requestUrl){
        mListener = new WeakReference<>(listener);
        execute(requestUrl);
    }

    @Override
    protected JSONObject doInBackground(String... strings) {
        try {
            String request = strings[0];
            return readJsonFromUrl(request);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            OnAnswerListener l = mListener.get();
            if (l!=null)
                l.OnError();
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


    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    private static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            return new JSONObject(readAll(rd));
        }
    }
}
