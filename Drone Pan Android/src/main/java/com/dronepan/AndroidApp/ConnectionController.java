package com.dronepan.AndroidApp;


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

    public void start() {
        // INIT DJI SDK MANAGER
        DJISDKManager.getInstance().initSDKManager(PanoramaController.getInstance().getMainContext(), mDJISDKMangerCallback);
    }

    // DJI SDK MANAGER CALLBACK
    private DJISDKManager.DJISDKManagerCallback mDJISDKMangerCallback = new DJISDKManager.DJISDKManagerCallback() {
        @Override public void onGetRegisteredResult(DJIError error) {


            if(error == DJISDKError.REGISTRATION_SUCCESS) {
                // START CONNECTION TO PRODUCT
                DJISDKManager.getInstance().startConnectionToProduct();

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "SUCCESS REGISTRATION SDK");
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

                // CALLS INTERFACE CALLBACK
                if(interfaceCallback != null) {
                    interfaceCallback.connectedToProduct(mProduct);
                }

            }
        }
    };

    // PRODUCT LISTENER
    private DJIBaseProduct.DJIBaseProductListener mDJIBaseProductListener = new DJIBaseProduct.DJIBaseProductListener() {
        @Override public void onComponentChange(DJIBaseProduct.DJIComponentKey key, DJIBaseComponent oldComponent , DJIBaseComponent newComponent) {
            // SWITCH COMPONENT KEYS

            switch (key) {
                case Battery:
                    // CALLS INTERFACE CALLBACK
                    if(interfaceCallback != null) {
                        DJIBattery battery = (DJIBattery)newComponent;
                        interfaceCallback.connectedToBattery(battery);
                    }
                    break;
                case Camera:
                    // CALLS INTERFACE CALLBACK
                    if(interfaceCallback != null) {
                        DJICamera camera = (DJICamera)newComponent;
                        interfaceCallback.connectedToCamera(camera);
                    }
                    break;
                case Gimbal:
                    // CALLS INTERFACE CALLBACK
                    if(interfaceCallback != null) {
                        DJIGimbal gimbal = (DJIGimbal)newComponent;
                        interfaceCallback.connectedToGimbal(gimbal);
                    }
                    break;
                case RemoteController:
                    // CALLS INTERFACE CALLBACK
                    if(interfaceCallback != null) {
                        DJIRemoteController rc = (DJIRemoteController)newComponent;
                        interfaceCallback.connectedToRemoteController(rc);
                    }
                    break;
                case FlightController:
                    // CALLS INTERFACE CALLBACK
                    if(interfaceCallback != null) {
                        DJIFlightController fc = (DJIFlightController)newComponent;
                        interfaceCallback.connectedToFlightController(fc);
                    }
                    break;

            }
            if(newComponent != null) {
                newComponent.setDJIComponentListener(mDJIComponentListener);
            }
            //notifyStatusChange();
        }

        @Override public void onProductConnectivityChanged(boolean isConnected) {
            //notifyStatusChange();
        }
    };

    // COMPONENT LISTENER
    private DJIBaseComponent.DJIComponentListener mDJIComponentListener = new DJIBaseComponent.DJIComponentListener() {
        @Override public void onComponentConnectivityChanged(boolean isConnected) {
            //notifyStatusChange();
        }
    };
}
