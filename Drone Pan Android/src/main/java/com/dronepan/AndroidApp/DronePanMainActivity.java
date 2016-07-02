package com.dronepan.AndroidApp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.CompoundButton;
import android.Manifest;

import dji.sdk.Products.DJIAircraft;
import dji.sdk.base.DJIBaseProduct;

public class DronePanMainActivity extends Activity implements TextureView.SurfaceTextureListener, View.OnClickListener {
    private static final String TAG = DronePanMainActivity.class.getName();
    private DJIController djiController = null;

    public static String DRONEPAN_ANDROID_VERSION = "0.1";

    public TextView mConnectStatusTextView;
    protected TextureView mVideoSurface = null;

    public TextView recordingTime;
    public Button mCaptureButton, mShootPhotoModeButton, mRecordVideoModeButton;
    public ToggleButton mRecordButton;

    // ON CREATE
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // SET CONTENT VIEW
        setContentView(R.layout.activity_dronepan);

        // START UI ELEMENTS
        initUI();

        // START DJI SDK CONTROLLER
        djiController = new DJIController(this);

        // RECEIVE DEVICE CONNECTION CHANGES
        IntentFilter filter = new IntentFilter();
        filter.addAction(DJIController.FLAG_CONNECTION_CHANGE);
        registerReceiver(mReceiver, filter);
    }

    protected BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            // UPDATE TITLE BAR
            updateTitleBar();
            // ON PRODUCT CHANGE
            onProductChange();

        }

    };


    // UPDATE TITLE BAR
    public void updateTitleBar() {
        if(mConnectStatusTextView == null) return;

        if(djiController.isAircrafConnected()) {
            mConnectStatusTextView.setText(djiController.getAircraftModel() + " connected!");
        }
        else {
            mConnectStatusTextView.setText("No aircraft connected!");
        }
    }

    // ON PRODUCT CHANGE SET VIDEO SURFACE
    protected void onProductChange() {
        initializeVideoCallback();
    }

    // INITIALIZE VIDEO CALLBACK
    public void initializeVideoCallback() {
        // IF AIRCRAFT IS CONNECTED
        if (!djiController.isAircrafConnected()) {
            Log.e(TAG, "No aircraft connected");
        } else {
            mVideoSurface.setSurfaceTextureListener(this);

            if (!djiController.getAircraftModel().equals(DJIBaseProduct.Model.UnknownAircraft)) {

                // INITIALIZE VIDEO CALLBACK ON DJI SDK
                djiController.initializeVideoCallback();

            }
        }
    }

    // REMOVE VIDEO CALLBACK
    public void removeVideoCallback() {
        // REMOVE VIDEO CALLBACK ON DJI SDK
        djiController.removeVideoCallback();
    }

    // SHOW TOASTER
    public void showToast(final String msg) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(DronePanMainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ON RESUME
    @Override public void onResume() {
        super.onResume();

        // INITIALIZE VIDEO CALL BACK ON RESUME
        initializeVideoCallback();

        // UPDATE TITLE BAR
        updateTitleBar();

        if(mVideoSurface == null) {
            Log.e(TAG, "mVideoSurface is null");
        }

    }

    // ON PAUSE
    @Override public void onPause() {

        // REMOVE VIDEO CALLBACK ON PAUSE
        removeVideoCallback();

        super.onPause();
    }

    // ON STOP
    @Override public void onStop() {
        super.onStop();
    }

    // ON RETURN
    public void onReturn(View view) {
        this.finish();
    }

    // ON DESTROY
    @Override public void onDestroy() {

        // REMOVE VIDEO CALLBACK ON DESTROY
        removeVideoCallback();

        unregisterReceiver(mReceiver);

        super.onDestroy();
    }

    //
    //  SURFACE TEXTURE EVENTS
    @Override public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {

        Log.e(TAG, "SURFACE TEXTURE AVAILABLE");

        // CREATE DJI CODEC MANAGER FOR SURFACE TEXTURE
        djiController.createCodecSurface(surface, width, height);

    }

    @Override public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {

        // DJI REMOVE CODEC MANAGER ON TEXTURE
        djiController.removeCodecSurface();

        return false;
    }

    // START UI ELEMENTS
    private void initUI() {
        mConnectStatusTextView = (TextView) findViewById(R.id.ConnectStatusTextView);

        // VIDEO SURFACE
        mVideoSurface = (TextureView)findViewById(R.id.video_previewer_surface);
        mVideoSurface.setSurfaceTextureListener(this);

        recordingTime = (TextView) findViewById(R.id.timer);
        mCaptureButton = (Button) findViewById(R.id.btn_capture);
        mRecordButton = (ToggleButton) findViewById(R.id.btn_record);
        mShootPhotoModeButton = (Button) findViewById(R.id.btn_shoot_photo_mode);
        mRecordVideoModeButton = (Button) findViewById(R.id.btn_record_video_mode);


        mCaptureButton.setOnClickListener(this);
        mRecordButton.setOnClickListener(this);
        mShootPhotoModeButton.setOnClickListener(this);
        mRecordVideoModeButton.setOnClickListener(this);

        recordingTime.setVisibility(View.INVISIBLE);

        mRecordButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_capture:{
                djiController.capturePhoto();
                break;
            }
            case R.id.btn_shoot_photo_mode:{
                break;
            }
            case R.id.btn_record_video_mode:{
                break;
            }
            default:
                break;
        }
    }

}
