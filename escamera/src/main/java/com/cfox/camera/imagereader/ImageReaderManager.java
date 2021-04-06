package com.cfox.camera.imagereader;

import android.view.Surface;

import com.cfox.camera.EsParams;


public interface ImageReaderManager {

    Surface createImageSurface(EsParams esParams, ImageSurfaceProvider provider);

    void closeImageReaders();
}
