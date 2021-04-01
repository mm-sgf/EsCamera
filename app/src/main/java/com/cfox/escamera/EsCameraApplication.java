package com.cfox.escamera;

import android.app.Application;

import com.cfox.camera.EsCamera;

public class EsCameraApplication extends Application {
    private static final String TAG = "CameraApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        EsCamera.init(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        EsCamera.release();
    }
}
