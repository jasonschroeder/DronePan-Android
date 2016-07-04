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

    private ApplicationController mApplicationController = null;

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

        // FORCE ORIENTATION TO PORTRAIT
        // @todo: RESOLVE BUG IN LANDSCAPE
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // SET CONTENT VIEW
        setContentView(R.layout.activity_dronepan);

        // START UI ELEMENTS
        initUI();

        // GET APPLICATION CONTROLLER
        mApplicationController = ApplicationController.getInstance();
        // INITIALIZE APP WITH MAIN CONTEXT
        mApplicationController.initializeApplication(this);

        // RECEIVE DEVICE CONNECTION CHANGES
        IntentFilter filter = new IntentFilter();
        filter.addAction(DJIController.FLAG_CONNECTION_CHANGE);
        registerReceiver(mReceiver, filter);
    }

    protected BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            ApplicationController.getInstance().showLog("INTENT RECIEVED PRODUCT CHANGE");
            // UPDATE TITLE BAR
            //updateTitleBar();
            // ON PRODUCT CHANGE
            //onProductChange();

        }

    };


    // UPDATE TITLE BAR
    public void updateTitleBar() {
        /*if(mConnectStatusTextView == null) return;

        if(djiController.isAircrafConnected()) {
            mConnectStatusTextView.setText(djiController.getAircraftModel() + " connected!");
        }
        else {
            mConnectStatusTextView.setText("No aircraft connected!");
        }*/
    }

    public void setTitleBar(String text) {
        mConnectStatusTextView.setText(text);
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
        mApplicationController.initializeVideoCallback();

        // UPDATE TITLE BAR
        //updateTitleBar();


    }

    // ON PAUSE
    @Override public void onPause() {

        // REMOVE VIDEO CALLBACK ON PAUSE
        mApplicationController.removeVideoCallback();

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
        mApplicationController.removeVideoCallback();

        unregisterReceiver(mReceiver);

        super.onDestroy();
    }

    //
    //  SURFACE TEXTURE EVENTS
    @Override public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {

        //
        mApplicationController.createCodecManager(surface, width, height);

    }

    @Override public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {

        // DJI REMOVE CODEC MANAGER ON TEXTURE
        mApplicationController.removeCodecManager();

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
                mApplicationController.capturePhoto();
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
