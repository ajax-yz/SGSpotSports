package com.example.android.sgspotsports;

public class Requests {

    public String request_type;
    public long timestamp;

    public Requests(){
        // Empty constructor
    }

    public Requests(String request_type, long timestamp) {
        this.request_type = request_type;
        this.timestamp = timestamp;
    }

    public String getRequest_type() {
        return request_type;
    }

    public void setRequest_type(String request_type) {
        this.request_type = request_type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
