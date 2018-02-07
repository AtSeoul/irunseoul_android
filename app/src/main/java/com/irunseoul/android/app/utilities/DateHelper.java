package com.irunseoul.android.app.utilities;

import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by hassanabid on 16/04/2017.
 */

public class DateHelper {

    public static String getCurrentDate() {

        DateTime now = DateTime.now();

        DateTime twoDaysBefore = now.minusDays(4);
        Log.d("DateHelper", " date  :" + twoDaysBefore + " year; " + twoDaysBefore.year().getAsShortText() +
                " month : " + twoDaysBefore.monthOfYear().getAsString()+ " day : " + twoDaysBefore.dayOfMonth().getAsText());

        //2017/03/15 08:00
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm");

        Log.d("DateHelper", " fmt : " + fmt.print(twoDaysBefore));

        return fmt.print(twoDaysBefore);

    }

    public static String getTodaysDate() {

        DateTime now = DateTime.now();

        //2017/03/15 08:00
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm");

        Log.d("DateHelper", " fmt : " + fmt.print(now));

        return fmt.print(now);

    }

    public static String getCurrentYear() {

        DateTime now = DateTime.now();
        return now.year().getAsShortText();
    }

    public static String getPreviousYear() {

        DateTime now = DateTime.now();
        return now.year().getAsShortText();
    }

    public static int getDaysDiff(String event_date) {

        int diff = 0;

        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm");
        DateTime start = DateTime.now();

        DateTime end = fmt.parseDateTime(event_date);

        diff = Days.daysBetween(start.toLocalDate(), end.toLocalDate()).getDays();

        return diff;
    }

    public static String getRaceYear(String event_date) {

        String result = "";

        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm");

        DateTime raceDate = fmt.parseDateTime(event_date);

        result = raceDate.year().getAsShortText();

        return result;
    }

    public static boolean isApplicationPeriod(String application_end) {

        boolean result = false;

        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy/MM/dd");

        DateTime end_date = fmt.parseDateTime(application_end);

        DateTime today = DateTime.now();

        int diff = Days.daysBetween(today.toLocalDate(), end_date.toLocalDate()).getDays();

        if(diff > 0)
            result = true;

        return result;
    }
}
