package com.example.john.flapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by john on 5/25/15.
 */
public class FlickrPublic extends Object {
    String title;
    String link;
    String description;
    String modified;
    String generator;


    public FlickrPublic(JSONObject jsonPhoto) throws JSONException {
        this.title = (String) jsonPhoto.optString("title");
        this.link = (String) jsonPhoto.optString("link");
        this.description = (String) jsonPhoto.optString("description");
        this.modified = (String) jsonPhoto.optString("modified");
        this.generator = (String) jsonPhoto.optString("generator");
    }


    public static ArrayList<FlickrPublic> FlickrPhotos(String photoData)
            throws NullPointerException, JSONException {
        ArrayList<FlickrPublic> flickrPhotos = new ArrayList<FlickrPublic>();
        JSONObject jsonObject = new JSONObject(photoData);
        JSONArray jsonArray = jsonObject.optJSONArray("items");
        for(int i = 0; i < jsonArray.length(); i++) {
            JSONObject photo = (JSONObject) jsonArray.get(i);
            FlickrPublic flickrPhoto = new FlickrPublic(photo);
            flickrPhotos.add(flickrPhoto);
            Log.d("connection", photo.optString("media"));
        }
        return flickrPhotos;
    }
}
