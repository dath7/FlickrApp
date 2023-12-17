package com.example.flickrbrowser;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
//https://www.flickr.com/services/feeds/photos_public.gne?tags=android,%20nougat%20&tagmode=ALL&lang=en-us&format=json&nojsoncallback=1
public class GetFlickrJsonData extends AsyncTask<String,Void,List<Photo>> implements GetRawData.OnDowloadComplete{
    private static final String TAG = "GetFlickrJsonData";

    private List<Photo> photoList = null;
    private String baseURL;
    private String languague;
    private boolean matchAll;

    private final OnDataAvailable callback;
    private boolean runningOnSameThread = false;

    interface OnDataAvailable {
        void onDataAvailable(List<Photo> data, DowloadStatus status);
    }

    public GetFlickrJsonData(String baseURL, String languague, boolean matchAll,OnDataAvailable callback) {
        Log.d(TAG, "GetFlickrJsonData called");
        this.baseURL = baseURL;
        this.languague = languague;
        this.matchAll = matchAll;
        this.callback = callback;
    }

    @Override
    protected void onPostExecute(List<Photo> photos) {
        Log.d(TAG, "onPostExecute: starts");
        if (callback != null) {
            callback.onDataAvailable(photoList,DowloadStatus.OK);
        }
    }

    @Override
    protected List<Photo> doInBackground(String... strings) {
        Log.d(TAG, "doInBackground starts");
        String destinationUri = createUri(strings[0],languague,matchAll);
        Log.d(TAG, "doInBackground: uri " + destinationUri);
        GetRawData getRawData = new GetRawData(this);
        getRawData.runInSameThread(destinationUri);
        Log.d(TAG, "doInBackground ends");
        return  photoList;
    }

    // error
    void executeOnSameThread(String searchCriteria) {
        Log.d(TAG, "executeOnSameThread starts");
        runningOnSameThread = true;
        String destinationUri = createUri(searchCriteria, languague, matchAll);

        GetRawData getRawData = new GetRawData(this);
        getRawData.execute(destinationUri);
        Log.d(TAG, "executeOnSameThread ends");
    }






    private String createUri(String searchCriteria,String lang,boolean matchAll) {
        Log.d(TAG, "createUri: starts");

        return Uri.parse(baseURL).buildUpon()
                .appendQueryParameter("tags",searchCriteria)
                .appendQueryParameter("tagmode",matchAll ? "ALL" : "ANY")
                .appendQueryParameter("lang",lang)
                .appendQueryParameter("format","json")
                .appendQueryParameter("nojsoncallback","1").build().toString();
    }

    @Override
    public void onDowloadComplete(String data, DowloadStatus status) {
        Log.d(TAG, "onDowloadComplete starts. Status = " + status);

        if (status == DowloadStatus.OK) {
            photoList = new ArrayList<>();

            try {
                JSONObject jsonData = new JSONObject(data);
                JSONArray itemArray = jsonData.getJSONArray("items");

                for (int i = 0; i < itemArray.length(); i++) {
                    JSONObject jsonPhoto = itemArray.getJSONObject(i);
                    String title = jsonPhoto.getString("title");
                    String author = jsonPhoto.getString("author");
                    String authorId = jsonPhoto.getString("author_id");
                    String tags = jsonPhoto.getString("tags");

                    JSONObject jsonMedia = jsonPhoto.getJSONObject("media");
                    String photoUrl = jsonMedia.getString("m");
                    // full-size photo
                    String link = photoUrl.replaceFirst("_m.", "_b.");

                    Photo photoObject = new Photo(title, author, authorId, link, tags, photoUrl);
                    Log.d(TAG, "onDowloadComplete: result " + photoObject.toString());
                    photoList.add(photoObject);
                    Log.d(TAG, "onDowloadComplete: " + photoObject.toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "onDowloadComplete: Error processing json data" + e.getMessage());
                status = DowloadStatus.FALED_OR_EMPTY;
            }

        }
        // run on samethread -> call back onDataAvailable
        if (runningOnSameThread && callback != null ) {
            callback.onDataAvailable(photoList,status);
        }

        Log.d(TAG, "onDowloadComplete: ends");
    }
}
