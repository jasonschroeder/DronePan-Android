package com.dronepan.AndroidApp;

import dji.sdk.Camera.DJICamera;
import dji.sdk.Camera.DJIPlaybackManager;

public class CameraController {
    private DJICamera mCamera;
    private DJIPlaybackManager.DJICameraPlaybackState mState;

    interface CameraControllerInterface {
        public void cameraControllerInError(String reason);
        public void cameraControllerOK(boolean fromError);
        public void cameraControllerNewMedia(String filename);
    }

    public CameraControllerInterface delegate = null;

    public CameraController() {

    }

    public void init(DJICamera cam) {
        mCamera = cam;
    }

    public DJICamera getCurrentCamera() {
        return mCamera;
    }

    public void setPhotoMode() {

    }

}
