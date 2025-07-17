package com.example.phcameraapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.util.Log;
import androidx.media3.common.util.UnstableApi;

import com.bumptech.glide.Glide;
import android.widget.ImageView;
import android.widget.TextView;

public class ColorChart extends AppCompatActivity {
    private static final String TAG = "ColorChart_DEBUG";

    @OptIn(markerClass = UnstableApi.class)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_chart);

        // Find the ImageView first, then use it with Glide
        ImageView imageView1 = findViewById(R.id.runepuzz7pep);
        Glide.with(this)
                .load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/9rqxDX0LNX/1mvt3k5r_expires_30_days.png")
                .into(imageView1); // <--- Use the typed ImageView variable

        ImageView imageView2 = findViewById(R.id.rlndyr5g9rc);
        Glide.with(this)
                .load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/9rqxDX0LNX/wtcptv5z_expires_30_days.png")
                .into(imageView2); // <--- Use the typed ImageView variable

        String imageUriString = getIntent().getStringExtra("imageUri");
        double phValue = getIntent().getDoubleExtra("phValue", -1.0); // -1.0 is a default if not found

        Log.d(TAG, "Received from Intent: imageUri=" + imageUriString + ", phValue=" + phValue);

//        ImageView imageView3 = findViewById(R.id.ruk05dboh2tr);
//        Glide.with(this)
//                .load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/9rqxDX0LNX/nlyya7pw_expires_30_days.png")
//                .into(imageView3); // <--- Use the typed ImageView variable
//
//        ImageView imageView4 = findViewById(R.id.r7v0wav33s3b);
//        Glide.with(this)
//                .load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/9rqxDX0LNX/via4el7e_expires_30_days.png")
//                .into(imageView4); // <--- Use the typed ImageView variable

        View button1 = findViewById(R.id.rv0wbu08ekt);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Pressed");
                Intent intent = new Intent(ColorChart.this, Results.class);
                // You might want to pass data again, or Results activity might re-fetch/re-display
                // For instance, pass the URI of the image and the final pH.
//                 intent.putExtra("imageUri", currentImageUriString);
//                 intent.putExtra("finalPhValue", currentPhValue);
                startActivity(intent);
                finish(); // Usually, finish ColorChart.
            }
        });
    }

}
