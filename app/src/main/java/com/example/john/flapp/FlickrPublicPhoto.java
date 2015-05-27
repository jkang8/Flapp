package com.example.john.flapp;

import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by john on 5/25/15.
 */
public class FlickrPublicPhoto extends Object {
    String title;
    String link;
    String description;
    String media;
    String author;
    String imageUrl;
    Bitmap bitmap;

    public FlickrPublicPhoto(JSONObject jsonPhoto) throws JSONException {
        this.title = (String) jsonPhoto.optString("title");
        this.link = (String) jsonPhoto.optString("link");
        this.description = (String) jsonPhoto.optString("description");
        this.media = (String) jsonPhoto.optString("media");
        this.author = (String) jsonPhoto.optString("author");
    }

    public String getImageUrl() throws JSONException {
        JSONObject media = new JSONObject(this.media);
        String imageUrl = media.optString("m");
        return imageUrl;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
    public Bitmap getBitmap() {
        return this.bitmap;
    }

}
