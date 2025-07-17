package com.example.phcameraapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import android.widget.ImageView;

public class GetStarted extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);

        // Cast to ImageView
        ImageView imageView1 = findViewById(R.id.r5laoon66qwk);
        Glide.with(this)
                .load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/9rqxDX0LNX/uyvew8kc_expires_30_days.png")
                .into(imageView1);

        // Cast to ImageView
        ImageView imageView2 = findViewById(R.id.rrxn2ma80tmc);
        Glide.with(this)
                .load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/9rqxDX0LNX/a17h0kam_expires_30_days.png")
                .into(imageView2);

        View button1 = findViewById(R.id.rvbd4cmihwwc);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Pressed");
                Intent intent = new Intent(GetStarted.this, ActivityCamera.class);
                startActivity(intent);
                finish(); // Call finish if you don't want users to return to GetStarted via the back button
                // from ActivityCamera. If they should be able to, don't call finish().
            }
        });
    }
}

