package com.cfox.camera.surface;

import android.view.Surface;

import com.cfox.camera.EsParams;


public interface SurfaceProviderReaderManager {

    Surface createSurface(EsParams esParams, SurfaceProvider provider);

    void release();
}
