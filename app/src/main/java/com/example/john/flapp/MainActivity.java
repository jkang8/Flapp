package com.example.john.flapp;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new loadPublicImages().execute();
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

    private class loadPublicImages extends AsyncTask<String, Integer, JSONObject> {
        private ProgressDialog progressDialog;
        private Integer PER_PAGE = 10;
        private static final String API_KEY = "e191b08c93cdc17ab14f6c6937cdfb10";
        //URL endpoint = new URL("https://api.flickr.com/services/feeds/photos_public.gne?format=json");
        //URL url = new URL("https://www.flickr.com/services/api/explore/flickr.photos.getRecent");

        @Override
        protected JSONObject doInBackground(String... params) {
            HttpURLConnection connection;
            int status;
            try {
                /*URL url = new URL("https://www.flickr.com/services/api/explore/flickr.photos.getRecent&api_key="
                                    + API_KEY
                                    +"&per_page=" + PER_PAGE
                                    + "&format=json"
                                    );*/
                // Connect to public photos endpoint
                URL url = new URL("https://api.flickr.com/services/feeds/photos_public.gne?format=json");
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                status = connection.getResponseCode();
                Log.d("connection", "status " + status);

                // Read in endpoint response
                InputStream in = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder sb = new StringBuilder();
                String line;
                int counter = 1, START_LINE = 1;
                while((line = reader.readLine()) != null ) {
                    if(counter > START_LINE) {
                        sb = sb.append(line);
                        Log.d("connection", "line: " + line);
                    }
                    counter++;
                }

                String publicPhotoData = "{" + sb.toString() + "}";
                try {
                    JSONObject jsonPhotoData = new JSONObject(publicPhotoData);
                    FlickrPublic flickrPublic = new FlickrPublic(jsonPhotoData);
                    ArrayList<FlickrPublic> photoArray = flickrPublic.FlickrPhotos(publicPhotoData);
                    for(int i=0; i< photoArray.size(); ++i) {
                        Log.d("connection", "array: " + photoArray.get(i));
                    }
                } catch (JSONException e) {
                    Log.d("connection", "JSONException main: " + e);
                }
                /*
                try {
                    JSONObject jsonPublic = new JSONObject("{ " +publicPhotoData+ "}");
                    JSONArray photoArray = jsonPublic.optJSONArray("items");
                    for(int i = 0; i < photoArray.length(); i++) {
                        JSONObject photo = (JSONObject) photoArray.get(i);
                        Log.d("connection", photo.optString("title"));
                    }
                } catch (JSONException e) {
                    Log.d("connection", "JSONException: " + e);
                } */
            } catch(MalformedURLException e) {
                System.err.println("Bad URL: " + e);
            } catch(IOException e) {
                System.err.println("IO Exception " + e);
            }
        return null;
        }

        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Loading images from Flickr");
        }

    }
}
