package com.example.phcameraapp;


import android.Manifest; // Import Manifest
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager; // Import PackageManager
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.opengl.Matrix;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log; // Import Log for better debugging
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast; // Import Toast

import androidx.annotation.NonNull; // Import NonNull
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat; // Import ActivityCompat
import androidx.core.content.ContextCompat; // Import ContextCompat
import androidx.core.content.FileProvider;
import com.bumptech.glide.Glide;
//import com.google.ar.imp.view.View;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import android.graphics.BitmapFactory;
// Add to imports:
import android.graphics.Color;
import android.widget.TextView; // To display RGB value
import android.view.View;
import android.graphics.Canvas; // For drawing
import android.graphics.Paint;  // For drawing
import java.util.Locale;
// In your MainActivity.java or a separate utility class

import android.graphics.Rect;

public class MainActivity extends AppCompatActivity {

    public static double lastCalculatedPh = -1.0;
    // Declare UI elements
    private Button openCamera;
    private ImageView clickedImage;

    // Variables to hold the file and its Uri
    private File photoFile;
    private Uri photoUri;

    // Unique request code for identifying the camera intent result
    private static final int CAMERA_REQUEST_CODE = 1001;
    // Request code for camera permission
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1002; // Different from camera intent

    private static final String TAG = "MainActivity"; // For logging
    private TextView rgbTextView; // Declare this if you want to show RGB in a TextView
//    ImageView centerTargetImage;
    private TextView rgbValueTextView;
//    private static final int SQUARE_HALF_SIZE = 25; // For a 50x50 square
    private static final int PLUS_SIGN_ARM_LENGTH = 300; // Distance from center to the end of each arm
    // ----------------------------
    private Paint markerPaint;
    private static final int MARKER_RADIUS = 8; // Radius of the circle markers in pixels

    // --- Updated Calibration Square Coordinates ---

    // Yellow Patches
    // Yellow Top: TL(280, 124), BR(355, 198)
    private static final int CHART_YELLOW_TOP_X = 270;
    private static final int CHART_YELLOW_TOP_Y = 124;
    private static final int CHART_YELLOW_TOP_WIDTH = 355 - 280; // 75
    private static final int CHART_YELLOW_TOP_HEIGHT = 198 - 124; // 74

    // Yellow Bottom: TL(269, 537), BR(335, 614)
    private static final int CHART_YELLOW_BOTTOM_X = 269;
    private static final int CHART_YELLOW_BOTTOM_Y = 537;
    private static final int CHART_YELLOW_BOTTOM_WIDTH = 335 - 269; // 66
    private static final int CHART_YELLOW_BOTTOM_HEIGHT = 614 - 537; // 77

    // Green Patches
    // Green Top: TL(337, 124), BR(436, 200)
    private static final int CHART_GREEN_TOP_X = 370;
    private static final int CHART_GREEN_TOP_Y = 124;
    private static final int CHART_GREEN_TOP_WIDTH = 400 - 337; // 99
    private static final int CHART_GREEN_TOP_HEIGHT = 200 - 124; // 76

    // Green Bottom: TL(355, 535), BR(430, 612)
    private static final int CHART_GREEN_BOTTOM_X = 355;
    private static final int CHART_GREEN_BOTTOM_Y = 535;
    private static final int CHART_GREEN_BOTTOM_WIDTH = 430 - 355; // 75
    private static final int CHART_GREEN_BOTTOM_HEIGHT = 612 - 535; // 77

    // Blue Patches
    // Blue Top: TL(467, 124), BR(530, 200)
    private static final int CHART_BLUE_TOP_X = 467;
    private static final int CHART_BLUE_TOP_Y = 124;
    private static final int CHART_BLUE_TOP_WIDTH = 530 - 467; // 63
    private static final int CHART_BLUE_TOP_HEIGHT = 200 - 124; // 76

    // Blue Bottom: TL(455, 541), BR(527, 615)
    private static final int CHART_BLUE_BOTTOM_X = 455;
    private static final int CHART_BLUE_BOTTOM_Y = 541;
    private static final int CHART_BLUE_BOTTOM_WIDTH = 527 - 455; // 72
    private static final int CHART_BLUE_BOTTOM_HEIGHT = 615 - 541; // 74

    // Red Patches
    // Red Top: TL(561, 131), BR(627, 201)
    private static final int CHART_RED_TOP_X = 561;
    private static final int CHART_RED_TOP_Y = 131;
    private static final int CHART_RED_TOP_WIDTH = 627 - 561; // 66
    private static final int CHART_RED_TOP_HEIGHT = 201 - 131; // 70

    // Red Bottom: TL(554, 540), BR(620, 609)
    private static final int CHART_RED_BOTTOM_X = 554;
    private static final int CHART_RED_BOTTOM_Y = 540;
    private static final int CHART_RED_BOTTOM_WIDTH = 620 - 554; // 66
    private static final int CHART_RED_BOTTOM_HEIGHT = 609 - 540; // 69

    // White Patches
    // White Top: TL(653, 128), BR(725, 203) - Assuming BR Y typo meant 203 not 202 for consistency
    private static final int CHART_WHITE_TOP_X = 653;
    private static final int CHART_WHITE_TOP_Y = 128;
    private static final int CHART_WHITE_TOP_WIDTH = 725 - 653; // 72
    private static final int CHART_WHITE_TOP_HEIGHT = 203 - 128; // 75 (if BR Y is 203) or 202-128 = 74

    // White Bottom: TL(649, 539), BR(708, 610)
    private static final int CHART_WHITE_BOTTOM_X = 649;
    private static final int CHART_WHITE_BOTTOM_Y = 539;
    private static final int CHART_WHITE_BOTTOM_WIDTH = 708 - 649; // 59
    private static final int CHART_WHITE_BOTTOM_HEIGHT = 610 - 539; // 71

    // Gray Patch
    // Gray Square: TL(600, 330), BR(670, 420)
    private static final int CHART_GRAY_X = 600;
    private static final int CHART_GRAY_Y = 330;
    private static final int CHART_GRAY_WIDTH = 670 - 600; // 70
    private static final int CHART_GRAY_HEIGHT = 420 - 330; // 90

// Add these static methods inside MainActivity.java

    private static final String TAG_LOOKUP_GENERATOR = "PhLookupGenerator"; // For logging

    // --- THE UNIVERSAL RECTANGLE COORDINATES ---
    private static final int SAMPLE_REGION_X = 480;
    private static final int SAMPLE_REGION_Y = 365;
    private static final int SAMPLE_REGION_WIDTH = 23;
    private static final int SAMPLE_REGION_HEIGHT = 22;

    // These are the coordinates you use for live analysis and for the yellow ROI display
    private static final int ANALYSIS_ROI_X = 480;      // Example from your previous constants
    private static final int ANALYSIS_ROI_Y = 365;      // Example
    private static final int ANALYSIS_ROI_WIDTH = 23;   // Example
    private static final int ANALYSIS_ROI_HEIGHT = 22;  // Example
    // -------------------------------------------
    // Add a request code for starting ActivityCamera
    private static final int REQUEST_CODE_CUSTOM_CAMERA = 102; // Choose any unique number
    private List<CalibrationSquare> calibrationSquares;
    private List<ChartPatch> chartPatches;

    // --- Declare these as member variables ---
    private Map<String, int[]> measuredCalibrationColors;
    private Map<String, int[]> referenceCalibrationColors;
    private StringBuilder analysisResultsText;
    private Button openMyCameraButton; // Your button to launch ActivityCamera


    // DECLARE GAINS HERE AS CLASS MEMBERS
    private float gainR = 1.0f;
    private float gainG = 1.0f;
    private float gainB = 1.0f;
    private ArrayList<Object> phLookupTable;
    private ImageView colorSquare;

    /**
     * Generates a list of PhLookupEntry objects by processing images from assets.
     * THIS IS FOR ONE-TIME USE TO GENERATE YOUR LOOKUP TABLE DATA.
     *
     * @param context Context to access assets.
//     * @param imagePhMap A map where keys are known pH values (e.g., 4.0, 4.5)
     *                   and values are the filenames of the corresponding images
     *                   in the assets folder (e.g., "ph_4.0.png").
     * @return A list of PhLookupEntry objects.
     */
    // Modify this static method in MainActivity.java

    // Modify this static method in MainActivity.java

