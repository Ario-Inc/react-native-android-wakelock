package com.arioliving.wakelock;

import com.facebook.react.uimanager.*;
import com.facebook.react.bridge.*;
import com.facebook.systrace.Systrace;
import com.facebook.systrace.SystraceMessage;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactRootView;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.shell.MainReactPackage;
import com.facebook.soloader.SoLoader;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import android.os.PowerManager;

public class AndroidWakeLockModule extends ReactContextBaseJavaModule {

    private PowerManager mPowerManager;
    private PowerManager.WakeLock mWakeLock;
    
    //Constructor
    public AndroidWakeLockModule(ReactApplicationContext reactContext) {
        super(reactContext);

        mPowerManager = (PowerManager)reactContext.getApplicationContext().getSystemService(Context.POWER_SERVICE);
    }

    @Override
    public String getName() {
        return "AndroidWakeLockModule";
    }

   @ReactMethod
   public void acquireWakeLock(boolean screenOn, Promise promise) {
        if (mWakeLock.isHeld()) {
            promise.reject("WakeLock already acquired");
        }
        else {
            if (screenOn) {
                mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "Acquired by ReactNative");
            }
            else {
                mWakeLock = mPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Acquired by ReactNative");
            }
            mWakeLock.acquire();
            promise.resolve(true);
        }
   }

    @ReactMethod
    public void releaseWakeLock(Promise promise) {
            if (!mWakeLock.isHeld()) {
                promise.reject("WakeLock is not held");
            }
            else {
                mWakeLock.release();
                promise.resolve(true);
            }
    }


}
