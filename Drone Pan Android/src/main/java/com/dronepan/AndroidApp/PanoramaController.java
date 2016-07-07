package com.dronepan.AndroidApp;

import android.util.Log;

import java.util.LinkedList;

import dji.sdk.Gimbal.DJIGimbal;
import dji.sdk.MissionManager.DJICustomMission;
import dji.sdk.MissionManager.DJIMission;
import dji.sdk.MissionManager.DJIMissionManager;
import dji.sdk.MissionManager.MissionStep.DJIGimbalAttitudeStep;
import dji.sdk.MissionManager.MissionStep.DJIGoToStep;
import dji.sdk.MissionManager.MissionStep.DJIMissionStep;
import dji.sdk.MissionManager.MissionStep.DJITakeoffStep;
import dji.sdk.base.DJIBaseComponent;
import dji.sdk.base.DJIError;

public class PanoramaController {
    private static final String TAG = PanoramaController.class.getName();

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

    private MainViewController mContext = null;

    private CameraController mCameraController;
    private DJIMissionManager mDJIMissionManager;
    private DJIMission mDJIMission;

    private float lastGimbalPtich = 0.0f;
    private float lastGimbalYaw = 0.0f;
    private float lastGimbalRoll = 0.0f;
    private float lastACYaw = 0.0f;

    private boolean isRunningState = false;
    private boolean isRunningOK = false;

    public PanoramaController(MainViewController ctx) {
        mContext = ctx;
    }

    // START PANORAMA
    public void start() {

        if (!checkProduct()) {
            return;
        }

        if (!checkCamera()) {
            return;
        }

        if (!checkSpace()) {
            return;
        }

        if (!checkGimbal()) {
            return;
        }

        if (!checkFC()) {
            return;
        }

        if (!checkRemote()) {
            return;
        }

        if (!checkRCMode()) {
            return;
        }

        isRunningState = true;
        isRunningOK = true;

        // PANORAMA STARTING
        doPanoLoop();

    }

    // DO PANO LOOP
    protected void doPanoLoop() {
        int currentCount = 0;

        delegate.postUserMessage("doPanoLoop");

        DJIMissionManager missionManager = DJIMissionManager.getInstance();

        if (missionManager == null) {
            delegate.postUserMessage("Error: Could not get mission manager instance");
            return;
        }

        mDJIMission = createCustomMission();
        if (mDJIMission == null) {
            delegate.postUserMessage("Please choose a mission type");
            //Utils.setResultToToast(mContext, "Please choose a mission type...");
        }
        missionManager.prepareMission(mDJIMission, new DJIMission.DJIMissionProgressHandler() {

            @Override
            public void onProgress(DJIMission.DJIProgressType type, float progress) {
                //setProgressBar((int)(progress * 100f));
            }

        }, new DJIBaseComponent.DJICompletionCallback() {


            @Override
            public void onResult(DJIError error) {
                if (error == null) {
                    //Utils.setResultToToast(mContext, "Success!");

                    delegate.postUserMessage("Success preparing mission");

                    startMission();
                } else {
                    delegate.postUserMessage("Error preparing mission " + error.getDescription());
                }
            }
        });
    }

    protected void startMission() {
        delegate.postUserMessage("Starting mission");

        DJIMissionManager missionManager = DJIMissionManager.getInstance();

        if (mDJIMission != null) {
            missionManager.setMissionExecutionFinishedCallback(new DJIBaseComponent.DJICompletionCallback() {

                @Override
                public void onResult(DJIError error) {

                    if(error == null) {
                        delegate.postUserMessage("Mission executing OK");
                    }

                }
            });

            missionManager.startMissionExecution(new DJIBaseComponent.DJICompletionCallback() {

                @Override
                public void onResult(DJIError mError) {

                    if (mError == null) {
                        delegate.postUserMessage("Mission execution sucess");
                    } else {
                        delegate.postUserMessage("Error mission execution: " + mError.getDescription());
                    }
                }
            });
        }
    }

    protected DJICustomMission createCustomMission() {
        LinkedList<DJIMissionStep> steps = new LinkedList<DJIMissionStep>();

        //Step 1: takeoff from the ground
        /*steps.add(new DJITakeoffStep(new DJIBaseComponent.DJICompletionCallback() {

            @Override
            public void onResult(DJIError error) {
                //Utils.setResultToToast(mContext, "Takeoff step: " + (error == null ? "Success" : error.getDescription()));
            }
        }));*/

        //Step 2: reset the gimbal to horizontal angle
        steps.add(new DJIGimbalAttitudeStep(
                DJIGimbal.DJIGimbalRotateAngleMode.RelativeAngle,
                new DJIGimbal.DJIGimbalAngleRotation(true, 25f, DJIGimbal.DJIGimbalRotateDirection.CounterClockwise),
                null,
                null,
                new DJIBaseComponent.DJICompletionCallback() {
                    @Override
                    public void onResult(DJIError error) {
                        delegate.postUserMessage("Set gimbal attitude step: " + DJIGimbal.DJIGimbalRotateDirection.Clockwise + " " + (error == null ? "Success" : error.getDescription()));
                    }

                }));

        //Step 3: Go 10 meters from home point
        /*steps.add(new DJIGoToStep(mHomeLatitude, mHomeLongitude, 10, new DJIBaseComponent.DJICompletionCallback() {

            @Override
            public void onResult(DJIError error) {
                //setResultToToast(mContext, "Goto step: " + (error == null ? "Success" : error.getDescription()));
            }
        }));*/

        DJICustomMission customMission = new DJICustomMission(steps);

        return customMission;
    }

    /*public void buildAttitudeStep(float pitch, float roll, float yaw) {
        DJIGimbalAttitudeStep attitude = DJIGimbalAttitudeStep(pitch, roll yaw);
        attitude.pitch = pitch;
        attitude.roll = roll;
        attitude.yaw = yaw;

        // TODO - if this fails to create (returns optional nil) then the ! will cause an app crash - needs handling
        return DJIGimbalAttitudeStep(attitude: attitude)!
    }*/

    public void buildMissionSteps() {

    }

    public boolean checkProduct() {

        return true;
    }

    public boolean checkCamera() {

        return true;
    }

    public boolean checkSpace() {

        return true;
    }

    public boolean checkGimbal() {

        return true;
    }

    public boolean checkFC() {

        return true;
    }

    public boolean checkRemote() {

        return true;
    }

    public boolean checkRCMode() {

        return true;
    }


    // CAPTURE PHOTO
    public void capturePhoto() {

        // SET CAMERA MODE TO SHOOT PHOTO
        //mDJIController.switchCameraMode(DJICameraSettingsDef.CameraMode.ShootPhoto);

        // CAPTURE PHOTO
        //mDJIController.capturePhoto();

    }

    // UPDATE VISUAL DEBUG DATA
    public void updateVisualDebugData() {

        /*if(mDJIController.isAircrafConnected()) {
            mMainActivity.setTitleBar(mDJIController.getAircraftModel() + " connected!");
        }
        else {
            mMainActivity.setTitleBar("No aircraft connected!");
        }*/

    }

}
