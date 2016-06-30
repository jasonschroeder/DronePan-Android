package com.dronepan.AndroidApp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class DronePanMainActivity extends AppCompatActivity {
    private DJIController djiController = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dronepan);

        // START DJI SDK CONTROLLER
        djiController = new DJIController(this);
    }


}
