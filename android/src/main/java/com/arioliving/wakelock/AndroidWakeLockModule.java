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
import android.content.ContentResolver;
import android.os.Bundle;
import android.util.Log;
import android.provider.Settings;
import android.os.Build;
import android.content.Intent;
import android.net.Uri;
import android.os.PowerManager;

public class AndroidWakeLockModule extends ReactContextBaseJavaModule {

    private PowerManager mPowerManager;
    private PowerManager.WakeLock mWakeLock;
    private ContentResolver mContentResolver;
    private Context mContext;
    //Constructor
    public AndroidWakeLockModule(ReactApplicationContext reactContext) {
        super(reactContext);

        mPowerManager = (PowerManager)reactContext.getApplicationContext().getSystemService(Context.POWER_SERVICE);
        mWakeLock = mPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Acquired by ReactNative");
        mContentResolver = reactContext.getContentResolver();
        mContext = reactContext;
    }

    @Override
    public String getName() {
        return "AndroidWakeLockModule";
    }

   @ReactMethod
   public void acquireWakeLock(boolean screenOn, Promise promise) {
        // if (mWakeLock.isHeld()) {
        //     promise.reject("WakeLock already acquired");
        // }
        // else {
            if (mWakeLock.isHeld()) {
                mWakeLock.release();
            }

            if (screenOn) {
                mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "Acquired by ReactNative");
            }
            else {
                mWakeLock = mPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Acquired by ReactNative");
            }
            mWakeLock.acquire();
            promise.resolve(true);
        // }
   }

    @ReactMethod
    public void releaseWakeLock(Promise promise) {
            if (mWakeLock.isHeld()) {
                mWakeLock.release();
            }
            promise.resolve(true);
    }
  
    @ReactMethod
    public void turnScreenOff(Promise promise) {
        if (checkSystemWritePermission()) {
            Settings.System.putInt(mContentResolver, Settings.System.SCREEN_BRIGHTNESS, 1);
            if (mWakeLock.isHeld()) {
                mWakeLock.release();
            }
            mWakeLock = mPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Acquired by ReactNative");
            mWakeLock.acquire();
            Settings.System.putString(mContentResolver, Settings.System.SCREEN_OFF_TIMEOUT, "7000");

            promise.resolve(true);
        }
        else {
            promise.reject("nope");
        }
    }

    @ReactMethod
    public void setScreenBrightness(Integer level) {
        if (checkSystemWritePermission()) {
            
            if (level > 255) {
                level = 255;
            }
            if (level < 1) {
                level = 1;
            }

            Settings.System.putInt(mContentResolver, Settings.System.SCREEN_BRIGHTNESS, level);
        }
    }

    private boolean checkSystemWritePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(mContext))
                return true;
            else
                openAndroidPermissionsMenu();
        }
        return false;
    }

    private void openAndroidPermissionsMenu() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + mContext.getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        }
    }

}
