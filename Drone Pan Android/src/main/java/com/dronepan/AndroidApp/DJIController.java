package com.dronepan.AndroidApp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.TextureView;
import android.widget.Toast;

import dji.sdk.Camera.DJICamera;
import dji.sdk.Codec.DJICodecManager;
import dji.sdk.Products.DJIAircraft;
import dji.sdk.SDKManager.DJISDKManager;
import dji.sdk.base.DJIBaseComponent;
import dji.sdk.base.DJIBaseProduct;
import dji.sdk.base.DJIError;
import dji.sdk.base.DJISDKError;

/**
 * Created by gramulho on 29/06/2016.
 */
public class DJIController {
    private static final String TAG = DJIController.class.getName();
    public static final String FLAG_CONNECTION_CHANGE = "dji_sdk_connection_change";

    // PRODUCT
    private static DJIBaseProduct mProduct;
    private Handler mHandler;
    // MAIN ACTIVITY
    private DronePanMainActivity mainCtx;

    protected DJICamera.CameraReceivedVideoDataCallback mReceivedVideoDataCallBack = null;
    protected DJICodecManager mCodecManager = null;

    public DJIController(DronePanMainActivity ctx) {
        // DJI SDK MANAGER
        mHandler = new Handler(Looper.getMainLooper());

        mainCtx = ctx;
        DJISDKManager.getInstance().initSDKManager(mainCtx, mDJISDKMangerCallback);

        // RECEIVED VIDEO DATA CALLBACK
        mReceivedVideoDataCallBack = new DJICamera.CameraReceivedVideoDataCallback() {
            @Override public void onResult(byte[] videoBuffer, int size) {
                if(mCodecManager != null) {
                    mCodecManager.sendDataToDecoder(videoBuffer, size);
                }
                else {
                    Log.e(TAG, "Codec Manager is null");
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(DJIController.FLAG_CONNECTION_CHANGE);
        mainCtx.registerReceiver(mReceiver, filter);
    }

    protected BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // UPDATE MAIN ACTIVITY TITLE BAR
            mainCtx.updateTitleBar();

            //updateTitleBar();
            //onProductChange();
        }
    };

    // GET PRODUCT INSTANCE
    public static synchronized DJIBaseProduct getProductInstance() {
        if(null == mProduct) {
            mProduct = DJISDKManager.getInstance().getDJIProduct();
        }

        return mProduct;
    }

    // GET CAMERA INSTANCE
    public static  synchronized DJICamera getCameraInstance() {
        if(getProductInstance() == null) {
            return null;
        }

        return getProductInstance().getCamera();
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
    public void setVideoSurface(TextureView textureView) {
        DJIBaseProduct product = getProductInstance();

        if(product == null || !product.isConnected()) {
            Log.e(TAG, "No aircraft connected");
        }
        else {
            if(null != textureView) {
                DJICamera camera = product.getCamera();
                if (camera != null){
                    // Set the callback
                    camera.setDJICameraReceivedVideoDataCallback(mReceivedVideoDataCallBack);
                }
            }
        }
    }

    // UNSET VIDEO SURFACE
    public void unsetVideoSurface() {
        DJICamera camera = getCameraInstance();

        if(camera != null) {
            // UNSET CAMERA VIDEO DATA CALLBACK
            camera.setDJICameraReceivedVideoDataCallback(null);
        }
    }

    // SET CODEC SURFACE TEXTURE
    public void setCodecSurface(SurfaceTexture surface, int width, int height) {
        if(mCodecManager == null) {
            mCodecManager = new DJICodecManager(mainCtx, surface, width, height);
        }
    }

    //  UNSET CODEC SURFACE TEXTURE
    public void unsetCodecSurface() {
        if(mCodecManager != null) {
            mCodecManager.cleanSurface();
            mCodecManager = null;
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
                        mainCtx.showToast("SUCCESS REGISTRATION SDK");
                    }
                });
            }
            else {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("DronePan", "ERROR REGISTRATION SDK");
                        mainCtx.showToast("ERROR REGISTERING SDK");
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

    /*protected void onProductChange() {
        //initPreviewer();
    }*/

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
            mainCtx.sendBroadcast(intent);
        }
    };
}
