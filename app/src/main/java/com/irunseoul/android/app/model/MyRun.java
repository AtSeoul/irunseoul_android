package com.irunseoul.android.app.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.sweetzpot.stravazpot.activity.model.Activity;
import com.sweetzpot.stravazpot.common.model.Distance;
import com.sweetzpot.stravazpot.common.model.Speed;
import com.sweetzpot.stravazpot.common.model.Time;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hassanabid on 9/16/16.
 */

@IgnoreExtraProperties
public class MyRun {

    public static final String ARG_TITLE = "title";
    public static final String ARG_CITY = "city";
    public static final String ARG_DATE = "date";
    public static final String ARG_EMAIL = "email";
    public static final String ARG_HOST = "host";
    public static final String ARG_LATITUDE = "latitude";
    public static final String ARG_LOCATION = "location";
    public static final String ARG_LONGTIDUDE = "longitude";
    public static final String ARG_MAP_URL = "map_url";
    public static final String ARG_TEMPERATURE = "temperature";
    public static final String ARG_RACE = "race";
    public static final String ARG_WEATHER = "weather";
    public static final String ARG_WEBSITE = "website";
    public static final String ARG_APPLICATION_PERIOD = "application_period";
    public static final String ARG_PHONE = "phone";



    public String uid;
    public String city;
    public String date;
    public String host;
    public String latitude;
    public String location;
    public String longitude;
    public String map_url;
    public String temperature;
    public String title;
    public String weather;
    public String website;

    public String run_app;
    public String run_id;
    public String run_name;
    public String distance;
    public String moving_time;
    public String moving_time_min;
    public String start_date;
    public String start_date_local;
    public String average_speed;
    public String type;
    public String photo_url;



    public MyRun() {

    }

    public MyRun(String uid, String city, String date, String host, String title, String weather, String website,
                 String latitude, String longitude, String location, String map_url, String temperature,
                 String run_app, Activity activity, String photo_url) {

        this.uid = uid;
        this.city = city;
        this.host = host;
        this.title = title;
        this.weather = weather;
        this.website = website;
        this.latitude  = latitude;;
        this.longitude = longitude;
        this.location = location;
        this.date = date;
        this.map_url = map_url;
        this.temperature = temperature;
        this.run_app = run_app;
        this.start_date = activity.getStartDate().toString();
        this.start_date_local = activity.getStartDateLocal().toString();
        this.average_speed = getKMperH(activity.getAverageSpeed());
        this.run_id = String.valueOf(activity.getID());
        this.run_name = activity.getName();
        this.distance = getDistancKM(activity.getDistance().getMeters());
        this.moving_time = getFormattedMovingTime(activity.getMovingTime());
        this.moving_time_min = getFormattedMovingTimeMin(activity.getMovingTime());
        this.type = activity.getType().toString();
        this.photo_url = photo_url;


    }

    // [START run_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("city", city);
        result.put("host", host);
        result.put("date", date);
        result.put("weather", weather);
        result.put("title", title);
        result.put("website", website);
        result.put("latitude", latitude);
        result.put("longitude", longitude);
        result.put("location", location);
        result.put("map_url", map_url);
        result.put("temperature", temperature);
        result.put("run_app", run_app);
        result.put("run_id", run_id);
        result.put("run_name", run_name);
        result.put("distance", distance);
        result.put("moving_time", moving_time);
        result.put("moving_time_min", moving_time_min);
        result.put("start_date", start_date);
        result.put("start_date_local", start_date_local);
        result.put("average_speed", average_speed);
        result.put("type", type);
        result.put("photo_url", photo_url);


        return result;
    }
    // [END run_to_map]

    // Helper methods

    private String getKMperH(Speed speed) {

        Float kmh = speed.getMetersPerSecond() * (3600.0f/1000.0f);
        String result = String.valueOf((float) Math.round(kmh * 100)/100);

        return result;
    }

    private String getFormattedMovingTime(Time time) {

        String movingTime = "";
        int totalSecs = time.getSeconds();

        int hours = totalSecs / 3600;
        int minutes = (totalSecs % 3600) / 60;
        int secs = totalSecs % 60;

        movingTime = String.format("%02d:%02d:%02d", hours, minutes, secs);

        return movingTime;
    }

    private String getFormattedMovingTimeMin(Time time) {

        String movingTime = "";
        int minutes = time.getSeconds() / 60;
        movingTime = String.format("%02d",  minutes);

        return movingTime;
    }

    private String getDistancKM(Float meters) {

        String km = "";
        Float kms = meters/1000.0f;
        km = String.valueOf((float) Math.round(kms*100) /100);
        return km;
    }

}
