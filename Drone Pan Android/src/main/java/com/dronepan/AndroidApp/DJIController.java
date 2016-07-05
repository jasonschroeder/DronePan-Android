package com.dronepan.AndroidApp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import dji.sdk.Camera.DJICamera;
import dji.sdk.Camera.DJICameraSettingsDef;
import dji.sdk.Codec.DJICodecManager;
import dji.sdk.FlightController.DJIFlightController;
import dji.sdk.Gimbal.DJIGimbal;
import dji.sdk.MissionManager.DJIMissionManager;
import dji.sdk.Products.DJIAircraft;
import dji.sdk.RemoteController.DJIRemoteController;
import dji.sdk.SDKManager.DJISDKManager;
import dji.sdk.base.DJIBaseComponent;
import dji.sdk.base.DJIBaseProduct;
import dji.sdk.base.DJIError;
import dji.sdk.base.DJISDKError;

public class DJIController {
    private static DJIController mInstance = null;

    private static final String TAG = DJIController.class.getName();
    public static final String FLAG_CONNECTION_CHANGE = "dji_sdk_connection_change";

    // PRODUCT
    private static DJIBaseProduct mProduct;

    protected DJICamera.CameraReceivedVideoDataCallback mReceivedVideoDataCallBack = null;
    protected DJICodecManager mCodecManager = null;
    protected DJIMissionManager mMissionManager = null;
    protected DJIFlightController mFlightController = null;
    protected DJIRemoteController mRemoteController = null;
    protected DJIGimbal mGimbal = null;

    private Handler mHandler;

    // PROTECTED CONSTRUCTOR
    protected DJIController() {

    }

    // SINGLETON
    public static DJIController getInstance() {
        if(mInstance == null) {
            mInstance = new DJIController();
        }
        return mInstance;
    }

    public void initializeDJIController() {

        // LOOPER HANDLER
        mHandler = new Handler(Looper.getMainLooper());

        // INIT DJI SDK MANAGER
        DJISDKManager.getInstance().initSDKManager(PanoramaController.getInstance().getMainContext(), mDJISDKMangerCallback);

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

        /*IntentFilter filter = new IntentFilter();
        filter.addAction(DJIController.FLAG_CONNECTION_CHANGE);
        mainCtx.registerReceiver(mReceiver, filter);*/
    }

    protected BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            PanoramaController.getInstance().updateVisualDebugData();

            // UPDATE MAIN ACTIVITY TITLE BAR

