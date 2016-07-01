package com.dronepan.AndroidApp;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
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

import org.w3c.dom.Text;

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

        // REQUEST PERMISSIONS FOR SDK SANITY
        /*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            AppCompatActivity.requestPermission(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.VIBRATE,
                            Manifest.permission.INTERNET, Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.WAKE_LOCK, Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
                            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.SYSTEM_ALERT_WINDOW,
                            Manifest.permission.READ_PHONE_STATE,
                    }
                    , 1);
        }*/

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // SET CONTENT VIEW
        setContentView(R.layout.activity_dronepan);

        // START UI ELEMENTS
        initUI();

        // START DJI SDK CONTROLLER
        djiController = new DJIController(this);

        showToast("DJI INITIALIZED");

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
        setVideoSurface();
    }

    // SET VIDEO SURFACE
    public void setVideoSurface() {
        if (!djiController.isAircrafConnected()) {
            Log.e(TAG, "No aircraft connected");
        } else {
            if (!djiController.getAircraftModel().equals(DJIBaseProduct.Model.UnknownAircraft)) {
                djiController.setVideoSurface(mVideoSurface);
            }
        }
    }

    // UNSET VIDEO SURFACE
    public void unsetVideoSurface() {
        djiController.unsetVideoSurface();
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

        // SET VIDEO SURFACE
        setVideoSurface();

        // UPDATE TITLE BAR
        updateTitleBar();

        if(mVideoSurface == null) {
            Log.e(TAG, "mVideoSurface is null");
        }

    }

    // ON PAUSE
    @Override public void onPause() {
        // UNSET VIDEO SURFACE
        unsetVideoSurface();

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
        // UNSET VIDEO SURFACE
        unsetVideoSurface();

        unregisterReceiver(mReceiver);

        super.onDestroy();
    }

    @Override public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        // SET CODEC SURFACE
        djiController.setCodecSurface(surface, width, height);
    }

    @Override public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        // DJI UNSET CODEC SURFACE
        djiController.unsetCodecSurface();

        return false;
    }

    // START UI ELEMENTS
    private void initUI() {
        mConnectStatusTextView = (TextView) findViewById(R.id.ConnectStatusTextView);
        // init mVideoSurface
        mVideoSurface = (TextureView)findViewById(R.id.video_previewer_surface);

        recordingTime = (TextView) findViewById(R.id.timer);
        mCaptureButton = (Button) findViewById(R.id.btn_capture);
        mRecordButton = (ToggleButton) findViewById(R.id.btn_record);
        mShootPhotoModeButton = (Button) findViewById(R.id.btn_shoot_photo_mode);
        mRecordVideoModeButton = (Button) findViewById(R.id.btn_record_video_mode);

        mVideoSurface.setSurfaceTextureListener(this);

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
