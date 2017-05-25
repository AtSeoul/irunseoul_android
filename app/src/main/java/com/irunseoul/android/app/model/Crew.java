package com.irunseoul.android.app.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;

/**
 * Created by hassanabid on 9/16/16.
 */

@IgnoreExtraProperties
public class Crew {

    public static final String ARG_NAME = "name";
    public static final String ARG_INSTAGRAM = "instagram";
    public static final String ARG_SCHEDULE = "schedule";
    public static final String ARG_EMAIL = "email";
    public static final String ARG_VIDEO = "video";
    public static final String ARG_LOCATION = "location";
    public static final String ARG_WEBSITE = "website";
    public static final String ARG_DESCRIPTION = "description";
    public static final String ARG_LOGO_URL = "logo_url";




    public String name;
    public String instagram;
    public String schedule;
    public String email;
    public String video;
    public String location;
    public String website;
    public String description;
    public String logo_url;


    public Crew() {

    }

}
