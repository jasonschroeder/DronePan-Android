package com.dronepan.AndroidApp;

public class PanoramaController {

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

    public PanoramaController() {

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
        /*DJIFlightController flightController = mDJIController.getFlightController();
        if(flightController != null) {
            DJIFlightControllerDataType.DJIFlightControllerCurrentState currentState = flightController.getCurrentState();
            DJIFlightControllerDataType.DJIFlightControllerFlightMode currentFlightMode = currentState.getFlightMode();

            if(currentFlightMode == DJIFlightControllerDataType.DJIFlightControllerFlightMode.GPSAtti){
                return true;
            }
            else {
                //showLog("WRONG FLIGHT MODE. "+currentFlightMode.toString());
            }
        }*/

        return false;
    }

    // CHECK GIMBAL
    public boolean checkGimbal() {
        /*if(mDJIController.getGimbal() != null) {
            return true;
        }*/

        //showLog("NO GIMBAL PRESENT");

        return false;
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
