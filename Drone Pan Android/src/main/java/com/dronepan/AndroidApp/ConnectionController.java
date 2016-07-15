package com.dronepan.AndroidApp;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import dji.sdk.Battery.DJIBattery;
import dji.sdk.Camera.DJICamera;
import dji.sdk.FlightController.DJIFlightController;
import dji.sdk.Gimbal.DJIGimbal;
import dji.sdk.RemoteController.DJIRemoteController;
import dji.sdk.SDKManager.DJISDKManager;
import dji.sdk.base.DJIBaseComponent;
import dji.sdk.base.DJIBaseProduct;
import dji.sdk.base.DJIError;
import dji.sdk.base.DJISDKError;

public class ConnectionController {
    private static final String TAG = ConnectionController.class.getName();

    private static DJIBaseProduct mProduct;
    protected String model = "";

    private Handler mHandler;

    private MainViewController viewController = null;

    public enum ProductType {
        Aircraft,
        Handheld,
        Unknown
    }

    // INTERFACE METHODS
    interface ConnectionControllerInterface {
        public void sdkRegistered();
        public void failedToRegister(String reason);
        public void connectedToProduct(DJIBaseProduct product);
        public void disconnected();
        public void connectedToBattery(DJIBattery battery);
        public void connectedToCamera(DJICamera camera);
        public void connectedToGimbal(DJIGimbal gimbal);
        public void connectedToRemoteController(DJIRemoteController rc);
        public void connectedToFlightController(DJIFlightController flightController);
        public void disconnectedFromBattery();
        public void disconnectedFromCamera();
        public void disconnectedFromGimbal();
        public void disconnectedFromRemote();
        public void disconnectedFromFlightController();
    }

    public ConnectionControllerInterface delegate = null;

    public void ConnectionController() {

    }

    public void start(MainViewController ctx) {
        mHandler = new Handler(Looper.getMainLooper());

        viewController = ctx;

        Log.d(TAG, "STARTING SDK MANAGER");
        // INIT DJI SDK MANAGER
        DJISDKManager.getInstance().initSDKManager(ctx, mDJISDKMangerCallback);
    }

    // DJI SDK MANAGER CALLBACK
    private DJISDKManager.DJISDKManagerCallback mDJISDKMangerCallback = new DJISDKManager.DJISDKManagerCallback() {
        @Override public void onGetRegisteredResult(DJIError error) {
            Log.d(TAG, "SDK Manger Registered Result  err: "+error.getDescription());

            if(error == DJISDKError.REGISTRATION_SUCCESS) {
                // START CONNECTION TO PRODUCT
                DJISDKManager.getInstance().startConnectionToProduct();

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "SUCCESS REGISTRATION SDK");
                        delegate.sdkRegistered();

                    }
                });
            }
            else {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("DronePan", "ERROR REGISTRATION SDK");

                    }
                });
            }
        }

        // ON PRODUCT CHANGED
        @Override public void onProductChanged(DJIBaseProduct oldProduct, DJIBaseProduct newProduct) {
            mProduct = newProduct;

            if(mProduct != null) {
                mProduct.setDJIBaseProductListener(mDJIBaseProductListener);

                // CALLS INTERFACE CALLBACK
                delegate.connectedToProduct(mProduct);
                delegate.connectedToBattery(mProduct.getBattery());
                delegate.connectedToCamera(mProduct.getCamera());
            }
        }
    };

    // PRODUCT LISTENER
    private DJIBaseProduct.DJIBaseProductListener mDJIBaseProductListener = new DJIBaseProduct.DJIBaseProductListener() {
        @Override public void onComponentChange(DJIBaseProduct.DJIComponentKey key, DJIBaseComponent oldComponent , DJIBaseComponent newComponent) {
            // SWITCH COMPONENT KEYS

            switch (key) {
                case Camera:
                    // CALLS INTERFACE CALLBACK
                        DJICamera camera = (DJICamera)newComponent;
                        delegate.connectedToCamera(camera);
                    break;
                case Gimbal:
                    // CALLS INTERFACE CALLBACK
                    DJIGimbal gimbal = (DJIGimbal)newComponent;
                    delegate.connectedToGimbal(gimbal);
                    break;
                case RemoteController:
                    // CALLS INTERFACE CALLBACK
                    DJIRemoteController rc = (DJIRemoteController)newComponent;
                    delegate.connectedToRemoteController(rc);
                    break;
                case FlightController:
                    // CALLS INTERFACE CALLBACK
                    DJIFlightController fc = (DJIFlightController)newComponent;
                    delegate.connectedToFlightController(fc);
                    break;

            }
            if(newComponent != null) {
                newComponent.setDJIComponentListener(mDJIComponentListener);
            }

            notifyStatusChange();
        }

        @Override public void onProductConnectivityChanged(boolean isConnected) {
            notifyStatusChange();
        }
    };

    // COMPONENT LISTENER
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
            Intent intent = new Intent(MainViewController.FLAG_CONNECTION_CHANGE);
            viewController.sendBroadcast(intent);
        }
    };
}
