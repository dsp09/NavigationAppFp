package org.tracker.prashu.savedRoutes;


// a simple plain old java object class that stores the details of a saved map.
public class SavedRoutesData {

    private String id;
    private String email;
    private String name;
    private String date;
    private String time;
    private String start_lat;
    private String start_lon;
    private String current_lat;
    private String current_lon;
    private String polyline_array;
    private String distance;
    private String duration;
    private String color;

    // constructor of the pojo class.
    public SavedRoutesData(String id, String email, String name, String date, String time, String start_lat, String start_lon, String cuttent_lat, String current_lon, String polyline_array, String duration, String distance, String color) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.date = date;
        this.time = time;
        this.start_lat = start_lat;
        this.start_lon = start_lon;
        this.current_lat = cuttent_lat;
        this.current_lon = current_lon;
        this.polyline_array = polyline_array;
        this.distance = distance;
        this.duration = duration;
        this.color = color;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getStart_lat() {
        return start_lat;
    }

    public String getStart_lon() {
        return start_lon;
    }

    public String getCurrent_lat() {
        return current_lat;
    }

    public String getCurrent_lon() {
        return current_lon;
    }

    public String getPolyline_array() {
        return polyline_array;
    }


    public String getDistance() {
        return distance;
    }

    public String getDuration() {
        return duration;
    }

    public String getColor() {
        return color;
    }


}
