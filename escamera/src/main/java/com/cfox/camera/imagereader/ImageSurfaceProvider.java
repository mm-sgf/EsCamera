package com.cfox.camera.imagereader;

import android.os.Handler;
import android.util.Size;
import android.view.Surface;


public abstract class ImageSurfaceProvider {
    private final TYPE mType;

    public enum TYPE {
        CAPTURE,
        PREVIEW
    }

    public ImageSurfaceProvider(TYPE type) {
        this.mType = type;
    }

    public TYPE getType() {
        return mType;
    }

    public final Surface onCreateImageSurface(Size previewSize, Size captureSize, Handler handler) {
        return createImageSurface(previewSize, captureSize, handler);
    }

    public abstract Surface createImageSurface(Size previewSize, Size captureSize, Handler handler);

    public abstract void release();
}
