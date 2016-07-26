package com.dronepan.AndroidApp;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import no.nordicsemi.android.log.ILogSession;
import no.nordicsemi.android.log.Logger;
import timber.log.Timber;

/**
 * Created by jason on 7/10/16.
 */

public class DronePanApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
            Timber.plant(new NRFTee(this));
        }

    }
    private class NRFTee extends Timber.Tree {

        private ILogSession mLogSession;

        public NRFTee(Context ctx) {
            mLogSession = Logger.newSession(ctx, "key", "DronePan");

        }

        @Override
        protected void log(int priority, String tag, String message, Throwable t) {
            switch (priority) {
                case Log.DEBUG:
                    Logger.d(mLogSession, message);
                    break;
                case Log.VERBOSE:
                    Logger.v(mLogSession, message);
                    break;
                case Log.INFO:
                    Logger.i(mLogSession, message);
                    break;
                case Log.WARN:
                    Logger.w(mLogSession, message);
                    break;
                case Log.ERROR:
                    Logger.e(mLogSession, message);
                    break;
                default:
                    Logger.w(mLogSession, message);
            }
        }
    }
}
