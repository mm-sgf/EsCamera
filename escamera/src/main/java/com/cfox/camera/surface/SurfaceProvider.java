package com.cfox.camera.surface;

import android.os.Handler;
import android.util.Size;
import android.view.Surface;


public abstract class SurfaceProvider {
    private final TYPE mType;

    public enum TYPE {
        CAPTURE,
        PREVIEW
    }

    public SurfaceProvider(TYPE type) {
        this.mType = type;
    }

    public TYPE getType() {
        return mType;
    }

    public final Surface onCreateSurface(Size previewSize, Size captureSize, Handler handler) {
        return createSurface(previewSize, captureSize, handler);
    }

    public abstract Surface createSurface(Size previewSize, Size captureSize, Handler handler);

    public abstract void release();
}