    public static List<PhLookupEntry> generateLookupTableFromReferenceImageAssets( // Renamed for clarity
                                                                                   Context context,
                                                                                   Map<Double, String> imagePhFilenames) { // pH mapped to image filename

        List<PhLookupEntry> lookupTable = new ArrayList<>();
        Log.d(TAG_LOOKUP_GENERATOR, "Method Start (Using ANALYSIS_ROI Coords). Input map size: " + imagePhFilenames.size());

        if (imagePhFilenames == null || imagePhFilenames.isEmpty()) {
            Log.e(TAG_LOOKUP_GENERATOR, "Input imagePhFilenames map IS NULL or EMPTY! Returning empty list.");
            return lookupTable;
        }

        for (Map.Entry<Double, String> mapEntry : imagePhFilenames.entrySet()) {
            double pH_value = mapEntry.getKey();
            String imageName = mapEntry.getValue();

            Log.d(TAG_LOOKUP_GENERATOR, "----------------------------------------------------");
            Log.d(TAG_LOOKUP_GENERATOR, "Processing pH: " + pH_value + " with Image: " + imageName);
            // Log the coordinates being used (your ANALYSIS_ROI coordinates)
            Log.d(TAG_LOOKUP_GENERATOR, "Using ANALYSIS_ROI Coords for sampling: X=" + ANALYSIS_ROI_X +
                    ", Y=" + ANALYSIS_ROI_Y + ", W=" + ANALYSIS_ROI_WIDTH + ", H=" + ANALYSIS_ROI_HEIGHT);

            InputStream inputStream = null;
            Bitmap bitmap = null;

            try {
                inputStream = context.getAssets().open(imageName);
                bitmap = BitmapFactory.decodeStream(inputStream);

                if (bitmap == null) {
                    Log.e(TAG_LOOKUP_GENERATOR, "FAILED to decode bitmap for: " + imageName + ". Skipping.");
                    if (inputStream != null) try { inputStream.close(); } catch (IOException ignored) {}
                    continue;
                }

                // --- Use ANALYSIS_ROI Coordinates for Sampling ---
                int currentSampleX = ANALYSIS_ROI_X;
                int currentSampleY = ANALYSIS_ROI_Y;
                int currentSampleWidth = ANALYSIS_ROI_WIDTH;
                int currentSampleHeight = ANALYSIS_ROI_HEIGHT;

                // --- Validations (ensure ROI is within bitmap bounds) ---
                if (currentSampleWidth <= 0 || currentSampleHeight <= 0 ||
                        currentSampleX < 0 || currentSampleY < 0 ||
                        currentSampleX + currentSampleWidth > bitmap.getWidth() ||
                        currentSampleY + currentSampleHeight > bitmap.getHeight()) {
                    Log.e(TAG_LOOKUP_GENERATOR, "ANALYSIS_ROI is OUT OF BOUNDS or invalid for image " + imageName + ". " +
                            "Bitmap Dims: " + bitmap.getWidth() + "x" + bitmap.getHeight() + ". " +
                            "ROI: X=" + currentSampleX + ",Y=" + currentSampleY + ",W=" + currentSampleWidth + ",H=" + currentSampleHeight + ". Skipping.");
                    if (bitmap!= null) bitmap.recycle(); // recycle bitmap before continuing
                    if (inputStream != null) try { inputStream.close(); } catch (IOException ignored) {}
                    continue;
                }

                // --- Extract Average RGB from the defined ANALYSIS_ROI ---
                long sumR = 0, sumG = 0, sumB = 0;
                int pixelCount = 0;

                for (int y = currentSampleY; y < currentSampleY + currentSampleHeight; y++) {
                    for (int x = currentSampleX; x < currentSampleX + currentSampleWidth; x++) {
                        int pixel = bitmap.getPixel(x, y);
                        sumR += Color.red(pixel);
                        sumG += Color.green(pixel);
                        sumB += Color.blue(pixel);
                        pixelCount++;
                    }
                }

                if (pixelCount == 0) {
                    Log.w(TAG_LOOKUP_GENERATOR, "No pixels in ANALYSIS_ROI for " + imageName + ". This shouldn't happen if ROI is valid. Skipping.");
                    if (bitmap!= null) bitmap.recycle();
                    if (inputStream != null) try { inputStream.close(); } catch (IOException ignored) {}
                    continue;
                }

                int avgR = (int) (sumR / pixelCount);
                int avgG = (int) (sumG / pixelCount);
                int avgB = (int) (sumB / pixelCount);

                Log.d(TAG_LOOKUP_GENERATOR, String.format(Locale.US,
                        "Image: %s (pH: %.1f) - ANALYSIS_ROI -> Avg RGB: (%d,%d,%d)",
                        imageName, pH_value, avgR, avgG, avgB));

                lookupTable.add(new PhLookupEntry(pH_value, avgR, avgG, avgB));

            } catch (IOException e) {
                Log.e(TAG_LOOKUP_GENERATOR, "IOException for " + imageName + ": " + e.getMessage(), e);
            } catch (IllegalArgumentException e) { // Could be from getPixel if x,y are out of bounds
                Log.e(TAG_LOOKUP_GENERATOR, "IllegalArgumentException during pixel processing for " + imageName + " (ROI: " + ANALYSIS_ROI_X + "," + ANALYSIS_ROI_Y + "," + ANALYSIS_ROI_WIDTH + "," + ANALYSIS_ROI_HEIGHT + " might be an issue for bitmap size " + (bitmap != null ? bitmap.getWidth()+"x"+bitmap.getHeight() : "unknown") + "): " + e.getMessage(), e);
            } catch (Exception e) { // Catch any other unexpected errors
                Log.e(TAG_LOOKUP_GENERATOR, "Generic Exception for " + imageName + ": " + e.getMessage(), e);
            } finally {
                if (bitmap != null) {
                    bitmap.recycle();
                }
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException ignored) {}
                }
            }
        }
        Log.i(TAG_LOOKUP_GENERATOR, "Lookup table generation (Using ANALYSIS_ROI Coords) COMPLETE. Entries: " + lookupTable.size());
        return lookupTable;
    }
    public static List<PhLookupEntry> generateLookupTableFromAssetsWithIndividualCoords(
            Context context,
            Map<Double, PhImageInfo> imagePhDataWithCoords) {
        List<PhLookupEntry> lookupTable = new ArrayList<>();
        Log.d(TAG_LOOKUP_GENERATOR, "Method Start. Input map size: " + imagePhDataWithCoords.size()); // Renamed for clarity

        if (imagePhDataWithCoords == null) {
            Log.e(TAG_LOOKUP_GENERATOR, "CRITICAL: imagePhDataWithCoords map is NULL! Returning empty list.");
            return lookupTable;
        }
        if (imagePhDataWithCoords.isEmpty()) {
            Log.w(TAG_LOOKUP_GENERATOR, "Input imagePhDataWithCoords map IS EMPTY! Returning empty list.");
            return lookupTable;
        }

        Set<Map.Entry<Double, PhImageInfo>> entrySet = null;
        try {
            entrySet = imagePhDataWithCoords.entrySet();
            if (entrySet == null) {
                Log.e(TAG_LOOKUP_GENERATOR, "CRITICAL: imagePhDataWithCoords.entrySet() returned NULL!");
                return lookupTable;
            }
            Log.d(TAG_LOOKUP_GENERATOR, "Successfully got entrySet. Size: " + entrySet.size());
        } catch (Exception e) {
            Log.e(TAG_LOOKUP_GENERATOR, "CRITICAL: Exception while getting entrySet: " + e.getMessage(), e);
            return lookupTable; // Critical failure
        }

        Log.d(TAG_LOOKUP_GENERATOR, "About to start FOR loop over entrySet.");
        int loopIterationCount = 0; // Counter

        for (Map.Entry<Double, PhImageInfo> mapEntry : entrySet) { // Iterate over the retrieved entrySet
            loopIterationCount++;
            Log.d(TAG_LOOKUP_GENERATOR, "LOOP START - Iteration: " + loopIterationCount); // **** VERY FIRST LOG INSIDE LOOP ****

            if (mapEntry == null) {
                Log.w(TAG_LOOKUP_GENERATOR, "Loop Iteration " + loopIterationCount + ": mapEntry is NULL. Skipping.");
                continue;
            }

            double pH_value = -1.0; // Default
            PhImageInfo imageInfo = null;

            try {
                pH_value = mapEntry.getKey();
                imageInfo = mapEntry.getValue();

                if (imageInfo == null) {
                    Log.w(TAG_LOOKUP_GENERATOR, "Loop Iteration " + loopIterationCount + ": imageInfo is NULL for pH " + pH_value + ". Skipping.");
                    continue;
                }
            } catch (Exception e) {
                Log.e(TAG_LOOKUP_GENERATOR, "Loop Iteration " + loopIterationCount + ": Exception getting key/value from mapEntry: " + e.getMessage(), e);
                continue;
            }

            Log.d(TAG_LOOKUP_GENERATOR, "----------------------------------------------------");
            Log.d(TAG_LOOKUP_GENERATOR, "Attempting to process pH: " + pH_value + " with ImageInfo: " + imageInfo.imageName);
            Log.d(TAG_LOOKUP_GENERATOR, "ImageInfo Coords: X=" + imageInfo.sampleX + ", Y=" + imageInfo.sampleY + ", W=" + imageInfo.sampleWidth + ", H=" + imageInfo.sampleHeight);

            InputStream inputStream = null;
            Bitmap bitmap = null;

            try {
                Log.d(TAG_LOOKUP_GENERATOR, "Attempting to open asset: " + imageInfo.imageName);
                inputStream = context.getAssets().open(imageInfo.imageName);
                Log.d(TAG_LOOKUP_GENERATOR, "Asset opened successfully: " + imageInfo.imageName);

                Log.d(TAG_LOOKUP_GENERATOR, "Attempting to decode stream for: " + imageInfo.imageName);
                bitmap = BitmapFactory.decodeStream(inputStream);

                if (bitmap == null) {
                    Log.e(TAG_LOOKUP_GENERATOR, "FAILED to decode bitmap for: " + imageInfo.imageName + ". BitmapFactory.decodeStream returned null.");
                    // No 'continue' here yet, will be caught by finally or next check
                } else {
                    Log.d(TAG_LOOKUP_GENERATOR, "Bitmap DECODED for " + imageInfo.imageName + ": " + bitmap.getWidth() + "x" + bitmap.getHeight());
                }

                // --- Critical Validations ---
                if (bitmap == null) { // Check again after decode attempt
                    Log.e(TAG_LOOKUP_GENERATOR, "Bitmap is NULL after decode attempt for: " + imageInfo.imageName + ". Skipping this entry.");
                    if (inputStream != null) try { inputStream.close(); } catch (IOException ignored) {}
                    continue; // Skip to next image in map
                }


                int currentSampleX = imageInfo.sampleX;
                int currentSampleY = imageInfo.sampleY;
                int currentSampleWidth = imageInfo.sampleWidth;
                int currentSampleHeight = imageInfo.sampleHeight;
                Log.d(TAG_LOOKUP_GENERATOR, "Using Coords for sampling: X=" + currentSampleX + ", Y=" + currentSampleY + ", W=" + currentSampleWidth + ", H=" + currentSampleHeight);


                if (currentSampleWidth <= 0 || currentSampleHeight <= 0) {
                    Log.e(TAG_LOOKUP_GENERATOR, "INVALID sample region dimensions for " + imageInfo.imageName +
                            ": W=" + currentSampleWidth + ", H=" + currentSampleHeight + ". Skipping.");
                    bitmap.recycle();
                    if (inputStream != null) try { inputStream.close(); } catch (IOException ignored) {}
                    continue;
                }
                Log.d(TAG_LOOKUP_GENERATOR, "Sample dimensions are VALID (positive W, H).");


                if (currentSampleX < 0 || currentSampleY < 0 ||
                        currentSampleX + currentSampleWidth > bitmap.getWidth() ||
                        currentSampleY + currentSampleHeight > bitmap.getHeight()) {
                    Log.e(
                            TAG_LOOKUP_GENERATOR,
                            "Sample region is OUT OF BOUNDS for image " + imageInfo.imageName + ". " +
                                    "Bitmap Dims: " + bitmap.getWidth() + "x" + bitmap.getHeight() + ". " +
                                    "Sample Area End X: " + (currentSampleX + currentSampleWidth) + ", End Y: " + (currentSampleY + currentSampleHeight) + ". Skipping."
                    );
                    bitmap.recycle();
                    if (inputStream != null) try { inputStream.close(); } catch (IOException ignored) {}
                    continue;
                }
                Log.d(TAG_LOOKUP_GENERATOR, "Sample region is WITHIN bounds.");


                Log.d(TAG_LOOKUP_GENERATOR, "Attempting to calculate average color...");
                double[] avgRgb = calculateAverageColorForLookupRegion(
                        bitmap,
                        currentSampleX,
                        currentSampleY,
                        currentSampleWidth,
                        currentSampleHeight
                );

                if (avgRgb != null) {
                    Log.d(TAG_LOOKUP_GENERATOR, "Average RGB calculated: R=" + avgRgb[0] + ", G=" + avgRgb[1] + ", B=" + avgRgb[2]);
                    PhLookupEntry entry = new PhLookupEntry(
                            pH_value,
                            (int) Math.round(avgRgb[0]),
                            (int) Math.round(avgRgb[1]),
                            (int) Math.round(avgRgb[2])
                    );
                    lookupTable.add(entry);
                    Log.i(TAG_LOOKUP_GENERATOR, "SUCCESS: Added to lookup: " + entry.toString());
                } else {
                    Log.w(TAG_LOOKUP_GENERATOR, "FAILURE: Could not get average RGB for " + imageInfo.imageName + " (pH " + pH_value + "). calculateAverageColorForLookupRegion returned null.");
                }
                bitmap.recycle(); // Recycle here after use
            } catch (IOException e) {
                Log.e(TAG_LOOKUP_GENERATOR, "IOException for image " + imageInfo.imageName + ": " + e.getMessage(), e);
            } catch (Exception e) { // Catch any other unexpected errors for this entry
                Log.e(TAG_LOOKUP_GENERATOR, "Generic Exception processing image " + imageInfo.imageName + " for pH " + pH_value + ": " + e.getMessage(), e);
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                        Log.d(TAG_LOOKUP_GENERATOR, "InputStream closed for " + imageInfo.imageName);
                    } catch (IOException e) {
                        Log.e(TAG_LOOKUP_GENERATOR, "Error closing input stream for " + imageInfo.imageName, e);
                    }
                }
                if (bitmap != null && !bitmap.isRecycled()) { // Ensure bitmap is recycled if something went wrong before its explicit recycle call
                    Log.w(TAG_LOOKUP_GENERATOR, "Recycling bitmap in finally block for " + imageInfo.imageName);
                    bitmap.recycle();
                }
                Log.d(TAG_LOOKUP_GENERATOR, "Finished processing attempt for pH: " + pH_value);
                Log.d(TAG_LOOKUP_GENERATOR, "----------------------------------------------------"); // Separator
            }
        }
        Log.i(TAG_LOOKUP_GENERATOR, "Lookup table generation attempt complete. Final Size: " + lookupTable.size());
        return lookupTable;
    }

