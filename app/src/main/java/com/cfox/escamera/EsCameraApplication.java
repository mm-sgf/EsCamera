package com.cfox.escamera;

import android.app.Application;

import com.cfox.camera.EsCamera;
import com.cfox.camera.log.EsLog;
import com.cfox.camera.utils.OrientationSensorManager;

public class EsCameraApplication extends Application {
    private static final String TAG = "CameraApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        EsLog.setPrintTag("Es");
        EsCamera.init(this);
        OrientationSensorManager.getInstance().init(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        EsCamera.release();
    }
}
