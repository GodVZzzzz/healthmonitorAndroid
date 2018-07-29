package com.example.as.healthmonitor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        ImageView imageView = (ImageView) findViewById(R.id.health_welcome_image);
        imageView.setImageResource(R.drawable.health_icon);
    }
}
