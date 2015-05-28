package com.example.john.flapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.widget.ImageView;

public class FullscreenActivity extends Activity {

    PagerAdapter mPageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        //ArrayList<Bitmap> bmpList = new ArrayList<Bitmap>();
        Intent intent = getIntent();
        ImageView imageView = (ImageView)findViewById(R.id.fullscreen);
        Bundle extras = getIntent().getExtras();
        byte[] byteArray = extras.getByteArray("image");
        String title = extras.getString("title");
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        imageView.setImageBitmap(bmp);


    }
}
