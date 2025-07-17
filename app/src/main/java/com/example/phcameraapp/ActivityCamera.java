package com.example.phcameraapp;

import android.app.Activity;
import android.content.Intent; // Will need later
import android.net.Uri;      // Will need later
import android.os.Bundle;
import android.os.Environment; // Will need later
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull; // Will need later
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector; // Will need soon
import androidx.camera.core.ImageCapture;  // Will need soon
import androidx.camera.core.ImageCaptureException; // Will need later
import androidx.camera.core.Preview;        // Will need soon
import androidx.camera.lifecycle.ProcessCameraProvider; // Will need soon
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat; // Will need soon

import com.google.common.util.concurrent.ListenableFuture; // Will need soon

import java.io.File; // Will need later
import java.text.SimpleDateFormat; // Will need later
import java.util.Locale; // Will need later
import java.util.concurrent.ExecutionException; // Will need soon
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ActivityCamera extends AppCompatActivity {

    // Use your original TAG or the debug one, just be consistent
    private static final String TAG = "ActivityCamera_DEBUG";
    public static final String EXTRA_IMAGE_URI = "extra_image_uri"; // For returning result

    private PreviewView cameraPreviewView;
    private Button captureImageButton;

    private ImageCapture imageCapture; // Keep null for now
    private ExecutorService cameraExecutor;
    private ImageView cameraTargetOverlay;
    private static final String FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"; // For unique filenames
//    public static final String EXTRA_IMAGE_URI = "extra_image_uri"; // Already defined
// ... (your existing code: TAG, FILENAME_FORMAT, imageCapture, cameraExecutor, etc.)
    public static final String EXTRA_START_PROCESSING_IMMEDIATELY = "extra_start_processing_immediately";


    // ... (imageCapture should be initialized in bindCameraUseCases)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Method execution started.");
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: super.onCreate() CALLED SUCCESSFULLY.");

        try {
            // Make sure R.layout.activity_camera is your original camera layout
            setContentView(R.layout.activity_camera);
            Log.d(TAG, "onCreate: setContentView(R.layout.activity_camera) successful");

            cameraPreviewView = findViewById(R.id.cameraPreviewView);
            captureImageButton = findViewById(R.id.captureImageButton);
            Log.d(TAG, "onCreate: Views initialized via findViewById");

            if (cameraPreviewView == null) {
                Log.e(TAG, "onCreate: FATAL - cameraPreviewView is NULL after findViewById!");
                Toast.makeText(this, "Error: PreviewView not found.", Toast.LENGTH_LONG).show();
                setResult(RESULT_CANCELED);
                finish();
                return;
            }
            if (captureImageButton == null) {
                Log.e(TAG, "onCreate: FATAL - captureImageButton is NULL after findViewById!");
                Toast.makeText(this, "Error: CaptureButton not found.", Toast.LENGTH_LONG).show();
                setResult(RESULT_CANCELED);
                finish();
                return;
            }
            Log.d(TAG, "onCreate: Views successfully found.");

            // Initialize executor
            cameraExecutor = Executors.newSingleThreadExecutor();
            Log.d(TAG, "onCreate: cameraExecutor initialized");

            cameraTargetOverlay = findViewById(R.id.camera_target_overlay);

            // Set the alpha (transparency)
            // Alpha is a float value from 0.0 (fully transparent) to 1.0 (fully opaque)
            cameraTargetOverlay.setAlpha(0.85f); // 0.5f means 50% opaque (half transparent)

            // --- WE WILL ADD START CAMERA LATER ---
             startCamera();
             Log.d(TAG, "onCreate: startCamera() would be called here.");


            // --- WE WILL ADD BUTTON CLICK LISTENER LATER ---
             captureImageButton.setOnClickListener(v -> {
                 Log.d(TAG, "captureImageButton clicked");
                 takePhoto();
             });
             Log.d(TAG, "onCreate: captureImageButton OnClickListener would be set here.");


            Log.d(TAG, "onCreate: Minimal setup in onCreate finished successfully.");
            // For now, let's just make it stay on screen without doing anything else
            // If you want to test returning immediately:
            // setResult(RESULT_CANCELED); // Or RESULT_OK if you want to test that path
            // finish();

        } catch (Throwable t) {
            Log.e(TAG, "onCreate: CRITICAL ERROR IN ONCREATE!", t);
            Log.e(TAG, "Camera Activity crashed: " + t.getMessage());
            Toast.makeText(this, "Camera Activity crashed: " + t.getMessage(), Toast.LENGTH_LONG).show();
            if (!isFinishing()) {
                setResult(RESULT_CANCELED);
                finish();
            }
        }
    }

    private void startCamera() {
        Log.d(TAG, "startCamera: Attempting to start camera.");
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                Log.d(TAG, "startCamera: cameraProviderFuture listener executing.");
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Log.d(TAG, "startCamera: Got cameraProvider instance.");
                bindCameraUseCases(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "startCamera: Camera provider future FAILED.", e);
                Toast.makeText(this, "Error initializing camera provider: " + e.getMessage(), Toast.LENGTH_LONG).show();
                setResult(RESULT_CANCELED);
                finish();
            } catch (Exception e) { // Catch any other unexpected error from .get() or bind...
                Log.e(TAG, "startCamera: UNEXPECTED error in cameraProviderFuture listener.", e);
                Toast.makeText(this, "Unexpected error starting camera: " + e.getMessage(), Toast.LENGTH_LONG).show();
                setResult(RESULT_CANCELED);
                finish();
            }
        }, ContextCompat.getMainExecutor(this));
        Log.d(TAG, "startCamera: cameraProviderFuture.addListener submitted.");
    }
    private void bindCameraUseCases(@NonNull ProcessCameraProvider cameraProvider) {
        Log.d(TAG, "bindCameraUseCases: Attempting to bind use cases.");

        Preview preview = new Preview.Builder().build();
        Log.d(TAG, "bindCameraUseCases: Preview builder created.");

        // Ensure cameraPreviewView is not null from onCreate check
        if (cameraPreviewView == null) {
            Log.e(TAG, "bindCameraUseCases: cameraPreviewView is NULL. Cannot set surface provider.");
            Toast.makeText(this, "Error: Preview is null in bind.", Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED);
            finish();
            return;
        }
        preview.setSurfaceProvider(cameraPreviewView.getSurfaceProvider());
        Log.d(TAG, "bindCameraUseCases: Surface provider set for Preview.");

        imageCapture = new ImageCapture.Builder().build(); // Initialize imageCapture here
        Log.d(TAG, "bindCameraUseCases: ImageCapture builder created.");

        CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
        Log.d(TAG, "bindCameraUseCases: CameraSelector set to DEFAULT_BACK_CAMERA.");

        try {
            cameraProvider.unbindAll();
            Log.d(TAG, "bindCameraUseCases: All use cases unbound.");
            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture); // Add imageCapture
            Log.d(TAG, "bindCameraUseCases: Use cases (Preview, ImageCapture) BOUND successfully.");
        } catch (Exception e) {
            Log.e(TAG, "bindCameraUseCases: Use case binding FAILED.", e);
            Toast.makeText(this, "Error binding camera use cases: " + e.getMessage(), Toast.LENGTH_LONG).show();
            setResult(RESULT_CANCELED);
            finish();
        }
    }
    // --- takePhoto() still commented out or empty ---
    // Inside ActivityCamera.java

