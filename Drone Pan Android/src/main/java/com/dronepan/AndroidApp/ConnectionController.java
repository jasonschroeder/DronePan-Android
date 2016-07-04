package com.dronepan.AndroidApp;


import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import dji.sdk.Battery.DJIBattery;
import dji.sdk.Camera.DJICamera;
import dji.sdk.FlightController.DJIFlightController;
import dji.sdk.Gimbal.DJIGimbal;
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
        public void connectedToFlightController(DJIFlightController flightController);
        public void disconnectedFromBattery();
        public void disconnectedFromCamera();
        public void disconnectedFromGimbal();
        public void disconnectedFromRemote();
        public void disconnectedFromFlightController();
    }

    public void ConnectionController() {
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
            }
        }
    };

    // PRODUCT LISTENER
    private DJIBaseProduct.DJIBaseProductListener mDJIBaseProductListener = new DJIBaseProduct.DJIBaseProductListener() {
        @Override public void onComponentChange(DJIBaseProduct.DJIComponentKey key, DJIBaseComponent oldComponent , DJIBaseComponent newComponent) {
            // SWITCH COMPONENT KEYS
            switch (key) {
                case Battery:

                    break;
                case Camera:

                    break;
                case Gimbal:

                    break;
                case RemoteController:

                    break;
                case FlightController:

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
