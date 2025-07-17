package com.example.phcameraapp;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;

public class CalibrationSquare {
    String name;
    Rect region; // Using android.graphics.Rect for simplicity

    // Constructor using TL and BR points
    public CalibrationSquare(String name, int tlX, int tlY, int brX, int brY) {
        this.name = name;
        this.region = new Rect(tlX, tlY, brX, brY);
    }

    // Alternative constructor if you prefer to pass all 4 points (though Rect usually uses TL and BR)
    // public CalibrationSquare(String name, int tlX, int tlY, int trX, int trY, int blX, int blY, int brX, int brY) {
    //     this.name = name;
    //     // For a non-rotated rectangle, min of X's and Y's for TL, max for BR
    //     int left = Math.min(tlX, blX);
    //     int top = Math.min(tlY, trY);
    //     int right = Math.max(trX, brX);
    //     int bottom = Math.max(blY, brY);
    //     this.region = new Rect(left, top, right, bottom);
    // }

    public String getName() {
        return name;
    }

    public Rect getRegion() {
        return region;
    }

    // Optional: Method to get average color from this region of a bitmap
    public int[] getAverageColor(Bitmap bitmap) {
        if (bitmap == null || region == null) {
            Log.e("CalibrationSquare", "Bitmap or region is null for " + name);
            return null;
        }

        int startX = region.left;
        int startY = region.top;
        int width = region.width();
        int height = region.height();

        // Boundary checks (important!)
        if (startX < 0 || startY < 0 || startX + width > bitmap.getWidth() || startY + height > bitmap.getHeight()) {
            Log.e("CalibrationSquare", "Region for " + name + " is out of bitmap bounds.");
            Log.d("CalibrationSquare", "Region: " + region.toShortString() + ", Bitmap: W" + bitmap.getWidth() + " H" + bitmap.getHeight());
            return null;
        }
        if (width <= 0 || height <= 0) {
            Log.e("CalibrationSquare", "Region for " + name + " has zero or negative width/height.");
            return null;
        }


        long sumR = 0;
        long sumG = 0;
        long sumB = 0;
        int pixelCount = 0;

        for (int y = startY; y < startY + height; y++) {
            for (int x = startX; x < startX + width; x++) {
                int pixel = bitmap.getPixel(x, y);
                sumR += Color.red(pixel);
                sumG += Color.green(pixel);
                sumB += Color.blue(pixel);
                pixelCount++;
            }
        }

        if (pixelCount == 0) {
            Log.e("CalibrationSquare", "No pixels sampled for " + name);
            return null; // Avoid division by zero
        }

        return new int[]{
                (int) (sumR / pixelCount),
                (int) (sumG / pixelCount),
                (int) (sumB / pixelCount)
        };
    }
}
