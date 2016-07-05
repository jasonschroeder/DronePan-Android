package com.dronepan.AndroidApp;

import android.content.Context;
import android.graphics.SurfaceTexture;

import dji.sdk.Camera.DJICameraSettingsDef;
import dji.sdk.FlightController.DJIFlightController;
import dji.sdk.FlightController.DJIFlightControllerDataType;

public class PanoramaController {
    private static PanoramaController mInstance = null;

    // CONTROLLERS
    private DJIController mDJIController = null;

    // MAIN APPLICATION ACTIVITY
    private MainViewController mMainActivity = null;

    interface PanoramaControllerInterface {
        public void postUserMessage(String message);
        public void postUserWarning(String warning);
        public void panoStarting();
        public void panoStopping();
        public void gimbalAttitudeChanged(float pitch, float yaw, float roll);
        public void aircraftYawChanged(float yaw);
        public void aircraftSattelitesChanged(int count);
        public void aircraftDistanceChanged(float lat, float lng);
        public void aircraftAltitudeChanged(float altitude);
        public void panoCountChanged(int count, int total);
        public void panoAvailable(boolean available);

    }

    public PanoramaControllerInterface delegate = null;

    // PROTECTED CONSTRUCTOR
    protected PanoramaController() {

    }

    // SINGLETON
    public static PanoramaController getInstance() {
        if(mInstance == null) {
            mInstance = new PanoramaController();
        }
        return mInstance;
    }

    // MAIN STARTING POINT
    public void initializeApplication(MainViewController activity) {

        // SET MAIN ACTIVITY
        mMainActivity = activity;

        // GET DJI CONTROLLER
        mDJIController = DJIController.getInstance();
        // INITIALIZE DJI CONTROLLER
        mDJIController.initializeDJIController();

        PanoramaController.getInstance().showLog("Application started OK.");

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

    // START PANORAMA
    public void startPanorama() {

        // CHECK RC MODE
        if(!checkRCMode()) {
            return;
        }

        // CHECK GIMBAL
        if(!checkGimbal()) {
            return;
        }

    }

    // CHECK RC MODE
    public boolean checkRCMode() {
        DJIFlightController flightController = mDJIController.getFlightController();
        if(flightController != null) {
            DJIFlightControllerDataType.DJIFlightControllerCurrentState currentState = flightController.getCurrentState();
            DJIFlightControllerDataType.DJIFlightControllerFlightMode currentFlightMode = currentState.getFlightMode();

            if(currentFlightMode == DJIFlightControllerDataType.DJIFlightControllerFlightMode.GPSAtti){
                return true;
            }
            else {
                showLog("WRONG FLIGHT MODE. "+currentFlightMode.toString());
            }
        }

        return false;
    }

    // CHECK GIMBAL
    public boolean checkGimbal() {
        if(mDJIController.getGimbal() != null) {
            return true;
        }

        showLog("NO GIMBAL PRESENT");

        return false;
    }

    // CAPTURE PHOTO
    public void capturePhoto() {

        // SET CAMERA MODE TO SHOOT PHOTO
        mDJIController.switchCameraMode(DJICameraSettingsDef.CameraMode.ShootPhoto);

        // CAPTURE PHOTO
        mDJIController.capturePhoto();

    }

    // UPDATE VISUAL DEBUG DATA
    public void updateVisualDebugData() {

        if(mDJIController.isAircrafConnected()) {
            mMainActivity.setTitleBar(mDJIController.getAircraftModel() + " connected!");
        }
        else {
            mMainActivity.setTitleBar("No aircraft connected!");
        }

    }

}
