package com.cfox.camera.imagereader;

import android.media.ImageReader;
import android.os.Handler;
import android.util.Size;
import android.view.Surface;

import com.cfox.camera.EsParams;
import com.cfox.camera.utils.WorkerHandlerManager;

import java.util.ArrayList;
import java.util.List;

public class ImageReaderManagerImpl implements ImageReaderManager {

    private final List<ImageSurfaceProvider> mImageSurfaceProviders;
    private final Handler mImageReaderHandler;
    public ImageReaderManagerImpl() {
        mImageSurfaceProviders = new ArrayList<>();
        mImageReaderHandler = WorkerHandlerManager.getHandler(WorkerHandlerManager.Tag.T_TYPE_IMAGE_READER);
    }

    @Override
    public Surface createImageSurface(EsParams esParams, ImageSurfaceProvider provider) {
        Size picSize = esParams.get(EsParams.Key.PIC_SIZE);
        Size previewSize = esParams.get(EsParams.Key.PREVIEW_SIZE);
        mImageSurfaceProviders.add(provider);
        return provider.onCreateImageSurface(previewSize, picSize, mImageReaderHandler);
    }


    @Override
    public void closeImageReaders() {
        for (ImageSurfaceProvider provider : mImageSurfaceProviders) {
            provider.release();
        }
        mImageSurfaceProviders.clear();
    }
}
