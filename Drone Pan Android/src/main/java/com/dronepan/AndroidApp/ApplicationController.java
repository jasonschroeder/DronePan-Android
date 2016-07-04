package com.dronepan.AndroidApp;

import android.content.Context;
import android.graphics.SurfaceTexture;

import dji.sdk.Camera.DJICameraSettingsDef;

public class ApplicationController {
    private static ApplicationController mInstance = null;

    // CONTROLLERS
    private DJIController mDJIController = null;
    private PanoramaController mPanoramaController = null;

    // MAIN APPLICATION ACTIVITY
    private DronePanMainActivity mMainActivity = null;

    // PROTECTED CONSTRUCTOR
    protected ApplicationController() {

    }

    // SINGLETON
    public static ApplicationController getInstance() {
        if(mInstance == null) {
            mInstance = new ApplicationController();
        }
        return mInstance;
    }

    // MAIN STARTING POINT
    public void initializeApplication(DronePanMainActivity activity) {

        // SET MAIN ACTIVITY
        mMainActivity = activity;

        // GET DJI CONTROLLER
        mDJIController = DJIController.getInstance();
        // INITIALIZE DJI CONTROLLER
        mDJIController.initializeDJIController();

        ApplicationController.getInstance().showLog("Application started OK.");

    }

    // SHOW DEBUG MESSAGE
    public void showLog(String log) {
        mMainActivity.showToast(log);
    }

    // GET MAIN CONTEXT OBJECT
    public Context getMainContext() {
        return (Context)mMainActivity;
    }

    // INITIALIZE VIDEO CALLBACK
    public void initializeVideoCallback() {
        mDJIController.initializeVideoCallback();
    }

    // REMOVE VIDEO CALLBACK
    public void removeVideoCallback() {
        mDJIController.removeVideoCallback();
    }

    // CREATE CODEC MANAGER
    public void createCodecManager(SurfaceTexture surface, int width, int height) {

        //
        mDJIController.createCodecSurface(surface, width, height);

    }

    // REMOVE CODEC MANAGER
    public void removeCodecManager() {

        //
        mDJIController.removeCodecSurface();

    }

    // CAPTURE PHOTO
    public void capturePhoto() {

        // SET CAMERA MODE TO SHOOT PHOTO
        mDJIController.switchCameraMode(DJICameraSettingsDef.CameraMode.ShootPhoto);

        // CAPTURE PHOTO
        mDJIController.capturePhoto();
    }

}
