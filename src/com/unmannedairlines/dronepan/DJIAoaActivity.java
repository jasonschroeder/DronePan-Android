package com.unmannedairlines.dronepan;

import android.app.Activity;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.view.View;
import dji.log.DJILogHelper;
import dji.midware.data.manager.P3.ServiceManager;
import dji.midware.usb.P3.DJIUsbAccessoryReceiver;
import dji.midware.usb.P3.UsbAccessoryService;

public class DJIAoaActivity extends Activity {
    private static boolean isStarted = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new View(this));
        
        if (isStarted) {
            //finish();
        }else {
            
            isStarted = true;
            ServiceManager.getInstance();
            UsbAccessoryService.registerAoaReceiver(this);
            // bypass drone selection for now
            //Intent intent = new Intent(DJIAoaActivity.this, SelectDroneTypeActivity.class);
            Intent intent = new Intent(DJIAoaActivity.this, MainActivity.class);
            intent.putExtra("DroneType", 1);
            startActivity(intent);

            //finish();
        }
        
        Intent aoaIntent = getIntent();
        if (aoaIntent!=null) {
            String action = aoaIntent.getAction();
            DJILogHelper.getInstance().LOGE("", "action="+action, false, true);
            if (action==UsbManager.ACTION_USB_ACCESSORY_ATTACHED ||
                    action==Intent.ACTION_MAIN) {
                Intent attachedIntent=new Intent();
                attachedIntent.setAction(DJIUsbAccessoryReceiver.ACTION_USB_ACCESSORY_ATTACHED);  
                sendBroadcast(attachedIntent);
//                DJILogHelper.getInstance().LOGE("", "action=send", false, true);
            }
        }
        
        finish();
    }
}
