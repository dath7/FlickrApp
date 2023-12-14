package com.example.flickrbrowser;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

enum DowloadStatus {IDLE, PROCESSING, NOT_INITIALISED, FALED_OR_EMPTY, OK}

public class GetRawData extends AsyncTask<String,Void,String> {
    private static final String TAG = "GetRawData";
    private DowloadStatus dowloadStatus;
    private final OnDowloadComplete callback;
    interface OnDowloadComplete {
        void onDowloadComplete(String data, DowloadStatus status);
    }
    public GetRawData(OnDowloadComplete callback) {
        Log.d(TAG, "GetRawData: called");
        this.dowloadStatus = DowloadStatus.IDLE;
        this.callback = callback;
    }
    void runInSameThread(String s) {
        Log.d(TAG, "runInSameThread: starts");
        if (callback != null) {
            String result = doInBackground(s);
            Log.d(TAG, "runInSameThread: result " + result);
            callback.onDowloadComplete(result,dowloadStatus);
        }
        Log.d(TAG, "runInSameThread: ends");
    }
    @Override
    protected void onPostExecute(String s) {
        // s: value return after execute doInBackground
        Log.d(TAG, "onPostExecute: parameter= " + s);
        if (callback != null) {
            callback.onDowloadComplete(s,dowloadStatus);
        }
    }

    @Override
    protected String doInBackground(String... strings) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        if (strings == null) {
            dowloadStatus = DowloadStatus.NOT_INITIALISED;
            return null;
        }
        try {
            dowloadStatus = DowloadStatus.PROCESSING;
            URL url = new URL(strings[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int response = connection.getResponseCode();
            Log.d(TAG, "doInBackground: The response code was " + response);
            StringBuilder result = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line;
            while (null != (line = reader.readLine())) {
                result.append(line).append("\n");
            }
            dowloadStatus = DowloadStatus.OK;
            return result.toString();
        } catch (MalformedURLException e) {
            Log.e(TAG, "doInBackground: Invalid URL " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "doInBackground: IO exception reading data " + e.getMessage());
        } catch (SecurityException e) {
            Log.e(TAG, "doInBackground: Security exception. Need permission? " + e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (reader!= null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, "doInBackground: Error closing stream " + e.getMessage());
                }
            }
        }
        dowloadStatus =DowloadStatus.FALED_OR_EMPTY;
        return null;
    }
}