// Ensure TAG_LOOKUP_GENERATOR is defined in your class:
// private static final String TAG_LOOKUP_GENERATOR = "PhLookupGenerator";

// The `calculateAverageColorForLookupRegion` method itself doesn't need to change.
// The `PhLookupEntry` class also remains the same.

    /**
     * Calculates the average RGB color for a specified rectangular region within a bitmap.
     * Specifically for the lookup table generation.
     */
    private static double[] calculateAverageColorForLookupRegion(
            Bitmap bitmap,
            int x,
            int y,
            int width,
            int height
    ) {
        if (bitmap == null) {
            Log.e(TAG_LOOKUP_GENERATOR, "Input bitmap is null for lookup region calculation.");
            return null;
        }
        if (width <= 0 || height <= 0) {
            Log.e(TAG_LOOKUP_GENERATOR, "Region width or height is non-positive for lookup.");
            return null;
        }
        // This check is already done in the calling function, but good for robustness
        if (x < 0 || y < 0 || x + width > bitmap.getWidth() || y + height > bitmap.getHeight()) {
            Log.e(TAG_LOOKUP_GENERATOR, "Region is out of bitmap bounds for lookup.");
            return null;
        }

        long totalR = 0; // Use long to prevent overflow before division
        long totalG = 0;
        long totalB = 0;
        int numPixels = 0;

        // Iterate over the defined rectangle
        for (int currentY = y; currentY < y + height; currentY++) {
            for (int currentX = x; currentX < x + width; currentX++) {
                try {
                    int pixelColor = bitmap.getPixel(currentX, currentY);
                    totalR += Color.red(pixelColor);
                    totalG += Color.green(pixelColor);
                    totalB += Color.blue(pixelColor);
                    numPixels++;
                } catch (Exception e) {
                    Log.e(TAG_LOOKUP_GENERATOR, "Error getting pixel at (" + currentX + "," + currentY + "): " + e.getMessage());
                    // Depending on strictness, you might choose to skip this pixel or abort
                }
            }
        }

        if (numPixels == 0) {
            Log.w(TAG_LOOKUP_GENERATOR, "No pixels were sampled in the specified region for lookup.");
            return null;
        }

        return new double[]{
                (double) totalR / numPixels,
                (double) totalG / numPixels,
                (double) totalB / numPixels
        };
    }
    // Inner class inside MainActivity.java (or as a separate file)
    private static class PhImageInfo {
        final String imageName;
        final int sampleX;
        final int sampleY;
        final int sampleWidth;
        final int sampleHeight;

        PhImageInfo(String imageName, int sampleX, int sampleY, int sampleWidth, int sampleHeight) {
            this.imageName = imageName;
            this.sampleX = sampleX;
            this.sampleY = sampleY;
            this.sampleWidth = sampleWidth;
            this.sampleHeight = sampleHeight;
        }
    }


    private static class PointAnalysisData { // Renamed for clarity, or adapt your existing PointData
        String name;
        int r, g, b;
        String phValue; // To store the calculated pH

        PointAnalysisData(String name, int r, int g, int b, String phValue) {
            this.name = name;
            this.r = r;
            this.g = g;
            this.b = b;
            this.phValue = phValue;
        }

        // Overload constructor if you want to set pH later
        PointAnalysisData(String name, int r, int g, int b) {
            this(name, r, g, b, "Unknown");
        }

        @NonNull
        @Override
        public String toString() {
            return name + " RGB: (" + r + ", " + g + ", " + b + "), pH: " + phValue;
        }
    }
    // Add this inner class inside MainActivity.java
    private static class PhLookupEntry {
        final double pH;
        final int r, g, b;

        PhLookupEntry(double pH, int r, int g, int b) {
            this.pH = pH;
            this.r = r;
            this.g = g;
            this.b = b;
        }

        @NonNull
        @Override
        public String toString() {
            // Using Locale.US to ensure dot as decimal separator if you copy-paste
            return String.format(java.util.Locale.US, "pH: %.1f, R: %d, G: %d, B: %d", pH, r, g, b);
        }
    }
//    / ---
// --- Static Helper Method for Calculating Average Color of a Region ---
private static double[] calculateAverageColorForRegion(Bitmap bmp, int xPos, int yPos, int w, int h) {
    if (bmp == null) {
        Log.e(TAG, "calculateAverageColorForRegion: Input bitmap is null!");
        return null;
    }
    long sumR = 0, sumG = 0, sumB = 0;
    int count = 0;
    int imageWidth = bmp.getWidth();
    int imageHeight = bmp.getHeight();

    // Ensure the region is at least partially within the bitmap
    int startX = Math.max(0, xPos);
    int startY = Math.max(0, yPos);
    int endX = Math.min(imageWidth, xPos + w);
    int endY = Math.min(imageHeight, yPos + h);

    if (startX >= endX || startY >= endY) {
        Log.w(TAG, "calculateAverageColorForRegion: Region is outside bitmap bounds or invalid. X:" + xPos + " Y:" + yPos + " W:" + w + " H:" + h);
        return null; // Region is completely outside or invalid
    }

    for (int y = startY; y < endY; y++) {
        for (int x = startX; x < endX; x++) {
            int pixelColor = bmp.getPixel(x, y);
            sumR += Color.red(pixelColor);
            sumG += Color.green(pixelColor);
            sumB += Color.blue(pixelColor);
            count++;
        }
    }

    if (count == 0) {
        Log.w(TAG, "calculateAverageColorForRegion: No pixels found in the specified region. X:" + xPos + " Y:" + yPos + " W:" + w + " H:" + h);
        return null; // No pixels were in the valid part of the region
    }
    return new double[]{(double) sumR / count, (double) sumG / count, (double) sumB / count, (double) count};
}

    // ---- THIS IS YOUR NEW PERMANENT LOOKUP TABLE ----
    private static final List<PhLookupEntry> PH_LOOKUP_TABLE = new ArrayList<>();


static {
    PH_LOOKUP_TABLE.add(new PhLookupEntry(4.5, 115, 86, 129));
    PH_LOOKUP_TABLE.add(new PhLookupEntry(5.0, 111, 85, 127));
    PH_LOOKUP_TABLE.add(new PhLookupEntry(5.5, 107, 87, 129));
    PH_LOOKUP_TABLE.add(new PhLookupEntry(6.0, 102, 72, 112));
    PH_LOOKUP_TABLE.add(new PhLookupEntry(6.5, 99, 100, 135));
    PH_LOOKUP_TABLE.add(new PhLookupEntry(7.0, 96, 106, 137));
    PH_LOOKUP_TABLE.add(new PhLookupEntry(7.5, 95, 113, 143));
    PH_LOOKUP_TABLE.add(new PhLookupEntry(8.0, 76, 93, 125));
    PH_LOOKUP_TABLE.add(new PhLookupEntry(8.5, 68, 96, 127));
    PH_LOOKUP_TABLE.add(new PhLookupEntry(9.0, 58, 86, 120));
}


// Log.d("YourAppTag", "Static PH_LOOKUP_TABLE initialized with " + PH_LOOKUP_TABLE.size() + " entries.");

// ---- REMOVE OR REPLACE YOUR OLD phColorMap and predictPH ----
// private static final Map<String, int[]> phColorMap = new HashMap<>();
// static { ... old map ... }

    private String predictPH(int[] currentRgb) { // currentRgb is [R, G, B] from live image
        if (PH_LOOKUP_TABLE.isEmpty()) {
            Log.w(TAG, "PH_LOOKUP_TABLE is empty!");
            return "Table Empty";
        }

        if (currentRgb == null || currentRgb.length != 3) {
            Log.w(TAG, "Invalid RGB array passed to predictPH");
            return "Invalid RGB";
        }
        Log.w(TAG, "RGB passed to predictPH: " + Arrays.toString(currentRgb));
        int r = currentRgb[0];
        int g = currentRgb[1];
        int b = currentRgb[2];

        PhLookupEntry closestMatch = null;

        double minDistanceSquared = Double.MAX_VALUE;


        PhLookupEntry overallClosestEntry = null;
        int minOverallRedDifference = Integer.MAX_VALUE;
        int minBlueDifferenceForBestRed = Integer.MAX_VALUE; // For tie-breaking

        Log.d(TAG, "findClosestPh_RedThenBlue: Comparing measured R=" + r + ", B=" + b);

        for (PhLookupEntry entry : PH_LOOKUP_TABLE) {
            int currentRedDifference = Math.abs(entry.r - r);

            // Log.d(TAG, "  Table pH " + entry.getPh() + ", R=" + entry.getR() + " (Diff: " + currentRedDifference +
            //           "), B=" + entry.getB());

            if (currentRedDifference < minOverallRedDifference) {
                // New best Red match found
                minOverallRedDifference = currentRedDifference;
                // Now check its Blue difference
                minBlueDifferenceForBestRed = Math.abs(entry.b - b);
                overallClosestEntry = entry;
            } else if (currentRedDifference == minOverallRedDifference) {
                // Tie in Red difference, use Blue to break the tie
                int currentBlueDifference = Math.abs(entry.b - b);
                if (currentBlueDifference < minBlueDifferenceForBestRed) {
                    minBlueDifferenceForBestRed = currentBlueDifference;
                    overallClosestEntry = entry; // This one is better on Blue for the same Red diff
                }
            }
        }
//        MainActivity.lastCalculatedPh = overallClosestEntry.pH;
//        Log.d(TAG, "analyzeImage: FINISHED. Set lastCalculatedPh to = " + MainActivity.lastCalculatedPh);
        if (overallClosestEntry != null) {
            Log.d(TAG, "findClosestPh_RedThenBlue - Closest pH: " + overallClosestEntry.pH +
                    " (Table R:" + overallClosestEntry.r + ", B:" + overallClosestEntry.b +
                    "). Min Red Diff: " + minOverallRedDifference +
                    (minOverallRedDifference == 0 ? " (Exact Red Match)" : "") +
                    ", Blue Diff for this match: " + Math.abs(overallClosestEntry.b - b));
        } else {
            Log.w(TAG, "findClosestPh_RedThenBlue - No match found.");
        }
        return String.format(java.util.Locale.US, "%.1f", overallClosestEntry.pH);
    }

// Your old euclideanDistance method might no longer be needed if predictPH handles it all.
// private double euclideanDistance(int[] rgb1, int[] rgb2) { ... }


