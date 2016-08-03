package com.dronepan.AndroidApp;

import dji.sdk.Camera.DJICamera;
import dji.sdk.Camera.DJICameraSettingsDef;
import dji.sdk.Camera.DJIPlaybackManager;
import dji.sdk.base.DJIBaseComponent;
import dji.sdk.base.DJIError;

public class CameraController {
    private DJICamera mCamera;
    private DJIPlaybackManager.DJICameraPlaybackState mState;

    private boolean mAEBCaptureMode = false;

    interface CameraControllerInterface {
        void cameraTakePictureSuccess();
        void cameraModeSwapAEB(boolean aebCapture);
        void cameraControllerInError(String reason);
        void cameraControllerOK(boolean fromError);
        void cameraControllerNewMedia(String filename);
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

    public void swapEABMode() {
        if (mAEBCaptureMode) {
            mAEBCaptureMode = false;
        } else {
            mAEBCaptureMode = true;
        }

        delegate.cameraModeSwapAEB(mAEBCaptureMode);
    }

    public void takePicture() {
        if (mCamera != null) {
            DJICameraSettingsDef.CameraShootPhotoMode shootMode =
                    DJICameraSettingsDef.CameraShootPhotoMode.Single;
            if (mAEBCaptureMode) {
                shootMode = DJICameraSettingsDef.CameraShootPhotoMode.AEBCapture;
            }
            mCamera.startShootPhoto(
                    shootMode,
                    new DJIBaseComponent.DJICompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            if (null == djiError) {
                                delegate.cameraTakePictureSuccess();
                            }
                        }
                    }
            ); // Execute the startShootPhoto API`
        }


    }

}
