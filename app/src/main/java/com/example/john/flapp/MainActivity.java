package com.example.john.flapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {
    Bitmap bitmap;
    GridView gridView;
    mSwipeRefreshLayout swipeContainer;
    ArrayList<FlickrPublicPhoto> flickrPhotoList = new ArrayList<FlickrPublicPhoto>();
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadActivity();

        swipeContainer = (mSwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnChildScrollUpListener(new mSwipeRefreshLayout.OnChildScrollUpListener() {
            @Override
            public boolean canChildScrollUp() {
                return gridView.getFirstVisiblePosition() > 0 ||
                        gridView.getChildAt(0) == null ||
                        gridView.getChildAt(0).getTop() < 0;
            }
        });
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //progressDialog.cancel();
                finish();
                loadActivity();
                //startActivity(getIntent());
                //if (swipeContainer.isRefreshing()) {
                    //swipeContainer.setRefreshing(false);
                //}
            }
        });

    }

    private void loadActivity() {
        setContentView(R.layout.activity_main);
        new loadPublicImages().execute();
        startActivity(getIntent());

        // Individual grid click to fullscreen
        gridView = (GridView) findViewById(R.id.gridView);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("ONITEMCLICK", "LOL");
                Log.d("ONITEMCLICK", flickrPhotoList.get(position).title);
                Bitmap bmp = flickrPhotoList.get(position).getBitmap();
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, os);
                byte[] byteArray = os.toByteArray();
                Intent intent = new Intent(getApplicationContext(), FullscreenActivity.class);
                intent.putExtra("image", byteArray);
                startActivity(intent);
            }
        });
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


    private class loadPublicImages extends AsyncTask<String, Void, ArrayList<FlickrPublicPhoto>>{
        private static final String API_KEY = "e191b08c93cdc17ab14f6c6937cdfb10";
        private static final String PUBLIC_ENDPOINT = "https://api.flickr.com/services/feeds/photos_public.gne?format=json";
        HttpURLConnection connection;
        //ArrayList<Bitmap> bitmapList = new ArrayList<Bitmap>();
        ArrayList<String> flickrPhotoUrls = new ArrayList<String>();

        @Override
        protected ArrayList<FlickrPublicPhoto> doInBackground(String... params) {

            try {
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
                        flickrPhotoList.add(flickrPhoto);
                        Log.d("connection", "FlickrPublicPhoto: " + flickrPhoto.getImageUrl());
                    }
                } catch (JSONException e) {
                    Log.d("connection", "JSONException main: " + e);
                }

                // Get bitmap
                try {
                    for(int i = 0; i <= flickrPhotoList.size()+1; i++) {
                        Log.d("connection", flickrPhotoUrls.get(i));
                        URL imgUrl = new URL(flickrPhotoList.get(i).getImageUrl());
                        bitmap = BitmapFactory.decodeStream(imgUrl.openStream());
                        //bitmapList.add(bitmap);
                        flickrPhotoList.get(i).setBitmap(bitmap);
                    }
                } catch (Exception e) {
                    Log.d("connection", "Exception: " + e);
                }
                connection.disconnect();
            } catch(MalformedURLException e) {
                System.err.println("Bad URL: " + e);
            } catch(IOException e) {
                System.err.println("IO Exception " + e);
            }
        return flickrPhotoList;
        }

        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Loading images from Flickr");
            //progressDialog.show();
        }

        protected void onPostExecute(ArrayList<FlickrPublicPhoto> flickrPhotoList) {
            gridView = (GridView) findViewById(R.id.gridView);
            //gridView.setAdapter(null);
            gridView.setAdapter(new ImageAdapter(MainActivity.this, android.R.layout.simple_list_item_1, flickrPhotoList));
            //progressDialog.dismiss();
            super.onPostExecute(flickrPhotoList);
        }

    }
}
