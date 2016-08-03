package com.dronepan.AndroidApp;


import dji.sdk.FlightController.DJIFlightController;
import dji.sdk.FlightController.DJIFlightControllerDataType;
import dji.sdk.FlightController.DJIFlightControllerDelegate;

public class FlightController {

    private DJIFlightController flightController;

    protected double mHomeLatitude = 181;
    protected double mHomeLongitude = 181;

    private DJIFlightControllerDataType.DJIFlightControllerFlightMode flightState = null;

    public FlightController() {

    }

    public void init(DJIFlightController fc) {
        flightController = fc;

        flightController.setUpdateSystemStateCallback(
                new DJIFlightControllerDelegate.FlightControllerUpdateSystemStateCallback() {

            @Override
            public void onResult(
                    DJIFlightControllerDataType.DJIFlightControllerCurrentState state) {

                mHomeLatitude = state.getHomeLocation().getLatitude();
                mHomeLongitude = state.getHomeLocation().getLongitude();
                flightState = state.getFlightMode();

                /*Utils.setResultToText(mContext, mFCPushInfoTV,
                        "home point latitude: " + mHomeLatitude +
                        "\nhome point longitude: " + mHomeLongitude +
                        "\nFlight state: " + flightState.name());*/

            }
        });
    }
}
