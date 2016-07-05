package com.dronepan.AndroidApp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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
import dji.sdk.RemoteController.DJIRemoteController;
import dji.sdk.base.DJIBaseProduct;

public class MainViewController extends Activity implements View.OnClickListener, ConnectionController.ConnectionControllerInterface, PanoramaController.PanoramaControllerInterface, CameraController.CameraControllerInterface {
    private static final String TAG = MainViewController.class.getName();

    public static final String FLAG_CONNECTION_CHANGE = "dji_sdk_connection_change";

    private DJIBaseProduct product;
    private ConnectionController connectionController = null;
    private PreviewController previewController = null;
    private BatteryController batteryController = null;
    private PanoramaController panoramaController = null;
    private CameraController cameraController = null;

    private PanoramaController mPanoramaController = null;

    public static String DRONEPAN_ANDROID_VERSION = "0.1";

    public TextView mConnectStatusTextView;
    protected TextureView mVideoSurface = null;

    public TextView recordingTime;
    public Button mCaptureButton, mShootPhotoModeButton, mRecordVideoModeButton;
    public ToggleButton mRecordButton;

    private Handler mHandler;

    // ON CREATE
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // FORCE ORIENTATION TO PORTRAIT
        // @todo: RESOLVE BUG IN LANDSCAPE
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // SET CONTENT VIEW
        setContentView(R.layout.activity_dronepan);

        // LOOPER HANDLER
        mHandler = new Handler(Looper.getMainLooper());

        // CONNECTION CONTROLLER
        connectionController = new ConnectionController();
        connectionController.delegate = this;
        // STARTS CONNECTION CONTROLLER
        connectionController.start(this);

        // START UI ELEMENTS
        initUI();

        // PREVIEW CONTROLLER
        previewController = new PreviewController();
        // START WITH VIDEO SURFACE
        previewController.startWithContextSurface(this, mVideoSurface);

        // PANORAMA CONTROLLER
        panoramaController = new PanoramaController();
        panoramaController.delegate = this;

        // RECEIVE DEVICE CONNECTION CHANGES
        IntentFilter filter = new IntentFilter();
        filter.addAction(FLAG_CONNECTION_CHANGE);
        registerReceiver(mReceiver, filter);
    }

    protected BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "BORADCASST RECEIVED");

            //PanoramaController.getInstance().updateVisualDebugData();
        }

    };

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

        // INITIALIZE VIDEO CALL BACK ON RESUME
        previewController.initializeVideoCallback();

        // UPDATE TITLE BAR
        //updateTitleBar();

        super.onResume();


    }

    // ON PAUSE
    @Override public void onPause() {

        // REMOVE VIDEO CALLBACK ON PAUSE
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
        previewController.removeVideoCallback();
        unregisterReceiver(mReceiver);

        super.onDestroy();
    }



    //
    // CONNECTION CONTROLLER INTERFACE
    //
    public void sdkRegistered() {
        Log.d(TAG, "SDK REGISTERED");

    }

    public void failedToRegister(String reason) {
        Log.e(TAG, "FAILED TO REGISTER SDK : " + reason);

        //showLog(reason);
    }

    public void connectedToProduct(DJIBaseProduct product) {

        showToast("CONNECTED TO PRODUCT "+product.getModel().toString());
        setTitleBar(product.getModel().toString() + " connected!");

    }

    public void disconnected() {

    }

    public void connectedToBattery(DJIBattery battery) {

    }

    public void connectedToCamera(DJICamera camera) {
        if(camera != null) {
            cameraController = new CameraController(camera);
            cameraController.delegate = this;

            // SET PREVIEW CONTROLLER CAMERA
            previewController.setCurrentCamera(camera);
        }
        else {
            cameraController = null;
        }
    }

    public void connectedToGimbal(DJIGimbal gimbal) {

    }

    public void connectedToRemoteController(DJIRemoteController rc) {

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

    //
    //  PANORAMA CONTROLLER INTERFACE
    //
    public void postUserMessage(String message) {

    }

    public void postUserWarning(String warning) {

    }

    public void panoStarting() {

    }

    public void panoStopping() {

    }

    public void gimbalAttitudeChanged(float pitch, float yaw, float roll) {

    }

    public void aircraftYawChanged(float yaw) {

    }

    public void aircraftSattelitesChanged(int count) {

    }

    public void aircraftDistanceChanged(float lat, float lng) {

    }

    public void aircraftAltitudeChanged(float altitude) {

    }

    public void panoCountChanged(int count, int total) {

    }

    public void panoAvailable(boolean available) {

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

    private void notifyStatusChange() {
        mHandler.removeCallbacks(updateRunnable);
        mHandler.postDelayed(updateRunnable, 500);
    }

    private Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            //Intent intent = new Intent(FLAG_CONNECTION_CHANGE);
            //PanoramaController.getInstance().getMainContext().sendBroadcast(intent);
        }
    };

}