// ... rest of your MainActivity ...

    private double euclideanDistance(int[] rgb1, int[] rgb2) {
        if (rgb1 == null || rgb1.length != 3 || rgb2 == null || rgb2.length != 3) {
            return Double.MAX_VALUE; // Or throw an IllegalArgumentException
        }
        long rDiff = rgb1[0] - rgb2[0];
        long gDiff = rgb1[1] - rgb2[1];
        long bDiff = rgb1[2] - rgb2[2];
        return Math.sqrt(rDiff * rDiff + gDiff * gDiff + bDiff * bDiff);
    }
    // --- END OF PH CALCULATION LOGIC ---

    private void initializeCalibrationSquares() {
        calibrationSquares = new ArrayList<>();

        // Yellow Top
        calibrationSquares.add(new CalibrationSquare("YellowTop", 280, 124, 355, 198));
        // Yellow Bottom
        calibrationSquares.add(new CalibrationSquare("YellowBottom", 269, 537, 335, 614)); // Corrected 6142 to 614

        // Green Top
        calibrationSquares.add(new CalibrationSquare("GreenTop", 337, 124, 436, 200));
        // Green Bottom
        calibrationSquares.add(new CalibrationSquare("GreenBottom", 355, 535, 430, 612));

        // Blue Top
        calibrationSquares.add(new CalibrationSquare("BlueTop", 467, 124, 530, 200));
        // Blue Bottom
        calibrationSquares.add(new CalibrationSquare("BlueBottom", 455, 541, 527, 615));

        // Red Top
        calibrationSquares.add(new CalibrationSquare("RedTop", 561, 131, 627, 201));
        // Red Bottom
        calibrationSquares.add(new CalibrationSquare("RedBottom", 554, 540, 620, 609));

        // White Top
        calibrationSquares.add(new CalibrationSquare("WhiteTop", 653, 128, 725, 203)); // Assuming 203 was intended for BR Y
        // White Bottom
        calibrationSquares.add(new CalibrationSquare("WhiteBottom", 649, 539, 708, 610));

        // Gray Square (Corrected assumption for BL and BR X values)
        calibrationSquares.add(new CalibrationSquare("Gray", 600, 330, 670, 420));

        Log.d(TAG, "Initialized " + calibrationSquares.size() + " calibration squares.");
        referenceCalibrationColors = new HashMap<>();

        // --- Using your NIX Sensor sRGB Values (D50 Illuminant) ---

        // White: SRGB 229, 226, 219
        // Assuming this corresponds to "WhiteTop" or your primary white patch
        referenceCalibrationColors.put("WhiteTop", new int[]{229, 226, 219});
        // If you have a "WhiteBottom" and it's similar, you might use the same.
        // If it's different and you don't have a NIX reading for it, you might have to
        // estimate or temporarily exclude it from a more advanced calibration.
        // For simple white balance, one good white/gray is key.
        // Example: referenceCalibrationColors.put("WhiteBottom", new int[]{220, 220, 220}); // Placeholder if different

        // Neutral gray: SRGB 119, 120, 119
        referenceCalibrationColors.put("Gray", new int[]{119, 120, 119});

        // Red: SRGB 206, 70, 71
        // Assuming "RedTop"
        referenceCalibrationColors.put("RedTop", new int[]{206, 70, 71});
        // If "RedBottom" exists and is different, handle similarly to WhiteBottom.
        // Example: referenceCalibrationColors.put("RedBottom", new int[]{190, 50, 60}); // Placeholder

        // Yellow: SRGB 233, 206, 32
        // Assuming "YellowTop"
        referenceCalibrationColors.put("YellowTop", new int[]{233, 206, 32});
        // Example: referenceCalibrationColors.put("YellowBottom", new int[]{220, 190, 30}); // Placeholder

        // Green: SRGB 103, 167, 86
        // Assuming "GreenTop"
        referenceCalibrationColors.put("GreenTop", new int[]{103, 167, 86});
        // Example: referenceCalibrationColors.put("GreenBottom", new int[]{90, 150, 75}); // Placeholder

        // Blue: SRGB 0, 140, 181
        // Assuming "BlueTop"
        referenceCalibrationColors.put("BlueTop", new int[]{0, 140, 181});

        Log.d(TAG, "Initialized " + referenceCalibrationColors.size() + " reference colors using NIX data.");

    }


private void logDetailedPatchComparisons() {
    Log.d(TAG, "logDetailedPatchComparisons: ENTERED. " +
            "Map IS " + (measuredCalibrationColors == null ? "NULL" : "NOT NULL") + ". " +
            "Size: " + (measuredCalibrationColors != null ? measuredCalibrationColors.size() : "N/A") + ". " +
            "Hash: " + (measuredCalibrationColors != null ? System.identityHashCode(measuredCalibrationColors) : "N/A"));


    Log.d("TestCall", "logDetailedPatchComparisons CALLED!"); // Your existing good log

    // 1. Check if the map is actually null or empty AT THIS POINT
    if (measuredCalibrationColors == null) {
        Log.e(TAG, "logDetailedPatchComparisons: measuredCalibrationColors is NULL!");
        analysisResultsText.append("Error: Measured colors map is null.\n");
        return;
    }

    // Add another log right before the isEmpty() check
    Log.d(TAG, "logDetailedPatchComparisons: Just before isEmpty() check. " +
            "Map IS " + (measuredCalibrationColors == null ? "NULL" : "NOT NULL") + ". " +
            "Size: " + (measuredCalibrationColors != null ? measuredCalibrationColors.size() : "N/A") + ". " +
            "Hash: " + (measuredCalibrationColors != null ? System.identityHashCode(measuredCalibrationColors) : "N/A"));

    if (measuredCalibrationColors.isEmpty()) {
        // THIS IS LIKELY WHERE YOUR "No measured calibration colors to compare yet" IS COMING FROM
        Log.i(TAG, "logDetailedPatchComparisons: No measured calibration colors to compare yet. Map is empty.");
        analysisResultsText.append("No measured calibration colors to compare.\n");
        return;
    }

    if (referenceCalibrationColors == null || referenceCalibrationColors.isEmpty()) {
        Log.w(TAG, "logDetailedPatchComparisons: Reference calibration colors are missing. Cannot compare.");
        analysisResultsText.append("Reference calibration colors missing.\n");
        return;
    }

    Log.d(TAG, "logDetailedPatchComparisons: Starting detailed comparison. Measured size: " + measuredCalibrationColors.size() + ", Reference size: " + referenceCalibrationColors.size());
    analysisResultsText.append("\n--- Patch Comparison Details ---\n");
    // analysisResultsText.append(String.format(Locale.US, "Applied WB Gains: R=%.2f, G=%.2f, B=%.2f\n", gainR, gainG, gainB));


    // Iterate through the REFERENCE patches to ensure we try to log for each expected patch
    for (Map.Entry<String, int[]> entry : referenceCalibrationColors.entrySet()) {
        String patchName = entry.getKey();
        int[] refRgb = entry.getValue();
        int[] measuredRgb = measuredCalibrationColors.get(patchName);

        if (measuredRgb != null) {
            // Option 1: Log the RAW measured colors
            String rawLog = String.format(Locale.US,
                    "Patch: %-15s | Ref: (%3d,%3d,%3d) | Raw Measured: (%3d,%3d,%3d)",
                    patchName, refRgb[0], refRgb[1], refRgb[2],
                    measuredRgb[0], measuredRgb[1], measuredRgb[2]);
            Log.d(TAG, rawLog);
            analysisResultsText.append(rawLog).append("\n");

            // Option 2: Log white-balance CORRECTED measured colors (if you want to see the effect of WB)
            // Ensure gainR, gainG, gainB have been calculated before this method is called.
            int correctedR = Math.min(255, Math.max(0, (int) (measuredRgb[0] * gainR)));
            int correctedG = Math.min(255, Math.max(0, (int) (measuredRgb[1] * gainG)));
            int correctedB = Math.min(255, Math.max(0, (int) (measuredRgb[2] * gainB)));

            String correctedLog = String.format(Locale.US,
                    "Patch: %-15s | Ref: (%3d,%3d,%3d) | WB Corrected: (%3d,%3d,%3d) | Raw Measured: (%3d,%3d,%3d)",
                    patchName, refRgb[0], refRgb[1], refRgb[2],
                    correctedR, correctedG, correctedB,
                    measuredRgb[0], measuredRgb[1], measuredRgb[2]);
            // Log.d(TAG, correctedLog); // Or choose which one to log/append
            analysisResultsText.append(String.format(Locale.US, "  -> WB Corrected: (%3d,%3d,%3d) using gains (R:%.2f,G:%.2f,B:%.2f)\n",
                    correctedR, correctedG, correctedB, gainR, gainG, gainB));


            // You can also calculate and log Delta E or other difference metrics here
            double deltaE = calculateDeltaE(refRgb, new int[]{correctedR, correctedG, correctedB}); // Assuming you have a DeltaE function
            analysisResultsText.append(String.format(Locale.US, "  -> Delta E (Ref vs WB Corrected): %.2f\n", deltaE));


        } else {
            String missingLog = "Patch: " + patchName + " | Ref: (" + refRgb[0] + "," + refRgb[1] + "," + refRgb[2] + ") | Measured: Not Found!";
            Log.w(TAG, missingLog);
            analysisResultsText.append(missingLog).append("\n");
        }
    }
    analysisResultsText.append("--------------------------------\n");
    // Update the TextView if you're accumulating results here
    // if (rgbValueTextView != null) {
    //     rgbValueTextView.setText(analysisResultsText.toString());
    // }
}

    // Example Delta E calculation (simplified CIEDE2000 or CIE76)
// For accurate color difference, CIEDE2000 is complex. CIE76 is simpler but less accurate.
    private double calculateDeltaE(int[] rgb1, int[] rgb2) {
        // Simple Euclidean distance in RGB space (CIE76 is based on L*a*b*, but this is a start)
        long dR = rgb1[0] - rgb2[0];
        long dG = rgb1[1] - rgb2[1];
        long dB = rgb1[2] - rgb2[2];
        return Math.sqrt(dR * dR + dG * dG + dB * dB);
    }
    private void initializeChartPatches() {
        chartPatches = new ArrayList<>();

        // Data from your constants (CHART_..._X, CHART_..._Y, etc.)
        chartPatches.add(new ChartPatch("YellowTop", CHART_YELLOW_TOP_X, CHART_YELLOW_TOP_Y, CHART_YELLOW_TOP_WIDTH, CHART_YELLOW_TOP_HEIGHT, Color.YELLOW));
        chartPatches.add(new ChartPatch("YellowBottom", CHART_YELLOW_BOTTOM_X, CHART_YELLOW_BOTTOM_Y, CHART_YELLOW_BOTTOM_WIDTH, CHART_YELLOW_BOTTOM_HEIGHT, Color.rgb(255,200,0))); // Darker Yellow

        chartPatches.add(new ChartPatch("GreenTop", CHART_GREEN_TOP_X, CHART_GREEN_TOP_Y, CHART_GREEN_TOP_WIDTH, CHART_GREEN_TOP_HEIGHT, Color.GREEN));
        chartPatches.add(new ChartPatch("GreenBottom", CHART_GREEN_BOTTOM_X, CHART_GREEN_BOTTOM_Y, CHART_GREEN_BOTTOM_WIDTH, CHART_GREEN_BOTTOM_HEIGHT, Color.rgb(0,200,0))); // Darker Green

        chartPatches.add(new ChartPatch("BlueTop", CHART_BLUE_TOP_X, CHART_BLUE_TOP_Y, CHART_BLUE_TOP_WIDTH, CHART_BLUE_TOP_HEIGHT, Color.BLUE));
        chartPatches.add(new ChartPatch("BlueBottom", CHART_BLUE_BOTTOM_X, CHART_BLUE_BOTTOM_Y, CHART_BLUE_BOTTOM_WIDTH, CHART_BLUE_BOTTOM_HEIGHT, Color.rgb(0,0,200))); // Darker Blue

        chartPatches.add(new ChartPatch("RedTop", CHART_RED_TOP_X, CHART_RED_TOP_Y, CHART_RED_TOP_WIDTH, CHART_RED_TOP_HEIGHT, Color.RED));
        chartPatches.add(new ChartPatch("RedBottom", CHART_RED_BOTTOM_X, CHART_RED_BOTTOM_Y, CHART_RED_BOTTOM_WIDTH, CHART_RED_BOTTOM_HEIGHT, Color.rgb(200,0,0))); // Darker Red

        chartPatches.add(new ChartPatch("WhiteTop", CHART_WHITE_TOP_X, CHART_WHITE_TOP_Y, CHART_WHITE_TOP_WIDTH, CHART_WHITE_TOP_HEIGHT, Color.WHITE));
        chartPatches.add(new ChartPatch("WhiteBottom", CHART_WHITE_BOTTOM_X, CHART_WHITE_BOTTOM_Y, CHART_WHITE_BOTTOM_WIDTH, CHART_WHITE_BOTTOM_HEIGHT, Color.LTGRAY)); // Light Gray for bottom white

        chartPatches.add(new ChartPatch("Gray", CHART_GRAY_X, CHART_GRAY_Y, CHART_GRAY_WIDTH, CHART_GRAY_HEIGHT, Color.GRAY));

        Log.d(TAG, "Initialized " + chartPatches.size() + " chart patches.");
    }
