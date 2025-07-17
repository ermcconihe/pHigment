package com.example.phcameraapp;

import static android.content.Intent.getIntent;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;

import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

import android.widget.Button;
import android.widget.ImageView;

import android.os.Handler;
import android.util.Log; // Make sure you're using android.util.Log for Android logging
import androidx.media3.common.util.UnstableApi;

import com.bumptech.glide.Glide;
// Remove: import java.util.logging.Handler;


public class ProcessImage extends AppCompatActivity {
    private static final String TAG = "ProcessImage_DEBUG";
    private static final long DELAY_DURATION_MS = 5000; // 5 seconds
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable navigateToColorChartRunnable;
    private String imageUriString; // To pass to ColorChart

    @OptIn(markerClass = UnstableApi.class)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_image);
        String imageUriString = getIntent().getStringExtra("savedUri");
        if (imageUriString != null) {
            Uri imageUri = Uri.parse(imageUriString);
//            routeToColorChart(imageUri);
            // Now load and process the image using this URI
            // Glide.with(this).load(imageUri).into(yourImageView);
            // ... your image processing logic ...
        }
        // Find the ImageView first, then use it with Glide
        ImageView imageView1 = findViewById(R.id.rcjr93r1sdch);
        Glide.with(this)
                .load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/9rqxDX0LNX/qceuvivl_expires_30_days.png")
                .into(imageView1); // <--- Use the typed ImageView variable

        ImageView imageView2 = findViewById(R.id.rdax9j21u6x);
        Glide.with(this)
                .load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/9rqxDX0LNX/w67ps8k9_expires_30_days.png")
                .into(imageView2); // <--- Use the typed ImageView variable

//        ImageView imageView3 = findViewById(R.id.rijz5qzfl9kb);
//        Glide.with(this)
//                .load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/9rqxDX0LNX/5r3wh1zf_expires_30_days.png")
//                .into(imageView3); // <--- Use the typed ImageView variable
//

        // Get the image URI passed from ActivityCamera (or potentially MainActivity if flow changes)
        imageUriString = getIntent().getStringExtra("savedUri");
        if (imageUriString != null) {
            androidx.media3.common.util.Log.d(TAG, "onCreate: Received image URI: " + imageUriString);
        } else {
            androidx.media3.common.util.Log.e(TAG, "onCreate: Image URI not received. This might affect ColorChart.");
            // Decide how to handle if URI is missing. Maybe finish or show error.
        }

        // Find the button and set its click listener
//        Button btnGoToColorChart = findViewById(R.id.btnGoToColorChart);
//        if (btnGoToColorChart == null) {
//            Log.e(TAG, "onCreate: Button 'btnGoToColorChart' NOT FOUND! Check your layout XML.");
//        } else {
//            String finalImageUriString1 = imageUriString;
//            btnGoToColorChart.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.d(TAG, "btnGoToColorChart clicked. Navigating to ColorChart.");
//                    Intent intent = new Intent(ProcessImage.this, ColorChart.class);
//                    if (finalImageUriString1 != null) {
//                        intent.putExtra("imageUri", finalImageUriString1);
//                    }
//                    // Pass the pH value if MainActivity has calculated it and stored it statically
//                    // This relies on MainActivity.lastCalculatedPh being set before this button is clicked
//                    intent.putExtra("phValue", MainActivity.lastCalculatedPh);
//                    Log.d(TAG, "Passing to ColorChart: URI=" + finalImageUriString1 + ", pH=" + MainActivity.lastCalculatedPh);
//
//                    startActivity(intent);
//                    // Decide if you want to finish ProcessImage when navigating.
//                    // If the user can go back to ProcessImage, don't finish.
//                    // If ProcessImage is just a temporary step, then finish.
//                    finish();
//                }
//            });
//        }
//        Log.i(TAG, "Lifecycle: ProcessImage - onCreate() END");

        String finalImageUriString = imageUriString;
        navigateToColorChartRunnable = () -> {
            androidx.media3.common.util.Log.d(TAG, "Delay ended. Navigating to ColorChart.");
            Intent intent = new Intent(ProcessImage.this, ColorChart.class);
            if (finalImageUriString != null) {
                intent.putExtra("imageUri", finalImageUriString); // Pass the original image URI
                // If MainActivity has completed analysis and stored results somewhere accessible
                // (e.g. SharedPreferences, static fields, or a ViewModel),
                // ColorChart can retrieve them.
                // Or, if analysis results are small and ready, they could be passed here.
                // For simplicity, ColorChart might just need the imageUri to re-display the image,
                // and then fetch analysis results from a shared source if needed.
            }
            // Fetch the pH from MainActivity's static field
            intent.putExtra("phValue", MainActivity.lastCalculatedPh);
            // You could pass R, G, B values similarly if calculated
            Log.d(TAG, "Passing to ColorChart: URI=" + finalImageUriString + ", pH=" + MainActivity.lastCalculatedPh);

            startActivity(intent);
            finish(); // Finish ProcessImage activity
        };

//         Start the delay
        handler.postDelayed(navigateToColorChartRunnable, DELAY_DURATION_MS);
        androidx.media3.common.util.Log.d(TAG, "onCreate: Scheduled navigation to ColorChart in " + DELAY_DURATION_MS + "ms.");

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Important: Remove callbacks when activity is destroyed to prevent memory leaks
        // or crashes if the runnable executes after the activity is gone.
        if (handler != null && navigateToColorChartRunnable != null) {
            handler.removeCallbacks(navigateToColorChartRunnable);
            Log.d(TAG, "onDestroy: Removed navigateToColorChartRunnable callback.");
        }
    }
    private void routeToColorChart(Uri imageToDisplayUri) {
        Intent intent = new Intent(ProcessImage.this, ColorChart.class);
        intent.putExtra("imageUri", imageToDisplayUri.toString());
//        intent.putExtra("pHValue", pH);
//        intent.putExtra("redValue", r);
//        intent.putExtra("greenValue", g);
//        intent.putExtra("blueValue", b);
        startActivity(intent);
        finish(); // Typically, you'd finish ProcessImage.
    }



}
