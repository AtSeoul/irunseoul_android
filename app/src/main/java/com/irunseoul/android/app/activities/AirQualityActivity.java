package com.irunseoul.android.app.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.irunseoul.android.app.R;

import kr.go.seoul.airquality.AirQualityButtonTypeA;
import kr.go.seoul.airquality.AirQualityButtonTypeB;
import kr.go.seoul.airquality.AirQualityTypeMini;


public class AirQualityActivity extends AppCompatActivity {

    public static String KEY = "7842726c6d68617339367355416972";

    private AirQualityButtonTypeA typeA;
    private AirQualityButtonTypeB typeB;

    private AirQualityTypeMini typeMini;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air_quality);

        typeA = (AirQualityButtonTypeA) findViewById(R.id.type_a);
        typeB = (AirQualityButtonTypeB) findViewById(R.id.type_b);

        typeMini = (AirQualityTypeMini) findViewById(R.id.type_mini);

        typeA.setOpenAPIKey(KEY);
        typeB.setOpenAPIKey(KEY);
        typeMini.setOpenAPIKey(KEY);

        typeA.setButtonText("Hello");
    }
}
