package com.example.john.flapp;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by john on 5/26/15.
 */
class ImageAdapter extends BaseAdapter {

    private Context context;
    private int pos;
    private ArrayList<FlickrPublicPhoto> flickrPhotoList;

    public ImageAdapter(Context c, int pos, ArrayList<FlickrPublicPhoto> photos) {
        this.context = c;
        this.pos = pos;
        this.flickrPhotoList = photos;
        Log.d("connection", "ImgAdapter created");
    }

    @Override
    public int getCount() {
        return flickrPhotoList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        // convertView is old view to reuse
        if(convertView == null) {
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(345, 345));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(2, 2, 2, 2);
        } else {
            imageView = (ImageView) convertView;
        }
        imageView.setImageBitmap(flickrPhotoList.get(position).getBitmap());
        return imageView;
    }
}