            //mainCtx.initializeVideoCallback();

        }
    };

    // GET PRODUCT INSTANCE
    public synchronized DJIBaseProduct getProductInstance() {
        if(null == mProduct) {
            mProduct = DJISDKManager.getInstance().getDJIProduct();
        }

        return mProduct;
    }

    // GET CAMERA INSTANCE
    public  synchronized DJICamera getCameraInstance() {
        if(getProductInstance() == null) {
            return null;
        }

        return getProductInstance().getCamera();
    }

    // GET FLIGHT CONTROLLER
    public synchronized DJIFlightController getFlightController() {
        if (mProduct != null && mProduct.isConnected()) {
            if (mProduct instanceof DJIAircraft) {
                mFlightController = ((DJIAircraft) mProduct).getFlightController();
            }
        }

        return mFlightController;
    }

    // GET REMOTE CONTROLLER
    public synchronized DJIRemoteController getRemoteController() {
        if (mProduct instanceof DJIAircraft) {
            mRemoteController = ((DJIAircraft) mProduct).getRemoteController();
        }

        return mRemoteController;
    }

    // GET GIMBAL
    public synchronized DJIGimbal getGimbal() {
        if (mGimbal == null && mProduct instanceof DJIAircraft) {
            mGimbal =  ((DJIAircraft) mProduct).getGimbal();
        }

        return mGimbal;
    }

    // IS AIRCRAFT CONNECTED
    public boolean isAircrafConnected() {
        DJIBaseProduct product = getProductInstance();

        if(product != null) {
            // IF PRODUCT IS CONNECTED
            if(product.isConnected()) {
               return true;
            }
        }

        return false;
    }

    // GET AIRCRAFT MODEL
    public String getAircraftModel() {
        DJIBaseProduct product = getProductInstance();

        if(product != null) {
            // IF PRODUCT IS CONNECTED
            if(product.isConnected()) {
                return getProductInstance().getModel()+"";
            }
        }

        // RETURN EMPTY STRING
        return "";
    }


    // SET VIDEO SURFACE
    public void initializeVideoCallback() {
        DJIBaseProduct product = getProductInstance();

        if(product == null || !product.isConnected()) {
            Log.e(TAG, "NO AIRCRAFT CONNECTED");
        }
        else {
            DJICamera camera = product.getCamera();
            if (camera != null){
                // SET DJI CAMERA VIDEO DATA CALLBACK
                camera.setDJICameraReceivedVideoDataCallback(mReceivedVideoDataCallBack);
            }
        }
    }

    // UNSET VIDEO SURFACE
    public void removeVideoCallback() {
        DJICamera camera = getCameraInstance();

        if(camera != null) {
            // UNSET CAMERA VIDEO DATA CALLBACK
            getCameraInstance().setDJICameraReceivedVideoDataCallback(null);
        }
    }

    // SET CODEC SURFACE TEXTURE
    public void createCodecSurface(SurfaceTexture surface, int width, int height) {
        if(mCodecManager == null) {
            Context ctx = PanoramaController.getInstance().getMainContext();
            mCodecManager = new DJICodecManager(ctx, surface, width, height);
        }
    }

    //  UNSET CODEC SURFACE TEXTURE
    public void removeCodecSurface() {
        if(mCodecManager != null) {
            mCodecManager.cleanSurface();
            mCodecManager = null;
        }
    }

    // CAPTURE PHOTO
    public void capturePhoto() {
        DJICameraSettingsDef.CameraMode cameraMode = DJICameraSettingsDef.CameraMode.ShootPhoto;

        final DJICamera camera = getCameraInstance();

        if(camera != null) {
            DJICameraSettingsDef.CameraShootPhotoMode photoMode = DJICameraSettingsDef.CameraShootPhotoMode.Single;

            camera.startShootPhoto(photoMode, new DJIBaseComponent.DJICompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if(djiError == null) {
                        PanoramaController.getInstance().showLog("CAPTURED PHOTO");
                    }
                    else {
                        PanoramaController.getInstance().showLog(djiError.getDescription());
                    }
                }
            });
        }
    }

    // RESET GIMBAL
    public void resetGimbal() {
        //getProductInstance().getGimbal().resetGimbal();
    }

    // SWITCH CAMERA MODE
    public void switchCameraMode(DJICameraSettingsDef.CameraMode cameraMode){

        DJICamera camera = getCameraInstance();
        if (camera != null) {
            camera.setCameraMode(cameraMode, new DJIBaseComponent.DJICompletionCallback() {
                @Override
                public void onResult(DJIError error) {

                    if (error == null) {
                        //PanoramaController.getInstance().showLog("Switch camera mode success!");
                    } else {
                        //PanoramaController.getInstance().showLog(error.getDescription());
                    }
                }
            });
        }

    }

    private DJISDKManager.DJISDKManagerCallback mDJISDKMangerCallback = new DJISDKManager.DJISDKManagerCallback() {
        @Override public void onGetRegisteredResult(DJIError error) {
            // LOG ERROR
            Log.d(TAG, error == null ? "Success" : error.getDescription());

            if(error == DJISDKError.REGISTRATION_SUCCESS) {
                // START CONNECTION TO PRODUCT
                DJISDKManager.getInstance().startConnectionToProduct();

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("DronePan", "SUCCESS REGISTRATION SDK");
                        PanoramaController.getInstance().showLog("SUCCESS REGISTRATION SDK");
                    }
                });
            }
            else {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("DronePan", "ERROR REGISTRATION SDK");
                        PanoramaController.getInstance().showLog("ERROR REGISTERING SDK");
                    }
                });
            }
        }

        // ON PRODUCT CHANGED
        @Override public void onProductChanged(DJIBaseProduct oldProduct, DJIBaseProduct newProduct) {
            mProduct = newProduct;

            if(mProduct != null) {
                mProduct.setDJIBaseProductListener(mDJIBaseProductListener);
            }
        }
    };

    private DJIBaseProduct.DJIBaseProductListener mDJIBaseProductListener = new DJIBaseProduct.DJIBaseProductListener() {
        @Override public void onComponentChange(DJIBaseProduct.DJIComponentKey key, DJIBaseComponent oldComponent , DJIBaseComponent newComponent) {
            if(newComponent != null) {
                newComponent.setDJIComponentListener(mDJIComponentListener);
            }
            notifyStatusChange();
        }

        @Override public void onProductConnectivityChanged(boolean isConnected) {
            notifyStatusChange();
        }
    };

    private DJIBaseComponent.DJIComponentListener mDJIComponentListener = new DJIBaseComponent.DJIComponentListener() {
        @Override public void onComponentConnectivityChanged(boolean isConnected) {
            notifyStatusChange();
        }
    };

    private void notifyStatusChange() {
        mHandler.removeCallbacks(updateRunnable);
        mHandler.postDelayed(updateRunnable, 500);
    }

    private Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(FLAG_CONNECTION_CHANGE);
            PanoramaController.getInstance().getMainContext().sendBroadcast(intent);
        }
    };
}
