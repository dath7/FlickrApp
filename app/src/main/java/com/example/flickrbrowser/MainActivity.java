package com.example.flickrbrowser;

import android.content.Intent;
import android.os.Bundle;


import android.util.Log;
import android.view.View;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;



import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements GetFlickrJsonData.OnDataAvailable, RecyclerItemClickListener.OnRecyclerClickListener{
    private static final String TAG = "MainActivity";
    private FlickRecycleViewAdapter flickRecycleViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: onCreate: starts");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        activeToolbar(false);

        RecyclerView recyclerView = findViewById(R.id.recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this,this,recyclerView));


        flickRecycleViewAdapter = new FlickRecycleViewAdapter( new ArrayList<Photo>(),this);
        recyclerView.setAdapter(flickRecycleViewAdapter);



    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: starts");
        super.onResume();
        GetFlickrJsonData getFlickrJsonData = new GetFlickrJsonData("https://www.flickr.com/services/feeds/photos_public.gne","en-us",true,this);
        //getFlickrJsonData.executeOnSameThread("android, nougat");
        getFlickrJsonData.execute("android,nougat");
        Log.d(TAG, "onResume: ends");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        Log.d(TAG, "onCreateOptionsMenu() returned " + true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_search) {
            Intent intent = new Intent(this,SearchActivity.class);
            startActivity(intent);
        }
        Log.d(TAG, "onOptionsItemSelected() returned");
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onDataAvailable(List<Photo> data, DowloadStatus status) {
        Log.d(TAG, "onDataAvailable: start");
        if (status == DowloadStatus.OK) {
            flickRecycleViewAdapter.loadNewData(data);
        } else {
            Log.e(TAG, "onDataAvailable: failed with status " + status );
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        Log.d(TAG, "onItemClick: start");
    }

    @Override
    public void onItemLongClick(View view, int position) {
        Log.d(TAG, "onItemLongClick: start");

        Intent intent = new Intent(this, PhotoDetailActivity.class);
        intent.putExtra(PHOTO_TRANGSFER,flickRecycleViewAdapter.getPhoto(position));
        startActivity(intent);
    }
}
