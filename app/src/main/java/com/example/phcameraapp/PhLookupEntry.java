// PhLookupEntry.java
package com.example.phcameraapp; // Or your actual package name

public class PhLookupEntry {
    private double ph;
    private int r;
    private int g;
    private int b;

    public PhLookupEntry(double ph, int r, int g, int b) {
        this.ph = ph;
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public double getPh() {
        return ph;
    }

    public int getR() {
        return r;
    }

    public int getG() {
        return g;
    }

    public int getB() {
        return b;
    }

    // Optional: For easier logging
    @Override
    public String toString() {
        return "pH: " + ph + ", R: " + r + ", G: " + g + ", B: " + b;
    }
}