package com.dronepan.AndroidApp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.CompoundButton;

import dji.sdk.Battery.DJIBattery;
import dji.sdk.Camera.DJICamera;
import dji.sdk.FlightController.DJIFlightController;
import dji.sdk.Gimbal.DJIGimbal;
import dji.sdk.base.DJIBaseProduct;

public class MainViewController extends Activity implements View.OnClickListener, ConnectionController.ConnectionControllerInterface {
    private static final String TAG = MainViewController.class.getName();

    private DJIBaseProduct product;
    private ConnectionController connectionController = null;
    private PreviewController previewController = null;
    private BatteryController batteryController = null;
    private PanoramaController panoramaController = null;

    private PanoramaController mPanoramaController = null;

    public static String DRONEPAN_ANDROID_VERSION = "0.1";

    public TextView mConnectStatusTextView;
    protected TextureView mVideoSurface = null;

    public TextView recordingTime;
    public Button mCaptureButton, mShootPhotoModeButton, mRecordVideoModeButton;
    public ToggleButton mRecordButton;


    // START UI ELEMENTS
    private void initUI() {
        mConnectStatusTextView = (TextView) findViewById(R.id.ConnectStatusTextView);

        // VIDEO SURFACE
        mVideoSurface = (TextureView)findViewById(R.id.video_previewer_surface);

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
        //mPanoramaController = PanoramaController.getInstance();
        // INITIALIZE APP WITH MAIN CONTEXT
        //mPanoramaController.initializeApplication(this);

        previewController = new PreviewController(this);
        previewController.startWithSurface(mVideoSurface);

        panoramaController = new PanoramaController();

        // RECEIVE DEVICE CONNECTION CHANGES
        IntentFilter filter = new IntentFilter();
        filter.addAction(DJIController.FLAG_CONNECTION_CHANGE);
        registerReceiver(mReceiver, filter);
    }

    protected BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            //PanoramaController.getInstance().updateVisualDebugData();

        }

    };


    // UPDATE TITLE BAR
    public void updateTitleBar() {

    }

    public void setTitleBar(String text) {
        if(mConnectStatusTextView == null) return;
        mConnectStatusTextView.setText(text);
    }

    // SHOW TOASTER
    public void showToast(final String msg) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(MainViewController.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ON RESUME
    @Override public void onResume() {
        super.onResume();

        // INITIALIZE VIDEO CALL BACK ON RESUME
        //mPanoramaController.initializeVideoCallback();
        previewController.initializeVideoCallback();

        // UPDATE TITLE BAR
        //updateTitleBar();


    }

    // ON PAUSE
    @Override public void onPause() {

        // REMOVE VIDEO CALLBACK ON PAUSE
        //mPanoramaController.removeVideoCallback();
        previewController.removeVideoCallback();

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
        //mPanoramaController.removeVideoCallback();

        unregisterReceiver(mReceiver);

        super.onDestroy();
    }



    //
    // CONNECTION CONTROLLER INTERFACE
    //
    public void sdkRegistered() {
        //DDLogInfo("Registered");
    }

    public void failedToRegister(String reason) {
        //DDLogWarn("Failed to register: "+reason);

        //showLog(reason);
    }

    public void connectedToProduct(DJIBaseProduct product) {

    }

    public void disconnected() {

    }

    public void connectedToBattery(DJIBattery battery) {

    }

    public void connectedToCamera(DJICamera camera) {

    }

    public void connectedToGimbal(DJIGimbal gimbal) {

    }

    public void connectedToFlightController(DJIFlightController flightController) {

    }

    public void disconnectedFromBattery() {

    }

    public void disconnectedFromCamera() {

    }

    public void disconnectedFromGimbal() {

    }

    public void disconnectedFromRemote() {

    }

    public void disconnectedFromFlightController() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_capture:{
                mPanoramaController.startPanorama();
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