@Override
protected void onCreate(Bundle savedInstanceState) {
    Log.d(TAG, "onCreate: entered");
    super.onCreate(savedInstanceState);
    Log.d(TAG, "onCreate: started");
    setContentView(R.layout.activity_main);
    Log.d(TAG, "onCreate: MainActivity - setContentView completed");

    openCamera = findViewById(R.id.openMyCameraButton);
    Log.d(TAG, "onCreate: openCamera initialized: " + (openCamera != null));
//    clickedImage = findViewById(R.id.click_image);
    Log.d(TAG, "onCreate: clickedImage initialized: " + (clickedImage != null));

    Intent intent = getIntent();
    String imageUriString = intent.getStringExtra("imageUriFromCamera");

    if (imageUriString != null) {
        Log.d(TAG, "MainActivity: Received URI from camera: " + imageUriString);
        // --- For testing delay ---
//         new android.os.Handler(getMainLooper()).postDelayed(
//         () -> {
//         Intent processIntent = new Intent(MainActivity.this, MainActivity.class);
//         processIntent.putExtra("imageUriForDisplay", imageUriString);
//         processIntent.putExtra("calculatedPhValue", "pH Pending (Main Shown)");
//         startActivity(processIntent);
//         // finish(); // Optional
//         },
//         10000 // 2 second delay
//         );
        // --- End testing delay ---
        // --- QUICK CHANGE: Immediately start ProcessImage ---
//        Intent processIntent = new Intent(MainActivity.this, ProcessImage.class);
//        processIntent.putExtra("imageUriForDisplay", imageUriString); // Key for ProcessImage
//        // If ProcessImage *needs* a pH value, send a placeholder for now
//        processIntent.putExtra("calculatedPhValue", "pH Pending (Main Shown)");

//        Log.d(TAG, "MainActivity: Starting ProcessImage.");
//        startActivity(processIntent);

        // Optional: Finish MainActivity if you don't want it in the back stack
        // after ProcessImage starts in this quick flow.
        // finish();
        // If you DO want it in the backstack, so pressing back from ProcessImage
        // shows MainActivity again, then DON'T call finish().

    } else {
        Log.w(TAG, "MainActivity: No image URI received. Check ActivityCamera intent extras.");
        // Handle if MainActivity is started without the URI (e.g., normal app launch)
    }

//    Button btnShowResults = findViewById(R.id.btnShowResults);
//    btnShowResults.setOnClickListener(v -> {
//        Log.d(TAG, "onClick: Open Camera Button CLICKED");
//        Intent intent = new Intent(MainActivity.this, Results.class); // Replace with your target Activity
//        startActivity(intent);
//    });
//
//    Button btnShowProcess = findViewById(R.id.btnShowProcess);
//    btnShowProcess.setOnClickListener(v -> {
//        Log.d(TAG, "onClick: Open Camera Button CLICKED");
//        Intent intent = new Intent(MainActivity.this, ProcessImage.class); // Replace with your target Activity
//        startActivity(intent);
//    });
//
//    Button btnShowChart = findViewById(R.id.btnShowChart);
//    btnShowChart.setOnClickListener(v -> {
//        Log.d(TAG, "onClick: Open Camera Button CLICKED");
//        Intent intent = new Intent(MainActivity.this, ColorChart.class); // Replace with your target Activity
//        startActivity(intent);
//    });

    //seven_again was the 7.0 color square image
    colorSquare = findViewById(R.id.colorSquare); // Your ImageView
    Log.d(TAG, "onCreate: colorSquare initialized: " + (colorSquare != null));

    analysisResultsText = new StringBuilder();
    rgbValueTextView = findViewById(R.id.rgbValueTextView); // Matches XML ID
    Log.d(TAG, "onCreate: rgbValueTextView initialized: " + (rgbValueTextView != null));




    openMyCameraButton = findViewById(R.id.openMyCameraButton);
// --- Initialize the maps and lists here ---
    measuredCalibrationColors = new HashMap<>();
    referenceCalibrationColors = new HashMap<>();
    chartPatches = new ArrayList<>(); // Or chartPatchNames = new ArrayList<>();

    initializeCalibrationSquares();
    initializeChartPatches();


    phLookupTable = new ArrayList<>(); // Make sure you're always starting with a fresh list


    phLookupTable.add(new PhLookupEntry(4.5, 115, 86, 129));
    phLookupTable.add(new PhLookupEntry(5.0, 111, 85, 127));
    phLookupTable.add(new PhLookupEntry(5.5, 107, 87, 129));
    phLookupTable.add(new PhLookupEntry(6.0, 102, 72, 112));
    phLookupTable.add(new PhLookupEntry(6.5, 99, 100, 135));
    phLookupTable.add(new PhLookupEntry(7.0, 96, 106, 137));
    phLookupTable.add(new PhLookupEntry(7.5, 95, 113, 143));
    phLookupTable.add(new PhLookupEntry(8.0, 76, 93, 125));
    phLookupTable.add(new PhLookupEntry(8.5, 68, 96, 127));
    phLookupTable.add(new PhLookupEntry(9.0, 58, 86, 120));



    // Optional: Sort the table if it benefits your lookup logic or readability
//     Collections.sort(phLookupTable, Comparator.comparingDouble(PhLookupEntry::getPh));

    // --- Initialize the Paint object ---
    markerPaint = new Paint();
    markerPaint.setColor(Color.RED); // Color of the markers (e.g., red)
    markerPaint.setStyle(Paint.Style.FILL);
    markerPaint.setAntiAlias(true); // For smoother circles
    // -----------------------------------

    if (openCamera == null) {
        Log.e(TAG, "onCreate: FATAL - openCamera is null!");
        // You might want to throw an exception here or finish the activity
        // to make the problem very obvious during testing.
        return;
    }

    // In onCreate()
    openMyCameraButton.setOnClickListener(v -> {
        if (checkCameraPermission()) { // Your existing permission check
            Log.d(TAG, "onClick: MainActivity - Open Camera Button CLICKED. launchCustomCameraActivity()");
            launchCustomCameraActivity();
        } else {
            requestCameraPermission(); // Your existing permission request
        }
    });
    openCamera.setOnClickListener(v -> {
        Log.d(TAG, "openCamera: Button clicked"); // ADD THIS LINE if not already there
        // Check for camera permission before proceeding
        if (checkCameraPermission()) {
            Log.d(TAG, "openCamera: Permission already granted. Launching camera.");
            launchCamera();
        } else {
            Log.d(TAG, "openCamera: Permission NOT granted. Requesting permission.");
            requestCameraPermission();
        }
    });

    View button1 = findViewById(R.id.rv0wbu08ekt);
    button1.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "onCreate: MainActivity.lastCalculatedPh: " + MainActivity.lastCalculatedPh);
            System.out.println("Pressed");
            Intent intent = new Intent(MainActivity.this, Results.class);
            // You might want to pass data again, or Results activity might re-fetch/re-display
            // For instance, pass the URI of the image and the final pH.
            intent.putExtra("imageUriForDisplay", imageUriString);
            intent.putExtra("calculatedPhValue", MainActivity.lastCalculatedPh);
            startActivity(intent);
            finish(); // Usually, finish ColorChart.
        }
    });


    Log.d(TAG, "onCreate: finished");
    //temp comment out this file during generation
    loadAndDisplayTestImage();
