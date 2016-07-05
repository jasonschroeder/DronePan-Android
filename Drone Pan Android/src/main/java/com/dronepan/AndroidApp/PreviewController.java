package com.dronepan.AndroidApp;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.Log;
import android.view.TextureView;

import dji.sdk.Camera.DJICamera;
import dji.sdk.Codec.DJICodecManager;

public class PreviewController implements TextureView.SurfaceTextureListener {
    protected TextureView mVideoSurface = null;
    private DJICodecManager mCodecManager = null;
    private DJICamera mCurrentCamera = null;

    protected DJICamera.CameraReceivedVideoDataCallback mReceivedVideoDataCallBack = null;

    public PreviewController() {
        // RECEIVED VIDEO DATA CALLBACK
        mReceivedVideoDataCallBack = new DJICamera.CameraReceivedVideoDataCallback() {
            @Override public void onResult(byte[] videoBuffer, int size) {
                if(mCodecManager != null) {
                    mCodecManager.sendDataToDecoder(videoBuffer, size);
                }
                else {
                    Log.e(TAG, "ERROR CREATING CODEC MANAGER");
                }
            }
        };
    }

    public void startWithSurface(TextureView texture) {
        mVideoSurface = texture;
        mVideoSurface.setSurfaceTextureListener(this);
    }

    //
    //  SURFACE TEXTURE EVENTS
    @Override public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        // CREATE CODEC ON SURFACE
        createCodecSurface(surface, width, height);
    }

    @Override public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        // REMOVE CODEC FROM SURFACE
        removeCodecSurface();

        return false;
    }

    // SET CURRENT PREVIEW CAMERA
    public void setCurrentCamera(DJICamera camera) {
        mCurrentCamera = camera;
    }

    // SET VIDEO SURFACE
    public void initializeVideoCallback() {
        if (mCurrentCamera != null){
            // SET DJI CAMERA VIDEO DATA CALLBACK
            mCurrentCamera.setDJICameraReceivedVideoDataCallback(mReceivedVideoDataCallBack);
        }
    }

    // UNSET VIDEO SURFACE
    public void removeVideoCallback() {
        if(mCurrentCamera != null) {
            // UNSET CAMERA VIDEO DATA CALLBACK
            mCurrentCamera.setDJICameraReceivedVideoDataCallback(null);
        }
    }

    // SET CODEC SURFACE TEXTURE
    private void createCodecSurface(SurfaceTexture surface, int width, int height) {
        if(mCodecManager == null) {
            Context ctx = PanoramaController.getInstance().getMainContext();
            mCodecManager = new DJICodecManager(ctx, surface, width, height);
        }
    }

    //  UNSET CODEC SURFACE TEXTURE
    private void removeCodecSurface() {
        if(mCodecManager != null) {
            mCodecManager.cleanSurface();
            mCodecManager = null;
        }
    }
}
