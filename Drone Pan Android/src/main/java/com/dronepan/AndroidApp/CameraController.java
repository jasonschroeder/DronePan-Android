package com.dronepan.AndroidApp;

import dji.sdk.Camera.DJICamera;

public class CameraController {
    private DJICamera mCamera;

    interface CameraControllerInterface {

    }

    public CameraControllerInterface delegate = null;

    public CameraController(DJICamera cam) {
        mCamera = cam;

    }

    public DJICamera getCurrentCamera() {
        return mCamera;
    }

    public void setPhotoMode() {

    }

}