//    rgbTextView = findViewById(R.id.rgb_value_text); // Add this TextView to activity_main.xml
}
    /**
     * Updates the background drawable of the colorSquare ImageView
     * based on the provided pH value.
     *
     * @param phValue The calculated pH value.
     */
    private void updateColorSquareForPh(double phValue) {
        if (colorSquare == null) {
            Log.e(TAG, "updateColorSquareForPh: colorSquare ImageView is null!");
            return;
        }

        int drawableResourceId;

        // Determine the correct drawable based on the pH value
        // This logic will depend on how your pH values map to your drawables.
        // You can use if-else if, a switch (for integers), or a more complex mapping.

        // Example using if-else if for ranges or specific values:
        if (phValue < 4.5) {
            drawableResourceId = R.drawable.four_five; // A default/out-of-range color
        } else if (phValue > 4.5 && phValue < 5.5) { // Example: pH 4.x
            drawableResourceId = R.drawable.five; // Assuming you have ph_color_4.xml
        } else if (phValue > 5.0 && phValue < 6.0) { // pH 5.x
            drawableResourceId = R.drawable.five_five;
        } else if (phValue > 5.5 && phValue < 6.5) { // pH 5.x
            drawableResourceId = R.drawable.six;
        }else if (phValue > 6.0 && phValue < 7.0) { // pH 6.0-6.4
            drawableResourceId = R.drawable.six_five;
        } else if (phValue > 6.5 && phValue < 7.5) { // pH 6.5-7.4 (e.g., around neutral)
            drawableResourceId = R.drawable.seven; // Your seven_again or similar
        }else if (phValue > 7.0 && phValue < 8.0) { // pH 5.x
            drawableResourceId = R.drawable.seven_five;
        } else if (phValue > 7.5 && phValue < 8.5) { // pH 7.5-8.4
            drawableResourceId = R.drawable.eight;
        }else if (phValue > 8.0 && phValue < 9.0) { // pH 5.x
            drawableResourceId = R.drawable.eight_five;
        } else if (phValue > 8.5 && phValue <= 9.0) { // pH 8.5-9.0
            drawableResourceId = R.drawable.nine;
        } else {
            drawableResourceId = R.drawable.seven; // Default for values outside expected range
        }
        // Add more conditions as needed for your pH scale and drawables

        Log.d(TAG, "Updating colorSquare for pH " + phValue + " with drawable ID: " + drawableResourceId);

        try {
            // Set the background drawable of the ImageView
//            colorSquare.setBackground(ContextCompat.getDrawable(this, drawableResourceId));
//            Log.e(TAG, "Set Background with drawable for colorSquare");
            // Or, if your drawables are meant to be the image source itself:
             colorSquare.setImageResource(drawableResourceId);
            Log.d(TAG, "Set Image Resource with drawable for colorSquare");

        } catch (Exception e) {
            Log.e(TAG, "Error setting drawable for colorSquare: " + e.getMessage());
            // Fallback to a default color or image if the resource is not found
            colorSquare.setBackgroundColor(Color.GRAY); // Example fallback
        }
    }
    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.CAMERA},
                CAMERA_PERMISSION_REQUEST_CODE
        );
    }

    private void launchCamera() {
    Log.d(TAG, "launchCamera: Method started."); // ADD THIS
        try {
            // Create a temporary image file to store the captured photo
            photoFile = createImageFile();

            // Get a content URI for the file using FileProvider
            photoUri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".fileprovider", // FileProvider authority (defined in manifest)
                    photoFile
            );

            // Create an intent to launch the camera app
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Tell the camera app where to save the image
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            // Grant temporary write permission to the camera app for the URI
            cameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            // Start the camera app and wait for the result
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
            // ... inside launchCamera() or similar ...
            Log.d(TAG, "Entering launchCamera/startCamera method.");

        } catch (IOException e) {
            Log.e(TAG, "Error creating image file or launching camera", e);
            Toast.makeText(this, "Error preparing camera.", Toast.LENGTH_SHORT).show();
            // e.printStackTrace(); // Keep for debugging if needed, but Log.e is often better
        }
    }

    // Function to create a temporary image file in the cache directory
    private File createImageFile() throws IOException {
        Log.d(TAG, "createImageFile: Method started."); // ADD THIS
        File imageDir = new File(getCacheDir(), "images");
        Log.d(TAG, "createImageFile: Image directory path: " + imageDir.getAbsolutePath()); // ADD THIS
        if (!imageDir.exists()) {
            Log.d(TAG, "createImageFile: Image directory does not exist. Attempting to create..."); // ADD THIS
            if (!imageDir.mkdirs()) {
                Log.e(TAG, "createImageFile: Failed to create image directory.");
                throw new IOException("Failed to create image directory: " + imageDir.getAbsolutePath());
            } else {
                Log.d(TAG, "createImageFile: Image directory created successfully."); // ADD THIS
            }
        } else {
            Log.d(TAG, "createImageFile: Image directory already exists."); // ADD THIS
        }
        File image = File.createTempFile("captured_", ".jpg", imageDir);
        Log.d(TAG, "createImageFile: Temp file created: " + image.getAbsolutePath()); // ADD THIS
        return image;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult: Received callback for requestCode: " + requestCode); // ADD THIS
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted
                Log.d(TAG, "onRequestPermissionsResult: Camera permission GRANTED by user.");
                Toast.makeText(this, "Camera permission granted", Toast.LENGTH_SHORT).show();
//                launchCamera(); // Now launch the camera
                launchCustomCameraActivity(); // Launch after permission granted
            } else {
                // Permission was denied
                Log.d(TAG, "onRequestPermissionsResult: Camera permission DENIED by user.");
                Toast.makeText(this, "Camera permission is required to use the camera.", Toast.LENGTH_LONG).show();
                // You might want to disable the button or explain further
                // openCamera.setEnabled(false); // Example
            }
        }
    }

    private void loadAndDisplayTestImage() {
    Log.d(TAG, "loadAndDisplayTestImage: Method started."); // ADD THIS
        try {
            Bitmap originalTestBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ph_8_5);
            if (originalTestBitmap == null) {
                Log.e(TAG, "Failed to decode R.drawable.ph. It is NULL."); // <<< ADD THIS
                Toast.makeText(this, "Failed to load test image resource.", Toast.LENGTH_SHORT).show();
                return; // Exit if bitmap is null
            } else {
                Log.d(TAG, "Successfully loaded R.drawable.ph. Dimensions: " +
                        originalTestBitmap.getWidth() + "x" + originalTestBitmap.getHeight()); // <<< ADD THIS
            }

            if (originalTestBitmap != null) {
                // Perform color correction
                Bitmap correctedBitmap = performColorCorrectionWithNewChart(originalTestBitmap);

                Bitmap bitmapToDisplayAndAnalyze;
                if (correctedBitmap != null) {
                    bitmapToDisplayAndAnalyze = correctedBitmap;
                } else {
                    Log.w(TAG, "Color correction failed, using original test image.");
                    bitmapToDisplayAndAnalyze = originalTestBitmap; // Fallback
                }

                if (clickedImage != null) {
                    clickedImage.setImageBitmap(bitmapToDisplayAndAnalyze);
                }
                // Now you can proceed to analyze this bitmapToDisplayAndAnalyze
                // for the pH strip, drawing markers, etc.
                analyzeBitmap(bitmapToDisplayAndAnalyze); // Your method for pH analysis and markers
                Log.i(TAG, "MainActivity: pH calculated: " + MainActivity.lastCalculatedPh);

            } else {
                Log.e(TAG, "Failed to load test image from drawables.");
                Toast.makeText(this, "Failed to load test image.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading/displaying test image", e);
            Toast.makeText(this, "Error with test image.", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap performColorCorrectionWithNewChart(Bitmap inputBitmap) {
        if (inputBitmap == null) {
            Log.e(TAG, "performColorCorrectionWithNewChart: Input bitmap is null.");
            return null; // Or return inputBitmap if you prefer to skip correction
        }

        Bitmap correctedBitmap = inputBitmap.copy(Bitmap.Config.ARGB_8888, true);

        // --- 1. Calculate average colors for the new white/gray patches ---
        double[] avgColorWhiteTop = calculateAverageColorForRegion(correctedBitmap,
                CHART_WHITE_TOP_X, CHART_WHITE_TOP_Y, CHART_WHITE_TOP_WIDTH, CHART_WHITE_TOP_HEIGHT);
        double[] avgColorWhiteBottom = calculateAverageColorForRegion(correctedBitmap,
                CHART_WHITE_BOTTOM_X, CHART_WHITE_BOTTOM_Y, CHART_WHITE_BOTTOM_WIDTH, CHART_WHITE_BOTTOM_HEIGHT);
        double[] avgColorGray = calculateAverageColorForRegion(correctedBitmap,
                CHART_GRAY_X, CHART_GRAY_Y, CHART_GRAY_WIDTH, CHART_GRAY_HEIGHT);

        // Collect all valid neutral patch averages
        List<double[]> neutralAverages = new ArrayList<>();
        if (avgColorWhiteTop != null) neutralAverages.add(avgColorWhiteTop);
        if (avgColorWhiteBottom != null) neutralAverages.add(avgColorWhiteBottom);
        if (avgColorGray != null) neutralAverages.add(avgColorGray);

        if (neutralAverages.isEmpty()) {
            Log.w(TAG, "No valid neutral (white/gray) patches found for white balance. Skipping correction.");
            return inputBitmap.copy(Bitmap.Config.ARGB_8888, true); // Return a copy of the original
        }

        // --- Calculate a combined average from all available neutral patches ---
        double totalR = 0, totalG = 0, totalB = 0;
        double totalPixels = 0;
        for (double[] avg : neutralAverages) {
            totalR += avg[0] * avg[3]; // avgR * count
            totalG += avg[1] * avg[3]; // avgG * count
            totalB += avg[2] * avg[3]; // avgB * count
            totalPixels += avg[3];     // count
        }

        if (totalPixels == 0) {
            Log.w(TAG, "Total pixels in neutral patches is zero. Skipping white balance.");
            return inputBitmap.copy(Bitmap.Config.ARGB_8888, true);
        }

        double finalAvgR = totalR / totalPixels;
        double finalAvgG = totalG / totalPixels;
        double finalAvgB = totalB / totalPixels;

        Log.d(TAG, String.format(Locale.US, "Combined Neutral Patch Avg RGB: (%.1f, %.1f, %.1f) from %d patches, %.0f pixels",
                finalAvgR, finalAvgG, finalAvgB, neutralAverages.size(), totalPixels));

        // --- 2. Calculate correction gains (Gray World assumption) ---
        // Aim to make the average of the neutral patches actually gray (R=G=B)
        double targetGray = (finalAvgR + finalAvgG + finalAvgB) / 3.0;
        if (targetGray == 0) { // Avoid division by zero
            Log.w(TAG, "Target gray for white balance is 0. Skipping correction.");
            return correctedBitmap;
        }


        double gainR = (finalAvgR > 0.5) ? targetGray / finalAvgR : 1.0; // Added > 0.5 check for robustness
        double gainG = (finalAvgG > 0.5) ? targetGray / finalAvgG : 1.0;
        double gainB = (finalAvgB > 0.5) ? targetGray / finalAvgB : 1.0;

        // --- ADJUST GAIN STRENGTH ---
        double correctionStrength = 0.7; // << TRY ADJUSTING THIS VALUE (e.g., 0.5 to 0.9)
        gainR = 1.0 + (gainR - 1.0) * correctionStrength;
        gainG = 1.0 + (gainG - 1.0) * correctionStrength;
        gainB = 1.0 + (gainB - 1.0) * correctionStrength;
        // --- END OF GAIN STRENGTH ADJUSTMENT ---

        // Optional: Clamp gains to prevent extreme amplification
        double maxGain = 2.5; // You might adjust this too, e.g., 2.0 or 2.5
        gainR = Math.min(gainR, maxGain);
        gainG = Math.min(gainG, maxGain);
        gainB = Math.min(gainB, maxGain);
        gainR = Math.max(gainR, 1.0 / maxGain);
        gainG = Math.max(gainG, 1.0 / maxGain);
        gainB = Math.max(gainB, 1.0 / maxGain);

        Log.d(TAG, String.format(Locale.US, "ADJUSTED Color Correction Gains (Strength: %.2f) - R: %.2f, G: %.2f, B: %.2f",
                correctionStrength, gainR, gainG, gainB));


//        Log.d(TAG, String.format(Locale.US, "Color Correction Gains - R: %.2f, G: %.2f, B: %.2f", gainR, gainG, gainB));

        // --- 3. Apply gains to the entire image ---
        int width = correctedBitmap.getWidth();
        int height = correctedBitmap.getHeight();
        int[] pixels = new int[width * height];
        correctedBitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        for (int i = 0; i < pixels.length; i++) {
            int color = pixels[i];
            int r = (int) (Color.red(color) * gainR);
            int g = (int) (Color.green(color) * gainG);
            int b = (int) (Color.blue(color) * gainB);

            pixels[i] = Color.rgb(
                    Math.min(255, Math.max(0, r)),
                    Math.min(255, Math.max(0, g)),
                    Math.min(255, Math.max(0, b))
            );
        }
        correctedBitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        Log.d(TAG, "White balance correction applied using new chart patches.");
        return correctedBitmap;
    }
// Add this helper method to your MainActivity.java

    /**
     * Calculates the average RGB color from a specified rectangular region of a Bitmap.
     *
     * @param bitmap The source Bitmap.
     * @param x The top-left x-coordinate of the region.
     * @param y The top-left y-coordinate of the region.
     * @param width The width of the region.
     * @param height The height of the region.
     * @return An array of 3 doubles [avgR, avgG, avgB], or null if the region is invalid or bitmap is null.
     */
    private double[] getAverageRgbFromBitmapRegion(Bitmap bitmap, int x, int y, int width, int height) {
        if (bitmap == null) {
            Log.e(TAG, "getAverageRgbFromBitmapRegion: Input bitmap is null.");
            return null;
        }

        // Validate region boundaries
        if (x < 0 || y < 0 || width <= 0 || height <= 0 ||
                x + width > bitmap.getWidth() || y + height > bitmap.getHeight()) {
            Log.e(TAG, "getAverageRgbFromBitmapRegion: Invalid region coordinates or dimensions.");
            Log.e(TAG, "Bitmap W: " + bitmap.getWidth() + " H: " + bitmap.getHeight());
            Log.e(TAG, "Region x: " + x + " y: " + y + " w: " + width + " h: " + height);
            return null;
        }

        long totalR = 0;
        long totalG = 0;
        long totalB = 0;
        int pixelCount = 0;

        // Get pixels from the specified region more efficiently
        int[] pixels = new int[width * height];
        try {
            // Parameters for getPixels:
            // pixels[]: The array to receive the T
            // offset: The first index in pixels[] to write to.
            // stride: The number of entries in pixels[] to skip after a row.
            //         Typically this is 'width' if you're processing the data linearly.
            // x, y: The x, y coordinate of the first pixel to read from the bitmap.
            // width, height: The number of pixels to read from each row and column.
            bitmap.getPixels(pixels, 0, width, x, y, width, height);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "getAverageRgbFromBitmapRegion: Error getting pixels from region. " + e.getMessage());
            return null; // Or handle more gracefully
        }


        for (int pixelColor : pixels) {
            totalR += Color.red(pixelColor);
            totalG += Color.green(pixelColor);
            totalB += Color.blue(pixelColor);
            pixelCount++;
        }

        if (pixelCount == 0) {
            Log.w(TAG, "getAverageRgbFromBitmapRegion: No pixels in the specified region (should not happen with validation).");
            return null; // Should ideally be caught by initial validation
        }

        double avgR = (double) totalR / pixelCount;
        double avgG = (double) totalG / pixelCount;
        double avgB = (double) totalB / pixelCount;

        Log.d(TAG, String.format(Locale.US, "Average RGB from region [x:%d, y:%d, w:%d, h:%d] = (R:%.1f, G:%.1f, B:%.1f) from %d pixels",
                x, y, width, height, avgR, avgG, avgB, pixelCount));

        return new double[]{avgR, avgG, avgB};
    }


    @FunctionalInterface
    interface TriFunction<T, U, V, W, R> {
        R apply(T t, U u, V v, W w);
    }

private static class ChartPatch {
    String name;
    int originalX;
    int originalY;
    int originalWidth;
    int originalHeight;
    int markerColor; // Optional: for drawing different colored markers

    ChartPatch(String name, int x, int y, int width, int height, int markerColor) {
        this.name = name;
        this.originalX = x;
        this.originalY = y;
        this.originalWidth = width;
        this.originalHeight = height;
        this.markerColor = markerColor;
    }
}
private static class ScaledRegion {
    int x, y, width, height;
    ScaledRegion(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
}

    private ScaledRegion getScaledCoordinates(int originalX, int originalY, int originalWidth, int originalHeight,
                                              float scaleX, float scaleY) {
        int scaledX = (int) (originalX * scaleX);
        int scaledY = (int) (originalY * scaleY);
        int scaledWidth = (int) (originalWidth * scaleX);
        int scaledHeight = (int) (originalHeight * scaleY);

        // Ensure width and height are at least 1 pixel to avoid errors with getPixel or drawing
        if (scaledWidth < 1) scaledWidth = 1;
        if (scaledHeight < 1) scaledHeight = 1;

        return new ScaledRegion(scaledX, scaledY, scaledWidth, scaledHeight);
    }
private void analyzeBitmap(Bitmap bitmap) {
    Log.d(TAG, "analyzeBitmap: Starting analysis.");
    if (bitmap == null) {
        Log.e(TAG, "analyzeBitmap: Input bitmap is null!");
        if (rgbValueTextView != null) {
            rgbValueTextView.setText("Error: No image to analyze.");
        }
        return;
    }
    if (analysisResultsText == null) { // Defensive check
        analysisResultsText = new StringBuilder();
    } else {
        analysisResultsText.setLength(0); // Clear previous results
    }
    //new code for getting avg RBG from area

    // 1. You've got the Bitmap.

    // 2. Perform Color Correction (RECOMMENDED)
    //    If you have your color correction chart and logic ready:
    Bitmap colorCorrectedBitmap = null;

    Bitmap bitmapToAnalyze = bitmap.copy(Bitmap.Config.ARGB_8888, false); // Work on a copy

// Inside analyzeBitmap, after you have 'bitmapToAnalyze'
    if (bitmapToAnalyze == null) {
        Log.e(TAG, "analyzeBitmap: Input bitmap is null!");
        // Update UI or return
        return;
    }

    int actualBitmapWidth = bitmapToAnalyze.getWidth();
    int actualBitmapHeight = bitmapToAnalyze.getHeight();

    int referenceImageWidth = 996; // From your Paint image
    int referenceImageHeight = 749; // From your Paint image

    Log.i(TAG, "Actual Bitmap: W=" + actualBitmapWidth + " H=" + actualBitmapHeight);
    Log.i(TAG, "Reference Bitmap: W=" + referenceImageWidth + " H=" + referenceImageHeight);
    float scaleX = (float) actualBitmapWidth / referenceImageWidth;
    float scaleY = (float) actualBitmapHeight / referenceImageHeight;

    Log.i(TAG, "Scale Factors: scaleX=" + scaleX + ", scaleY=" + scaleY);
    // Now, you can compare and decide if scaling is needed
    // ... rest of your analyzeBitmap method ...
    // --- Create a mutable copy OF THE BITMAP TO ANALYZE for drawing markers ---
    Bitmap mutableBitmapForDisplay = bitmapToAnalyze.copy(Bitmap.Config.ARGB_8888, true);
//    Canvas canvas = new Canvas(mutableBitmapForDisplay);
    // Initialize markerPaint (ensure it's defined in your class)
    // Paint markerPaint = new Paint();
    // markerPaint.setColor(Color.RED); // Or any color you like
    // markerPaint.setStyle(Paint.Style.FILL);
    // markerPaint.setAntiAlias(true);
    // final int MARKER_RADIUS = 10; // Define this constant

    // -------------------------------------------------------------------------

    int width = bitmapToAnalyze.getWidth();
    int height = bitmapToAnalyze.getHeight();

    if (width == 0 || height == 0) {
        Log.e(TAG, "analyzeBitmap: Bitmap to analyze has zero width or height.");
        if (rgbValueTextView != null) {
            rgbValueTextView.setText("Error: Invalid image dimensions.");
        }
        if (clickedImage != null) {
            clickedImage.setImageBitmap(mutableBitmapForDisplay);
        }
        return;
    }

    // 3. Extract the RGB color of the pH indicator patch.
    //    For now, we're using the "C" (Center) point as the primary indicator.
    //    You might refine this to be a specific, user-defined point or an average of a small area.

    //using avg of area of RBG values
    // These are EXAMPLE values, calculate them based on your setup and target overlay
    int roiWidth = 150;  // Width of the sampling area
    int roiHeight = 150; // Height of the sampling area
    int roiX = (width - roiWidth) / 2;    // Centered X
    int roiY = (height - roiHeight) / 2;  // Centered Y (adjust if your target is not exactly centered)

    // Log the intended ROI coordinates and bitmap dimensions for debugging
    Log.d(TAG, "analyzeBitmap: Bitmap dimensions W:" + width + " H:" + height);
    Log.d(TAG, "analyzeBitmap: Attempting to sample ROI at X:" + roiX + " Y:" + roiY + " W:" + roiWidth + " H:" + roiHeight);


    // --- Get average RGB from the defined ROI ---
    double[] averageRgbD = getAverageRgbFromBitmapRegion(bitmapToAnalyze, roiX, roiY, roiWidth, roiHeight);

    if (averageRgbD == null) {
        String errorMsg = "Could not get average RGB from defined region.";
        Log.e(TAG, errorMsg);
        rgbValueTextView.setText(errorMsg);
        return;
    }
    // Convert double[] to int[] if your estimatePhFromRgb expects int[]
    int[] averageRgb = new int[]{(int) averageRgbD[0], (int) averageRgbD[1], (int) averageRgbD[2]};

    // --- Proceed with pH estimation using the averageRgb ---
    // Assuming phLookupTable is already populated (e.g., in onCreate or a dedicated method)
    // And assuming estimatePhFromRgb and other necessary methods/variables are defined in your class
//    if (phLookupTable == null || phLookupTable.isEmpty()) {
//        Log.e(TAG, "pH Lookup Table is not initialized or is empty!");
//        rgbValueTextView.setText("Error: pH Lookup Table not ready.");
//        return;
//    }
    if (chartPatches == null || chartPatches.isEmpty()) {
        Log.e(TAG, "Chart patches not initialized!");
        // Handle error: update UI, return, etc.
        if (rgbValueTextView != null) {
            rgbValueTextView.setText("Error: Calibration patches not configured.");
        }
        return;
    }

    StringBuilder analysisResultsText = new StringBuilder("Calibration Square Averages:\n");
//    Map<String, int[]> measuredCalibrationColors = new HashMap<>();
    Canvas canvas1 = null; // Initialize canvas once if drawing markers
    if (mutableBitmapForDisplay != null && mutableBitmapForDisplay.isMutable() && markerPaint != null) {
        canvas1 = new Canvas(mutableBitmapForDisplay);
    }


    Log.d(TAG, "Processing " + chartPatches.size() + " chart patches...");

    if (chartPatches == null || chartPatches.isEmpty()) {
        Log.e(TAG, "analyzeBitmap: chartPatches list is null or empty. Cannot analyze.");
        return; // Or handle appropriately
    }
    Log.d(TAG, "analyzeBitmap: Number of chartPatches: " + chartPatches.size());


    if (measuredCalibrationColors == null) {
        measuredCalibrationColors = new HashMap<>();
        Log.d(TAG, "analyzeBitmap: measuredCalibrationColors was null, initialized now.");
    }
    Log.d(TAG, "analyzeBitmap: Clearing measuredCalibrationColors. Current size before clear: " + measuredCalibrationColors.size());
    measuredCalibrationColors.clear();
    Log.d(TAG, "analyzeBitmap: measuredCalibrationColors cleared. Current size after clear: " + measuredCalibrationColors.size());


    Log.d(TAG, "analyzeBitmap: Starting loop to populate measuredCalibrationColors.");
    for (ChartPatch patch : chartPatches) {
        Log.d(TAG, "analyzeBitmap: Processing patch - " + patch.name);
        // --- Scale the coordinates for the current patch ---
        ScaledRegion scaledPatchRegion = getScaledCoordinates(
                patch.originalX, patch.originalY,
                patch.originalWidth, patch.originalHeight,
                scaleX, scaleY);

        Log.d(TAG, patch.name + " Original: X=" + patch.originalX + " Y=" + patch.originalY + " W=" + patch.originalWidth + " H=" + patch.originalHeight);
        Log.d(TAG, patch.name + " Scaled  : X=" + scaledPatchRegion.x + " Y=" + scaledPatchRegion.y + " W=" + scaledPatchRegion.width + " H=" + scaledPatchRegion.height);

        // --- Get average RGB from the scaled region ---
        double[] avgRgbD = getAverageRgbFromBitmapRegion(bitmapToAnalyze,
                scaledPatchRegion.x, scaledPatchRegion.y,
                scaledPatchRegion.width, scaledPatchRegion.height);


        if (avgRgbD != null) {
            int[] rgb = new int[]{(int) avgRgbD[0], (int) avgRgbD[1], (int) avgRgbD[2]};
            Log.i(TAG, "analyzeBitmap: Measured " + patch.name + " RGB: (" + rgb[0] + "," + rgb[1] + "," + rgb[2] + ")");
            measuredCalibrationColors.put(patch.name, rgb);
            Log.d(TAG, "analyzeBitmap: Added to map. measuredCalibrationColors size now: " + measuredCalibrationColors.size() + ", Last key: " + patch.name);


            // --- Optional: Draw marker (using scaled coordinates) ---
            if (canvas1 != null && markerPaint != null) {
                markerPaint.setColor(patch.markerColor); // Use patch-specific color
                markerPaint.setStyle(Paint.Style.STROKE);
                markerPaint.setStrokeWidth(3); // Adjust as needed
                canvas1.drawRect(scaledPatchRegion.x, scaledPatchRegion.y,
                        scaledPatchRegion.x + scaledPatchRegion.width,
                        scaledPatchRegion.y + scaledPatchRegion.height, markerPaint);
            }
        } else {
            String errorMsg = patch.name + ": Could not get average RGB.";
            analysisResultsText.append(errorMsg).append("\n");
            Log.w(TAG, errorMsg + " (Scaled ROI: X:" + scaledPatchRegion.x + " Y:" + scaledPatchRegion.y + " W:" + scaledPatchRegion.width + " H:" + scaledPatchRegion.height + ")");
        }

    }
    Log.d(TAG, "Finished processing chart patches.");
    Log.d(TAG, "analyzeBitmap: Finished loop. Final measuredCalibrationColors size: " + measuredCalibrationColors.size());

    // --- MOVE WHITE BALANCE CALCULATION HERE ---
// --- It should only run ONCE after all patches are measured ---
    // Pick one of your reliable neutral patches with NIX data
    String neutralPatchForWhiteBalance = "Gray"; // Or "WhiteTop"
    // String neutralPatchForWhiteBalance = "WhiteTop";

    int[] measuredNeutral = measuredCalibrationColors.get(neutralPatchForWhiteBalance);
    int[] referenceNeutral = referenceCalibrationColors.get(neutralPatchForWhiteBalance);
    float gainR = 1.0f, gainG = 1.0f, gainB = 1.0f;

    if (measuredNeutral != null && referenceNeutral != null &&
            measuredNeutral[0] > 5 && measuredNeutral[1] > 5 && measuredNeutral[2] > 5) { // Avoid division by zero/near-zero, increase threshold slightly
        gainR = (float) referenceNeutral[0] / measuredNeutral[0];
        gainG = (float) referenceNeutral[1] / measuredNeutral[1];
        gainB = (float) referenceNeutral[2] / measuredNeutral[2];

        // Optional: Clamp gains to prevent extreme shifts (e.g., if a patch is very dark)
        float maxGain = 3.0f; // Example max gain
        float minGain = 0.33f; // Example min gain
        gainR = Math.max(minGain, Math.min(maxGain, gainR));
        gainG = Math.max(minGain, Math.min(maxGain, gainG));
        gainB = Math.max(minGain, Math.min(maxGain, gainB));

        Log.i(TAG, String.format(Locale.US, "Using %s for WB. Gains: R=%.2f, G=%.2f, B=%.2f",
                neutralPatchForWhiteBalance, gainR, gainG, gainB));
        Log.d(TAG, String.format(Locale.US, "Measured %s: (%d,%d,%d), Reference %s: (%d,%d,%d)",
                neutralPatchForWhiteBalance, measuredNeutral[0], measuredNeutral[1], measuredNeutral[2],
                neutralPatchForWhiteBalance, referenceNeutral[0], referenceNeutral[1], referenceNeutral[2]));

    } else {
        Log.w(TAG, "Could not perform white balance: " + neutralPatchForWhiteBalance + " patch data missing, invalid, or too dark.");
    }
    // Call it AFTER the loop has finished populating ALL patches

    // ... white balance calculations using measuredCalibrationColors ...
    Log.i(TAG, "Using " + neutralPatchForWhiteBalance + " for WB. Gains: R=" + gainR + ", G=" + gainG + ", B=" + gainB);

    // *** THIS IS THE CORRECT PLACE TO CALL IT ***
    Log.d(TAG, "analyzeBitmap: PRE-CALL to logDetailedPatchComparisons. " +
            "Map IS " + (measuredCalibrationColors == null ? "NULL" : "NOT NULL") + ". " +
            "Size: " + (measuredCalibrationColors != null ? measuredCalibrationColors.size() : "N/A") + ". " +
            "Hash: " + (measuredCalibrationColors != null ? System.identityHashCode(measuredCalibrationColors) : "N/A"));
    logDetailedPatchComparisons();
    Log.d(TAG, "Finished processing chart patches.");


//    double estimatedPh = estimatePhFromRgb(averageRgb, phLookupTable); // Your existing method
    // 4. Pass this extracted RGB array to your predictPH method.
        String predictedPHValue = predictPH(averageRgb); // This should call your NEW predictPH
//        String rgbText =  String.format(" Spot RGB: ( %d )", averageRgb);
//        String rgbText =  String.format(Locale.US, "Avg RGB: (%d, %d, %d)\n",
//            averageRgb[0], averageRgb[1], averageRgb[2]);
//    MainActivity.lastCalculatedPh = Double.parseDouble(predictedPHValue);
        String phText = "Predicted pH: " + predictedPHValue;
    Log.d(TAG, "Analysis - " + phText);
//    String analysisResult = String.format(Locale.US, "Avg RGB: (%d, %d, %d)\nEst. pH: %.1f",
//            averageRgb[0], averageRgb[1], averageRgb[2], predictedPHValue);
    // Update: Based on your previous code snippet, you likely ALREADY have the correct format:
 String analysisRGBResult = String.format(Locale.US, "Avg RGB: (%d, %d, %d)\nEst. pH: " + predictedPHValue,
 averageRgb[0], averageRgb[1], averageRgb[2]);
    String analysisResult = String.format(Locale.US, predictedPHValue);
    rgbValueTextView.setText(analysisResult);
    MainActivity.lastCalculatedPh = Double.parseDouble(predictedPHValue);
    Log.i(TAG, analysisResult);
    Log.i(TAG, "MainActivity.lastCalculatedPh = " + MainActivity.lastCalculatedPh);

    Log.d(TAG, "Bitmap analysis complete using ROI.");
    updateColorSquareForPh(Double.parseDouble(predictedPHValue));


    // 5. Display the returned pH string (and the marked image).
    if (clickedImage != null) {
        clickedImage.setImageBitmap(mutableBitmapForDisplay); // Show the bitmap with markers
    } else {
        Log.w(TAG, "clickedImage ImageView is null. Cannot display marked image.");
    }

    Log.d(TAG, "analyzeBitmap: Analysis complete.");
}


    // Helper class to store point data
    private static class PointData {
        String name;
        int x;
        int y;

        PointData(String name, int x, int y) {
            this.name = name;
            this.x = x;
            this.y = y;
        }
    }
    private void launchCustomCameraActivity() {
        Log.d(TAG, "launchCustomCameraActivity: METHOD ENTERED in MainActivity.");
        Intent intent = new Intent(this, ActivityCamera.class);
        // You can use registerForActivityResult for a more modern approach,
        // but startActivityForResult is fine for now.
        startActivityForResult(intent, REQUEST_CODE_CUSTOM_CAMERA);
        Log.d(TAG, "launchCustomCameraActivity: CALLED startActivityForResult with request code: " + REQUEST_CODE_CUSTOM_CAMERA);

    }
    // Keep your existing onActivityResult for when you switch back to camera
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Log.d(TAG, "onActivityResult: before analyzeBitmap. ");
        Log.d(TAG, "onActivityResult: ENTRY in MainActivity - requestCode=" + requestCode + ", resultCode=" + resultCode + ", data=" + data); // Already have this

        // ...
        if (requestCode == REQUEST_CODE_CUSTOM_CAMERA) {
            if (resultCode == RESULT_OK && data != null) {
                Log.d(TAG, "onActivityResult: Request code MATCHES.");
                String imageUriString = data.getStringExtra(ActivityCamera.EXTRA_IMAGE_URI);
                if (imageUriString != null) {
                    Uri savedImageUri = Uri.parse(imageUriString);
                    Log.d(TAG, "Received image URI from ActivityCamera: " + savedImageUri);
                    // Now process this image URI

                    // 1. Start the "Processing..." screen (ProcessImage.java)
//                    Intent processIntent = new Intent(MainActivity.this, ProcessImage.class);
//                    startActivity(processIntent);

                    try {
                        Bitmap originalCapturedBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), savedImageUri);
                        if (originalCapturedBitmap != null) {
                            Bitmap mutableCapturedBitmap = originalCapturedBitmap.copy(Bitmap.Config.ARGB_8888, true);

                            Bitmap correctedBitmap = performColorCorrectionWithNewChart(mutableCapturedBitmap);
                            Bitmap bitmapToDisplayAndAnalyze = (correctedBitmap != null) ? correctedBitmap : mutableCapturedBitmap;

                            if (clickedImage != null) {
                                clickedImage.setImageBitmap(bitmapToDisplayAndAnalyze);
                                // clickedImage.setVisibility(View.VISIBLE); // if it was hidden
                            }
                            analyzeBitmap(bitmapToDisplayAndAnalyze);
                            Log.d(TAG, "onActivityResult1: <<< RETURNED FROM analyzeBitmap. New lastCalculatedPh = " + MainActivity.lastCalculatedPh);
                            // ...
                        } else {
                            Log.e(TAG, "Failed to load bitmap from received URI.");
                            Toast.makeText(this, "Failed to load image.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Error processing image from ActivityCamera", e);
                        Toast.makeText(this, "Error processing image.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.w(TAG, "Image URI string is null in onActivityResult.");
                }
            } else if (resultCode == RESULT_CANCELED) {
                Log.w(TAG, "onActivityResult: Request code " + requestCode + " DOES NOT match " + REQUEST_CODE_CUSTOM_CAMERA);
                Log.d(TAG, "Custom camera action was canceled.");
                Toast.makeText(this, "Camera cancelled.", Toast.LENGTH_SHORT).show();
            }
        } else { //(requestCode == YOUR_OLD_CAMERA_REQUEST_CODE)
            super.onActivityResult(requestCode, resultCode, data);
            Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);
            if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
                if (photoUri != null) {
                    try {
                        Log.d(TAG, "onActivityResult: Image captured successfully. URI: " + photoUri.toString());
                        // Load the full-size image from the URI
                        Bitmap capturedBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);

                        // ****** ADD THIS LOG IMMEDIATELY ******
                        if (capturedBitmap != null) {
                            Log.d(TAG, "Dimensions of 'capturedBitmap' immediately after loading: Width=" + capturedBitmap.getWidth() + ", Height=" + capturedBitmap.getHeight());
                        } else {
                            Log.e(TAG, "'capturedBitmap' is null after MediaStore.Images.Media.getBitmap!");
                            // Handle this error - perhaps return or show a toast
                            return;
                        }
                        // Analyze the captured bitmap
                        analyzeBitmap(capturedBitmap);
                        Log.d(TAG, "onActivityResult2: <<< RETURNED FROM analyzeBitmap. New lastCalculatedPh = " + MainActivity.lastCalculatedPh);
                        // ...
                    } catch (IOException e) {
                        Log.e(TAG, "onActivityResult: Error loading bitmap from URI", e);
                        Toast.makeText(this, "Failed to load image from camera.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.w(TAG, "onActivityResult: photoUri is null, but result was OK.");
                    Toast.makeText(this, "Image capture completed, but URI is missing.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.d(TAG, "onActivityResult: Camera action cancelled or failed. resultCode: " + resultCode);
                if (resultCode != RESULT_CANCELED) {
                    Toast.makeText(this, "Camera action failed.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}