// In ActivityCamera.java

//    private void takePhoto() {
//        Log.d(TAG, "takePhoto: Method called.");
//
//        if (imageCapture == null) {
//            Log.e(TAG, "takePhoto: ImageCapture use case is NULL. Aborting.");
//            Toast.makeText(this, "Camera error: Not ready.", Toast.LENGTH_SHORT).show();
//            setResult(RESULT_CANCELED);
//            finish();
//            return;
//        }
//        Log.d(TAG, "takePhoto: ImageCapture instance is available.");
//
//        File photoFile = null;
//        try {
//            File picturesDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//            if (picturesDir == null) {
//                Log.e(TAG, "takePhoto: getExternalFilesDir(Environment.DIRECTORY_PICTURES) returned null. Check permissions or storage state.");
//                Toast.makeText(this, "Error: Cannot access storage.", Toast.LENGTH_LONG).show();
//                setResult(RESULT_CANCELED);
//                finish();
//                return;
//            }
//            if (!picturesDir.exists() && !picturesDir.mkdirs()) {
//                Log.e(TAG, "takePhoto: Failed to create pictures directory: " + picturesDir.getAbsolutePath());
//            }
//
//            String fileName = new SimpleDateFormat(FILENAME_FORMAT, Locale.US)
//                    .format(System.currentTimeMillis()) + ".jpg";
//            photoFile = new File(picturesDir, fileName);
//            Log.d(TAG, "takePhoto: Output photoFile destination: " + photoFile.getAbsolutePath());
//        } catch (Exception e) {
//            Log.e(TAG, "takePhoto: Error creating photoFile object.", e);
//            Toast.makeText(this, "Error preparing file: " + e.getMessage(), Toast.LENGTH_LONG).show();
//            setResult(RESULT_CANCELED);
//            finish();
//            return;
//        }
//
//        ImageCapture.OutputFileOptions outputOptions =
//                new ImageCapture.OutputFileOptions.Builder(photoFile).build();
//        Log.d(TAG, "takePhoto: OutputFileOptions created.");
//
//        final String finalPhotoPath = photoFile.getAbsolutePath(); // For logging in callback
//
//        Log.d(TAG, "takePhoto: Preparing to call imageCapture.takePicture(). Using MainExecutor.");
//        imageCapture.takePicture(
//                outputOptions,
//                ContextCompat.getMainExecutor(this),
//                new ImageCapture.OnImageSavedCallback() {
//                    @Override
//                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
//                        Log.d(TAG, "onImageSaved: Success! Image saved.");
//                        Uri savedUri = outputFileResults.getSavedUri();
//                        if (savedUri == null) {
//                            Log.w(TAG, "onImageSaved: outputFileResults.getSavedUri() was NULL. Attempting Uri.fromFile for: " + finalPhotoPath);
//                            savedUri = Uri.fromFile(new File(finalPhotoPath)); // Reconstruct from the known path
//                        }
//                        Log.d(TAG, "onImageSaved: Saved URI: " + (savedUri != null ? savedUri.toString() : "NULL_URI_AFTER_FALLBACK"));
//
//                        Toast.makeText(ActivityCamera.this, "Photo capture succeeded: " + savedUri, Toast.LENGTH_SHORT).show();
//
//                        Intent resultIntent = new Intent();
//                        if (savedUri != null) {
//                            resultIntent.putExtra(EXTRA_IMAGE_URI, savedUri.toString());
//                            setResult(RESULT_OK, resultIntent);
//                            Log.d(TAG, "onImageSaved: setResult(RESULT_OK) with URI: " + savedUri.toString());
//                        } else {
//                            Log.e(TAG, "onImageSaved: savedUri is still NULL even after fallback. Setting result to CANCELED.");
//                            setResult(RESULT_CANCELED);
//                        }
//                        finish();
//                        Log.d(TAG, "onImageSaved: finish() called.");
//                    }
//
//                    @Override
//                    public void onError(@NonNull ImageCaptureException exception) {
//                        Log.e(TAG, "onError: Photo capture FAILED. ImageCaptureException.", exception);
//                        Toast.makeText(getBaseContext(), "Photo capture error: " + exception.getMessage(), Toast.LENGTH_LONG).show();
//                        setResult(RESULT_CANCELED);
//                        Log.d(TAG, "onError: setResult(RESULT_CANCELED)");
//                        finish();
//                        Log.d(TAG, "onError: finish() called.");
//                    }
//                }
//        );
//        Log.d(TAG, "takePhoto: imageCapture.takePicture() call submitted.");
//    }

    private void takePhoto() {
        Log.d(TAG, "takePhoto: Method called.");

        if (imageCapture == null) {
            Log.e(TAG, "takePhoto: ImageCapture use case is NULL. Cannot take photo.");
            Toast.makeText(this, "Camera error: Not ready for capture.", Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED);
            finish();
            return;
        }
        Log.d(TAG, "takePhoto: ImageCapture instance is available.");

        // Create time-stamped output file to hold the image
        File photoFile = new File(
                getExternalFilesDir(Environment.DIRECTORY_PICTURES), // Ensure this directory is appropriate
                new SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                        .format(System.currentTimeMillis()) + ".jpg");
        Log.d(TAG, "takePhoto: Output photoFile created at: " + photoFile.getAbsolutePath());

        ImageCapture.OutputFileOptions outputOptions =
                new ImageCapture.OutputFileOptions.Builder(photoFile).build();
        Log.d(TAG, "takePhoto: OutputFileOptions created.");

        // Set up image capture listener, which is triggered after photo has been taken
        imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(this), // Or your cameraExecutor
                new ImageCapture.OnImageSavedCallback() {
                    // In ActivityCamera.java

                    // Define your extra key consistently
                    // Inside your ImageCapture.OnImageSavedCallback
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        Uri savedUri = outputFileResults.getSavedUri();

                        if (savedUri == null && photoFile != null) {
                            savedUri = Uri.fromFile(photoFile);
                            Log.d(TAG, "onImageSaved: savedUri was null, attempting Uri.fromFile(photoFile): " + savedUri);
                        }
// In ActivityCamera.java
// ... inside onImageSaved callback ...

                        if (savedUri != null) {
                            Log.d(TAG, "onImageSaved: Image saved successfully. URI: " + savedUri.toString());

                            // --- FOR THE CALLING ACTIVITY (e.g., GetStarted) IF IT'S USING startActivityForResult ---
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra(EXTRA_IMAGE_URI, savedUri.toString()); // Use your defined key
                            setResult(Activity.RESULT_OK, resultIntent);
                            Log.d(TAG, "onImageSaved: setResult(Activity.RESULT_OK) for the calling activity.");
                            // --- END: FOR CALLING ACTIVITY ---

                            // --- QUICK CHANGE: Start MainActivity instead of ProcessImage ---
                            Intent mainActivityIntent = new Intent(ActivityCamera.this, MainActivity.class);
                            mainActivityIntent.putExtra("imageUriFromCamera", savedUri.toString()); // Key for MainActivity
                            startActivity(mainActivityIntent);
                            Log.d(TAG, "onImageSaved: Started MainActivity with image URI.");

                        } else {
                            Log.e(TAG, "onImageSaved: Error, savedUri is NULL.");
                            setResult(Activity.RESULT_CANCELED); // For calling activity if using startActivityForResult
                        }
                        finish(); // Finish ActivityCamera
//                        if (savedUri != null) {
//                            Log.d(TAG, "onImageSaved: Image saved successfully. URI: " + savedUri.toString());
//                            Intent resultIntent = new Intent();
//                            resultIntent.putExtra(EXTRA_IMAGE_URI, savedUri.toString());
//
//                            // --->>> THIS IS CRITICAL <<<---
//                            setResult(Activity.RESULT_OK, resultIntent);
//                            Log.d(TAG, "onImageSaved: setResult(Activity.RESULT_OK) CALLED with URI.");
//                            // --->>> END CRITICAL SECTION <<<---
//
//                            // Start ProcessImage (this part seems to work for you, but the result to MainActivity is the issue)
//                            Intent processIntent = new Intent(ActivityCamera.this, ProcessImage.class);
//                            processIntent.putExtra("savedUri", savedUri.toString());
//                            startActivity(processIntent);
//                            Log.d(TAG, "onImageSaved: Started ProcessImage activity.");
//
//                        }else {
//                            Log.e(TAG, "onImageSaved: Error, savedUri is NULL.");
//                            // --->>> THIS IS CRITICAL FOR ERROR CASE <<<---
//                            setResult(Activity.RESULT_CANCELED);
//                            Log.d(TAG, "onImageSaved: setResult(Activity.RESULT_CANCELED) CALLED due to null URI.");
//                            // --->>> END CRITICAL SECTION <<<---
//                        }

                        // --->>> `finish()` MUST be after `setResult()` <<<---
//                        finish();
                        Log.d(TAG, "onImageSaved: ActivityCamera.finish() CALLED.");
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Log.e(TAG, "Photo capture error: " + exception.getMessage(), exception);
                        // --->>> THIS IS CRITICAL FOR ERROR CASE <<<---
                        setResult(MainActivity.RESULT_CANCELED);
                        Log.d(TAG, "onError: setResult(Activity.RESULT_CANCELED) CALLED due to exception.");
                        // --->>> END CRITICAL SECTION <<<---

                        // --->>> `finish()` MUST be after `setResult()` <<<---
                        finish();
                        Log.d(TAG, "onError: ActivityCamera.finish() CALLED.");
                    }
//                    @Override
//                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
//                        Uri savedUri = outputFileResults.getSavedUri();
//                        if (savedUri == null) {
//                            // Attempt fallback if savedUri is null (as in your previous good log)
//                            savedUri = Uri.fromFile(photoFile);
//                        }
//
//                        Log.d(TAG, "onImageSaved: Photo capture SUCCEEDED. URI: " + (savedUri != null ? savedUri.toString() : "NULL_URI"));
//
//                        if (savedUri != null) {
//                            // 1. Prepare result for MainActivity
//                            Intent resultIntent = new Intent();
//                            resultIntent.putExtra(EXTRA_IMAGE_URI, savedUri.toString());
//                            resultIntent.putExtra(EXTRA_START_PROCESSING_IMMEDIATELY, true); // Signal MainActivity
//                            setResult(RESULT_OK, resultIntent);
//                            Log.d(TAG, "onImageSaved: setResult(RESULT_OK) for MainActivity with URI: " + savedUri);
//
//                            // 2. Route to ProcessImage screen
//                            Intent processIntent = new Intent(ActivityCamera.this, ProcessImage.class);
//                            processIntent.putExtra("savedUri", savedUri.toString()); // Pass URI to ProcessImage
//                            startActivity(processIntent);
//                            Log.d(TAG, "onImageSaved: Started ProcessImage activity.");
//
//                        } else {
//                            Log.e(TAG, "onImageSaved: savedUri is NULL. Cannot proceed.");
//                            Toast.makeText(ActivityCamera.this, "Error saving image.", Toast.LENGTH_SHORT).show();
//                            setResult(RESULT_CANCELED);
//                        }
//                        finish(); // Finish ActivityCamera
//                        Log.d(TAG, "onImageSaved: ActivityCamera.finish() called.");
//                    }
//
//                    @Override
//                    public void onError(@NonNull ImageCaptureException exception) {
//                        Log.e(TAG, "onError: Photo capture FAILED.", exception);
//                        Toast.makeText(getBaseContext(), "Photo capture error: " + exception.getMessage(), Toast.LENGTH_LONG).show();
//                        setResult(RESULT_CANCELED);
//                        finish();
//                    }
                }
        );
        Log.d(TAG, "takePhoto: imageCapture.takePicture() call submitted.");
    }

    private void routeToProcessImage(Uri imageUri) { // Or pass a String filePath
        Intent intent = new Intent(ActivityCamera.this, ProcessImage.class);
        intent.putExtra("imageUri", imageUri.toString()); // Pass the URI as a String
        // Or: intent.putExtra("imagePath", filePath);
        startActivity(intent);
        finish(); // Usually, after taking a picture, you don't want to go back to the camera preview.
        // In ActivityCamera.java, after image is captured (imageUri)
//        Intent intent = new Intent(ActivityCamera.this, MainActivity.class);
//        intent.putExtra("ACTION_PROCESS_IMAGE", true);
//        intent.putExtra("imageUriToProcess", imageUri.toString());
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP); // Bring existing MainActivity to front or create new
//        startActivity(intent);
//        finish(); // Finish ActivityCamera
    }

    // You would call routeToProcessImage(imageUri) in your CameraX image capture success callback.
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }
        // Attempt to unbind CameraX resources. This can be tricky if ProcessCameraProvider
        // itself failed to initialize, so wrap it.
        try {
            ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
            if (cameraProviderFuture.isDone()) { // Only try to get if it's done (might not be if init failed early)
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                if (cameraProvider != null) {
                    cameraProvider.unbindAll();
                    Log.d(TAG, "onDestroy: CameraX use cases unbound.");
                }
            }
        } catch (Exception e) { // ExecutionException, InterruptedException, or other
            Log.e(TAG, "onDestroy: Error unbinding CameraX.", e);
        }
        Log.d(TAG, "onDestroy() called.");
    }

    // You can keep onStart, onResume, onPause, onStop with logs for now if you like
}


//@Override
//protected void onStart() {
//    super.onStart();
//    Log.d(TAG, "onStart() called");
//}
//    @Override
//    protected void onResume() {
//        super.onResume();
//        Log.d(TAG, "onResume() called");
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        Log.d(TAG, "onPause() called");
//        // If we are here and not explicitly finishing with RESULT_OK,
//        // it will result in RESULT_CANCELED by default when finish() is called.
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        Log.d(TAG, "onStop() called");
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        Log.d(TAG, "onDestroy() called");
//    }
//}