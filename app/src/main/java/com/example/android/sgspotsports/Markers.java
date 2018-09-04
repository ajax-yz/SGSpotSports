package com.example.android.sgspotsports;

import com.google.android.gms.maps.model.LatLng;

public class Markers {

    private LatLng latLng;
    private Double lat;
    private Double lng;
    private String marker_id, owner_id, facility_name, description, imageUrl;
    private String address, facility_type;
    private String building_name, rating;

    public Markers() {
    }



    public Markers(Double lat, Double lng, String owner_id, String facility_name, String description, String imageUrl, String address, String facility_type) {
        this.lat = lat;
        this.lng = lng;
        this.owner_id = owner_id;
        this.facility_name = facility_name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.address = address;
        this.facility_type = facility_type;
    }

    public Markers(Double lat, Double lng, String marker_id, String owner_id, String facility_name, String description, String imageUrl, String address, String facility_type, String building_name, String rating) {
        this.lat = lat;
        this.lng = lng;
        this.marker_id = marker_id;
        this.owner_id = owner_id;
        this.facility_name = facility_name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.address = address;
        this.facility_type = facility_type;
        this.building_name = building_name;
        this.rating = rating;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String getFacility_type() {
        return facility_type;
    }

    public void setFacility_type(String facility_type) {
        this.facility_type = facility_type;
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
