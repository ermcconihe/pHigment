package com.example.phcameraapp;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import android.widget.ImageView;
import android.util.Log;
import android.widget.TextView;

public class Results extends AppCompatActivity {

    public TextView pHValue;
    public TextView pHAnalysisValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        Log.d("Results", "onCreate called, MainActivity.lastCalculatedPh: " + MainActivity.lastCalculatedPh);

        StringBuilder analysisResultsText = new StringBuilder();
        pHValue = findViewById(R.id.pHValue); // Matches XML ID
        Log.d(TAG, "onCreate: pHValue initialized: " + (pHValue != null));

        pHValue.setText(String.valueOf(MainActivity.lastCalculatedPh));
        Log.d(TAG, "onCreate: pHValue set " );

        pHAnalysisValue = findViewById(R.id.pHAnalysisValue);
        if (MainActivity.lastCalculatedPh < 6.0) {
            Log.d(TAG, "onCreate: pHValue is < 4 " );
            pHAnalysisValue.setText("Low");
        }
        else if (MainActivity.lastCalculatedPh > 7.5) {
            Log.d(TAG, "onCreate: pHValue is > 7 " );
            pHAnalysisValue.setText("High");
        }
        else {
            Log.d(TAG, "onCreate: pHValue is normal " );
            pHAnalysisValue.setText("Normal");
        }
        // Find the ImageView first, then use it with Glide
        ImageView imageView1 = findViewById(R.id.r2ehtb5xzelk);
        Glide.with(this)
                .load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/9rqxDX0LNX/uvheynfh_expires_30_days.png")
                .into(imageView1); // <--- Use the typed ImageView variable

        ImageView imageView2 = findViewById(R.id.rl7588wdkhd);
        Glide.with(this)
                .load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/9rqxDX0LNX/t0zkufed_expires_30_days.png")
                .into(imageView2); // <--- Use the typed ImageView variable

//        ImageView imageView3 = findViewById(R.id.ra07via2hk7l);
//        Glide.with(this)
//                .load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/9rqxDX0LNX/qghvppys_expires_30_days.png")
//                .into(imageView3); // <--- Use the typed ImageView variable

        View button1 = findViewById(R.id.rk9eukme4qmd);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Pressed");
                Intent intent = new Intent(Results.this, GetStarted.class);
                // Critical: Clear the task stack to make GetStarted the new root.
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish(); // Finish Results activity.
            }
        });
    }
}
