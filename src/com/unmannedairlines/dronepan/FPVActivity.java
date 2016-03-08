package com.unmannedairlines.dronepan;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


import dji.sdk.api.DJIDrone;
import dji.sdk.api.DJIDroneTypeDef.DJIDroneType;
import dji.sdk.api.DJIError;
import dji.sdk.api.Camera.DJICameraSettingsTypeDef.*;
import dji.sdk.api.Gimbal.DJIGimbalRotation;
import dji.sdk.api.MainController.DJIMainControllerSystemState;
import dji.sdk.interfaces.DJIExecuteResultCallback;
import dji.sdk.interfaces.DJIMcuUpdateStateCallBack;
import dji.sdk.interfaces.DJIReceivedVideoDataCallBack;
import dji.sdk.widget.DjiGLSurfaceView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class FPVActivity extends DemoBaseActivity implements OnClickListener
{
    private static final String TAG = "FPVActivity";
    private static final boolean YAW_LEFT = false;
    private static final boolean YAW_RIGHT = true;
    private static final boolean PITCH_UP = true;
    private static final boolean PITCH_DOWN = false;
    private static final boolean ABSOLUTE_ANGLE = true;

    private int startingLoop = 0;
    private int firstLoopCount = 0;
    private int secondLoopCount = 0;
    private int thirdLoopCount = 0;
    private int fourthLoopCount = 0;
    private boolean panoInProgress = false;
    
    private Button mStartTakePhotoBtn;
    private Button mStartPanoBtn;
    private ProgressBar progressBar;

    //private TextView yawStatus;
    private TextView photoStatus;
    private TextView altitudeStatus;

    // Handles displaying the current battery percentage left
    private TextView batteryStatus;
    private String batteryStatusString = "";
    private String McStateString = "";

    private DjiGLSurfaceView mDjiGLSurfaceView;
    private DJIReceivedVideoDataCallBack mReceivedVideoDataCallBack = null;
    private DJIMcuUpdateStateCallBack mMcuCallBack = null;
    //private DJIBatteryUpdateInfoCallBack batteryStatusCallBack = null;
    
    private final int SHOWTOAST = 1;


    private TextView mConnectStateTextView;
    private Timer mTimer;
    
    
    private Context m_context;

    class Task extends TimerTask {
        //int times = 1;

        @Override
        public void run() 
        {
            //Log.d(TAG ,"==========>Task Run In!");
            checkConnectState(); 
        }

    };
    
    private void checkConnectState(){
        
        FPVActivity.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                boolean bConnectState = DJIDrone.getDjiCamera().getCameraConnectIsOk();
                if (bConnectState) {
                    //mConnectStateTextView.setText(R.string.camera_connection_ok);
                    mConnectStateTextView.setText("Connected");
                } else {
                    //mConnectStateTextView.setText(R.string.camera_connection_break);
                    mConnectStateTextView.setText("Disconnected");
                }
            }
        });
        
    }
    
    
    private Handler handler = new Handler(new Handler.Callback() {
        
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case SHOWTOAST:
                    setResultToToast((String)msg.obj); 
                    break;
                default:
                    break;
            }
            return false;
        }
    });
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_protocol_demo);

        mStartPanoBtn = (Button)findViewById(R.id.StartPanoButton);
        mStartPanoBtn.setOnClickListener(this);

        photoStatus = (TextView)findViewById(R.id.photoStatus);
        //yawStatus = (TextView)findViewById(R.id.yawStatus);
        //batteryStatus = (TextView)findViewById(R.id.batteryStatus);
        altitudeStatus = (TextView)findViewById(R.id.altitudeStatus);

        mConnectStateTextView = (TextView)findViewById(R.id.ConnectStateCameraTextView);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
 
        mDjiGLSurfaceView = (DjiGLSurfaceView)findViewById(R.id.DjiSurfaceView_02);
        
        mDjiGLSurfaceView.start();

        // Listen for video updates
        mReceivedVideoDataCallBack = new DJIReceivedVideoDataCallBack(){

            @Override
            public void onResult(byte[] videoBuffer, int size)
            {
                // TODO Auto-generated method stub
                mDjiGLSurfaceView.setDataToDecoder(videoBuffer, size);
            }

            
        };
        
        DJIDrone.getDjiCamera().setReceivedVideoDataCallBack(mReceivedVideoDataCallBack);

        /*batteryStatusCallBack = new DJIBatteryUpdateInfoCallBack() {
            @Override
            public void onResult(DJIBatteryProperty prop) {
                StringBuffer sb = new StringBuffer();
                sb.append(prop.remainPowerPercent);
                batteryStatusString = sb.toString();

                FPVActivity.this.runOnUiThread(new Runnable(){

                    @Override
                    public void run()
                    {
                        //batteryStatus.setText(batteryStatusString);
                        batteryStatus.setText(Integer.toString((int)System.currentTimeMillis()));
                    }
                });
            }
        };

        // Listen for battery updates
        DJIDrone.getDjiBattery().setBatteryUpdateInfoCallBack(batteryStatusCallBack);*/

        mMcuCallBack = new DJIMcuUpdateStateCallBack(){

            @Override
            public void onResult(DJIMainControllerSystemState state) {
                StringBuffer sb = new StringBuffer();
                //sb.append("satelliteCount=").append(state.satelliteCount).append("\n");
                sb.append(state.altitude);
                /*sb.append("pitch=").append(state.pitch).append("\n");
                sb.append("roll=").append(state.roll).append("\n");
                sb.append("yaw=").append(state.yaw).append("\n");
                sb.append("remainPower=").append(state.remainPower).append("\n");
                sb.append("remainFlyTime=").append(state.remainFlyTime).append("\n");
                sb.append("powerLevel=").append(state.powerLevel).append("\n");*/

                McStateString = sb.toString();

                FPVActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        altitudeStatus.setText("Alt: " + McStateString + "m");
                    }
                });
            }
        };

        DJIDrone.getDjiMC().setMcuUpdateStateCallBack(mMcuCallBack);
     
        m_context = this.getApplicationContext();

    }
    
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        
        mTimer = new Timer();
        Task task = new Task();
        mTimer.schedule(task, 0, 500);

        DJIDrone.getDjiMC().startUpdateTimer(1000);
        //DJIDrone.getDjiBattery().startUpdateTimer(2000);
        
        super.onResume();
    }
    
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        
        if(mTimer!=null) {            
            mTimer.cancel();
            mTimer.purge();
            mTimer = null;
        }

        DJIDrone.getDjiMC().stopUpdateTimer();
        //DJIDrone.getDjiBattery().stopUpdateTimer();
        
        super.onPause();
    }
    
    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        // TODO Auto-generated method stub
        mDjiGLSurfaceView.destroy();
        DJIDrone.getDjiCamera().setReceivedVideoDataCallBack(null);
        
        super.onDestroy();
    }
    @Override
    public void onClick(View v)
    {
        List<String> strlist = null;
        List<String> strlist2 = null;
        int TotalStringCnt = 0;
        String[] mSettingStrs = null;
        
        // TODO Auto-generated method stub
        switch (v.getId())
        {
            case R.id.StartPanoButton:
                startPano();
                break;
            default:
                break;
        }
    }

    // Kick off the pano process
    private void startPano() {
        if(panoInProgress == false) {
            if (DJIDrone.getDroneType() == DJIDroneType.DJIDrone_Inspire1)

                DJIDrone.getDjiCamera().setCameraMode(CameraMode.Camera_Capture_Mode, new DJIExecuteResultCallback() {

                    @Override
                    public void onResult(DJIError mErr) {
                        if (mErr.errorCode != 0) {
                            // TODO Auto-generated method stub
                            Log.d(TAG, "Set Camera Mode errorCode = " + mErr.errorCode);
                            Log.d(TAG, "Set Camera Mode errorDescription = " + mErr.errorDescription);
                            String result = "errorCode =" + mErr.errorCode + "\n" + "errorDescription =" + DJIError.getErrorDescriptionByErrcode(mErr.errorCode);
                            handler.sendMessage(handler.obtainMessage(SHOWTOAST, result));
                        } else { // Start taking photos

                            // Pano is started
                            panoInProgress = true;

                            handler.sendMessage(handler.obtainMessage(SHOWTOAST, "Starting panorama"));

                            // Pitch gimbal to zero
                            DJIGimbalRotation pitch = new DJIGimbalRotation(true, PITCH_UP, ABSOLUTE_ANGLE, 0);
                            DJIDrone.getDjiGimbal().updateGimbalAttitude(pitch, null, null);

                            // Pause before taking a photo
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    takeFirstRowPhotos();
                                }
                            }, 2000);
                        }

                    }

                });
        } else {
            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setMessage("Are you sure you want to stop this panorama?").setPositiveButton("Yes", dialogListener).setNegativeButton("No", dialogListener).show();
        }
    }

    DialogInterface.OnClickListener dialogListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface d, int which) {
            switch(which) {
                case DialogInterface.BUTTON_POSITIVE:
                    panoInProgress = false;
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    handler.sendMessage(handler.obtainMessage(SHOWTOAST, "NO"));
                    break;
            }
        }
    };


    // Pitch straight ahead and take 6 photos
    // Yaw take photo at
    // 0 degrees
    // 60 degrees
    // 120 degrees
    // 180 degrees
    // 240 degrees
    // 300 degrees
    private void takeFirstRowPhotos() {

        // Let's change the start button text to stop
        if(firstLoopCount == 0)
            mStartPanoBtn.setText("Stop");

        if(!continueWithPano()) return;

        if(firstLoopCount <= 5) {

            rotateGimbal(firstLoopCount * 60);

            // Delay for 2 seconds and take photo
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    takePhoto();
                }
            }, 3000);

            firstLoopCount = firstLoopCount + 1;

        } else { // First loop done

            firstLoopCount = 0;
            startingLoop = 2;

            resetStep1();

        }
    }

    // We need to pitch down 30 degrees before we start taking photos
    private void pitchDownBeforeSecondRowPhotos() {
        DJIGimbalRotation pitch = new DJIGimbalRotation(true, PITCH_DOWN, ABSOLUTE_ANGLE, -30);
        DJIDrone.getDjiGimbal().updateGimbalAttitude(pitch, null, null);

        // Delay 2 seconds before starting the photo loop
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                takeSecondRowPhotos();
            }
        }, 2000);
    }

    private void takeSecondRowPhotos() {

        if(!continueWithPano()) return;

        if(secondLoopCount <= 5) {

            rotateGimbal(secondLoopCount*60);

            // Delay for 2 seconds and take photo
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    takePhoto();
                }
            }, 3000);

            secondLoopCount = secondLoopCount + 1;

        } else { // Second loop done

            secondLoopCount = 0;
            startingLoop = 3;

            resetStep1();

        }
    }

    private void pitchDownBeforeThirdRowPhotos() {
        DJIGimbalRotation pitch = new DJIGimbalRotation(true, PITCH_DOWN, ABSOLUTE_ANGLE, -60);
        DJIDrone.getDjiGimbal().updateGimbalAttitude(pitch, null, null);

        // Delay 2 seconds before starting the photo loop
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                takeThirdRowPhotos();
            }
        }, 2000);
    }

    private void takeThirdRowPhotos() {

        if(!continueWithPano()) return;

        if(thirdLoopCount <= 5) {

            rotateGimbal(thirdLoopCount*60);

            // Delay for 2 seconds and take photo
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    takePhoto();
                }
            }, 3000);

            thirdLoopCount = thirdLoopCount + 1;

        } else { // Third loop done

            thirdLoopCount = 0;
            startingLoop = 4;

            resetStep1();

        }
    }

    private void pitchDownBeforeFourthRowPhotos() {
        DJIGimbalRotation pitch = new DJIGimbalRotation(true, PITCH_DOWN, ABSOLUTE_ANGLE, -90);
        DJIDrone.getDjiGimbal().updateGimbalAttitude(pitch, null, null);

        // Delay 2 seconds before starting the photo loop
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                takeFourthRowPhotos();
            }
        }, 2000);

    }

    private void takeFourthRowPhotos() {

        if(!continueWithPano()) return;

        if(fourthLoopCount <= 1) {

            rotateGimbal(fourthLoopCount*180);

            // Delay for 2 seconds and take photo
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    takePhoto();
                }
            }, 3000);

            fourthLoopCount = fourthLoopCount + 1;

        } else { // Fourth loop done

            fourthLoopCount = 0;
            startingLoop = 5; // Reset and get ready for the next pano

            // Reset the gimbal
            resetStep1();

        }
    }

    // Pitch gimbal back to 0 and reset vars for next pano to be taken
    private void finishPanoAndReset() {
        DJIGimbalRotation pitch = new DJIGimbalRotation(true, PITCH_UP, ABSOLUTE_ANGLE, 0);
        DJIDrone.getDjiGimbal().updateGimbalAttitude(pitch, null, null);

        startingLoop = 0;

        if(panoInProgress)
            handler.sendMessage(handler.obtainMessage(SHOWTOAST, "Panorama complete!"));
        else
            handler.sendMessage(handler.obtainMessage(SHOWTOAST, "Panorama stopped"));

        photoStatus.setText("Photo: 0/20");
        progressBar.setProgress(0);

        panoInProgress = false;
        mStartPanoBtn.setText("Start");

    }

    private void rotateGimbal(int y) {
        DJIGimbalRotation yaw = new DJIGimbalRotation(true, YAW_RIGHT, ABSOLUTE_ANGLE, y);
        DJIDrone.getDjiGimbal().updateGimbalAttitude(null, null, yaw);
    }

    // Yaw back to 170 degrees
    private void resetStep1() {
        DJIGimbalRotation yaw = new DJIGimbalRotation(true, true, true, 170);
        DJIDrone.getDjiGimbal().updateGimbalAttitude(null, null, yaw);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                resetStep2();
            }
        }, 2000);
    }

    // Yaw back to zero
    private void resetStep2() {
        DJIGimbalRotation yaw = new DJIGimbalRotation(true, true, true, 0);
        DJIDrone.getDjiGimbal().updateGimbalAttitude(null, null, yaw);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (panoInProgress == false)
                    finishPanoAndReset();
                else if (startingLoop == 2)
                    pitchDownBeforeSecondRowPhotos();
                else if (startingLoop == 3)
                    pitchDownBeforeThirdRowPhotos();
                else if (startingLoop == 4)
                    pitchDownBeforeFourthRowPhotos();
                else if (startingLoop == 5)
                    finishPanoAndReset();
            }
        }, 3000);
    }

    private void takePhoto() {
        DJIDrone.getDjiCamera().startTakePhoto(new DJIExecuteResultCallback() {
            @Override
            public void onResult(DJIError mErr) {
                // Photo failed - display error code and try again
                if (mErr.errorCode != 0) {
                    String result = "errorCode = " + mErr.errorCode + "\n" + "errorDescription = " + DJIError.getErrorDescriptionByErrcode(mErr.errorCode) + "\nTrying photo again";
                    handler.sendMessage(handler.obtainMessage(SHOWTOAST, result));
                    // Try to take another photo
                    // TODO: make this retry logic only happen 3 times otherwise we could run into an infinitely loop with no sd card
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            takePhoto();
                        }
                    }, 2000);
                    // Photo success continue looping
                } else {
                    // Delay 2 seconds and then loop back
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (firstLoopCount != 0) {
                                photoStatus.setText("Photo: " + firstLoopCount + "/20");
                                progressBar.setProgress(firstLoopCount * 5);
                                takeFirstRowPhotos();
                            } else if (secondLoopCount != 0) {
                                progressBar.setProgress((secondLoopCount + 6) * 5);
                                photoStatus.setText("Photo: " + (secondLoopCount + 6) + "/20");
                                takeSecondRowPhotos();
                            } else if (thirdLoopCount != 0) {
                                progressBar.setProgress((thirdLoopCount + 12) * 5);
                                photoStatus.setText("Photo: " + (thirdLoopCount + 12) + "/20");
                                takeThirdRowPhotos();
                            } else if (fourthLoopCount != 0) {
                                progressBar.setProgress((fourthLoopCount + 18) * 5);
                                photoStatus.setText("Photo: " + (fourthLoopCount + 18) + "/20");
                                takeFourthRowPhotos();
                            }

                            // Update the progress bar

                        }
                    }, 2000);
                }
            }
        });
    }

    private boolean continueWithPano() {
        if(panoInProgress == false) {

            firstLoopCount = secondLoopCount = thirdLoopCount = fourthLoopCount = 0;

            resetStep1();

            return false;
        } else {
            return true;
        }

    }
    private void setResultToToast(String result){
        Toast.makeText(FPVActivity.this, result, Toast.LENGTH_SHORT).show();
    }

}
