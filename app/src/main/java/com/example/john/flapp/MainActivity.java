package com.example.john.flapp;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {
    //private static final String API_KEY = "e191b08c93cdc17ab14f6c6937cdfb10";
    //private static final String API_SECRET = "5188aa164ef85c88";
    Bitmap bitmap;
    ImageView imgview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new loadPublicImages().execute();
        imgview = (ImageView)findViewById(R.id.imgview);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

        return super.onOptionsItemSelected(item);
    }


    private class loadPublicImages extends AsyncTask<String, Void, ArrayList<Bitmap>>{
        private ProgressDialog progressDialog;
        private static final String API_KEY = "e191b08c93cdc17ab14f6c6937cdfb10";
        private static final String PUBLIC_ENDPOINT = "https://api.flickr.com/services/feeds/photos_public.gne?format=json";
        HttpURLConnection connection;
        ArrayList<Bitmap> bitmapList = new ArrayList<Bitmap>();
        //URL endpoint = new URL("https://api.flickr.com/services/feeds/photos_public.gne?format=json");
        //URL url = new URL("https://www.flickr.com/services/api/explore/flickr.photos.getRecent");

        @Override
        protected ArrayList<Bitmap> doInBackground(String... params) {
            ArrayList<String> flickrPhotoUrls = new ArrayList<String>();

            try {
                /*URL url = new URL("https://www.flickr.com/services/api/explore/flickr.photos.getRecent&api_key="
                                    + API_KEY
                                    +"&per_page=" + PER_PAGE
                                    + "&format=json"
                                    );*/

                // Connect to public photos endpoint
                URL url = new URL(PUBLIC_ENDPOINT);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // Read in endpoint response
                InputStream in = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder sb = new StringBuilder();
                String line;
                int counter = 1, START_LINE = 1;
                while((line = reader.readLine()) != null ) {
                    if(counter > START_LINE) {
                        sb = sb.append(line);
                        //Log.d("connection", "line: " + line);
                    }
                    counter++;
                }
                String publicPhotoData = "{" + sb.toString() + "}";

                // Create FlickrPublicPhoto object and grab the imageUrl
                try {
                    JSONObject jsonPhotoData = new JSONObject(publicPhotoData);
                    FlickrPublicPhoto flickrPublicphoto = new FlickrPublicPhoto(jsonPhotoData);
                    JSONArray jsonArray = jsonPhotoData.optJSONArray("items");
                    for(int i = 0; i < jsonArray.length(); i++) {
                        JSONObject photo = (JSONObject) jsonArray.get(i);
                        FlickrPublicPhoto flickrPhoto = new FlickrPublicPhoto(photo);
                        flickrPhotoUrls.add(flickrPhoto.getImageUrl());
                        //Log.d("connection", "FlickrPublicPhoto: " + flickrPhoto.getImageUrl());
                    }
                } catch (JSONException e) {
                    Log.d("connection", "JSONException main: " + e);
                }

                // Get bitmap
                try {
                    for(int i = 0; i < flickrPhotoUrls.size(); i++) {
                        Log.d("connection", flickrPhotoUrls.get(i));
                        URL imgUrl = new URL(flickrPhotoUrls.get(i));
                        bitmap = BitmapFactory.decodeStream(imgUrl.openStream());
                        bitmapList.add(bitmap);
                    }
                } catch (Exception e) {
                    Log.d("connection", "Exception: " + e);
                }
            } catch(MalformedURLException e) {
                System.err.println("Bad URL: " + e);
            } catch(IOException e) {
                System.err.println("IO Exception " + e);
            }
        return bitmapList;
        }

        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Loading public images from Flickr");
            progressDialog.show();
        }

        protected void onPostExecute(ArrayList<Bitmap> bmpList) {
            progressDialog.dismiss();
            Log.d("connection", "bmp" + bmpList.toString());
            imgview.setImageBitmap(bmpList.get(0));
            super.onPostExecute(bmpList);
        }

    }
}
