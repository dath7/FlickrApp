package com.example.flickrbrowser;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

class FlickRecycleViewAdapter extends RecyclerView.Adapter<FlickRecycleViewAdapter.FlickrImageViewHolder> {

    private static final String TAG = "FlickRecycleViewAdapter";
    private List<Photo> photoList;
    private Context context;

    public FlickRecycleViewAdapter(List<Photo> photoList, Context context) {
        this.photoList = photoList;
        this.context = context;
    }

    @NonNull
    @Override
    public FlickrImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // called by layout manager when it need a new view
        Log.d(TAG, "onCreateViewHolder: new view requested");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.browse,parent,false);
        return new FlickrImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FlickrImageViewHolder holder, int position) {
        // Called by the layout manager when it wants new data in an existing row
        Photo photoItem = photoList.get(position);
        Log.d(TAG, "onBindViewHolder: " + photoItem.getTitle() + "-->" + position);
        Picasso.with(context).load(photoItem.getImage())
                .error(R.drawable.placeholder)
                .placeholder(R.drawable.placeholder)
                .into(holder.thumbnail);

        holder.title.setText(photoItem.getTitle());

    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: called");
        return ((photoList!=null) && (photoList.size() != 0) ? photoList.size() : 0);
    }
    void loadNewData(List<Photo> newPhotos) {
        photoList = newPhotos;
        notifyDataSetChanged();
    }
    public Photo getPhoto(int position) {
        return ((photoList!=null) && (photoList.size() != 0) ? photoList.get(position) : null);
    }


    static class FlickrImageViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "FlickrImageViewHolder";
        ImageView thumbnail = null;
        TextView title = null;

        public FlickrImageViewHolder(View itemView) {
            super(itemView);
            Log.d(TAG, "FlickrImageViewHolder: starts");
            this.thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            this.title = (TextView) itemView.findViewById(R.id.title);
        }
    }
}
