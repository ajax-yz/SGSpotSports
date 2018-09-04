package com.example.android.sgspotsports;

import com.google.android.gms.maps.model.LatLng;

public class Markers {

    private LatLng latLng;
    private String marker_id, owner_id, facility_name, description, imageUrl;
    private String address, building_name, rating;

    public Markers() {
    }



    public Markers(LatLng latLng, String owner_id, String facility_name, String description, String imageUrl, String address) {
        this.latLng = latLng;
        this.owner_id = owner_id;
        this.facility_name = facility_name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.address = address;
    }

    public Markers(LatLng latLng, String marker_id, String owner_id, String facility_name, String description, String imageUrl, String address, String building_name, String rating) {
        this.latLng = latLng;
        this.marker_id = marker_id;
        this.owner_id = owner_id;
        this.facility_name = facility_name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.address = address;
        this.building_name = building_name;
        this.rating = rating;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBuilding_name() {
        return building_name;
    }

    public void setBuilding_name(String building_name) {
        this.building_name = building_name;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getMarker_id() {
        return marker_id;
    }

    public void setMarker_id(String marker_id) {
        this.marker_id = marker_id;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id;
    }

    public String getFacility_name() {
        return facility_name;
    }

    public void setFacility_name(String facility_name) {
        this.facility_name = facility_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
