package com.dronepan.AndroidApp;


import dji.sdk.Battery.DJIBattery;

public class BatteryController {

    interface BatteryControllerInterface {
        void batteryControllerPercentUpdated(int batteryPercent);
    }

    public BatteryControllerInterface delegate = null;

    public BatteryController() {

    }

    void init(DJIBattery battery) {
        try {
            battery.setBatteryStateUpdateCallback(
                new DJIBattery.DJIBatteryStateUpdateCallback() {
                    @Override
                    public void onResult(DJIBattery.DJIBatteryState djiBatteryState) {
                        delegate.batteryControllerPercentUpdated(djiBatteryState.getBatteryEnergyRemainingPercent());
                    }
                }
            );
        } catch (Exception exception) {

        }
    }

}
