package com.example.android.sgspotsports;

public class MarkerItems {

    private String mSportsType;
    private int mMarkerImage;

    public MarkerItems(String mSportsType, int mMarkerImage) {
        this.mSportsType = mSportsType;
        this.mMarkerImage = mMarkerImage;
    }

    public MarkerItems() {
    }

    public String getmSportsType() {
        return mSportsType;
    }

    public void setmSportsType(String mSportsType) {
        this.mSportsType = mSportsType;
    }

    public int getmMarkerImage() {
        return mMarkerImage;
    }

    public void setmMarkerImage(int mMarkerImage) {
        this.mMarkerImage = mMarkerImage;
    }
}
