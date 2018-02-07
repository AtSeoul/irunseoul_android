package com.irunseoul.android.app.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;

/**
 * Created by hassanabid on 9/16/16.
 */

@IgnoreExtraProperties
public class Event {

    public static final String ARG_TITLE = "title";
    public static final String ARG_CITY = "city";
    public static final String ARG_DATE = "date";
    public static final String ARG_EMAIL = "email";
    public static final String ARG_HOST = "host";
    public static final String ARG_LATITUDE = "latitude";
    public static final String ARG_LOCATION = "location";
    public static final String LONGITUDE = "longitude";
    public static final String ARG_MAP_URL = "map_url";
    public static final String ARG_TEMPERATURE = "temperature";
    public static final String ARG_RACE = "race";
    public static final String ARG_WEATHER = "weather";
    public static final String ARG_WEBSITE = "website";
    public static final String ARG_APPLICATION_PERIOD = "application_period";
    public static final String ARG_PHONE = "phone";
    public static final String ARG_KEY = "event_key";



    public String application_period;
    public String city;
    public String date;
    public String email;
    public String host;
    public String latitude;
    public String location;
    public String longitude;
    public String map_url;
    public String phone;
    public List<String> race;
    public String temperature;
    public String title;
    public String weather;
    public String website;
    public String eventKey = "";


    public  Event() {

    }

}
