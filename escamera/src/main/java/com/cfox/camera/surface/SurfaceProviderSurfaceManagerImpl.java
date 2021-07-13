package com.cfox.camera.surface;

import android.os.Handler;
import android.util.Size;
import android.view.Surface;

import com.cfox.camera.EsParams;
import com.cfox.camera.utils.WorkerHandlerManager;

import java.util.ArrayList;
import java.util.List;

public class SurfaceProviderSurfaceManagerImpl implements SurfaceProviderReaderManager {

    private final List<SurfaceProvider> mSurfaceProviders;
    private final Handler mImageSurfaceHandler;
    public SurfaceProviderSurfaceManagerImpl() {
        mSurfaceProviders = new ArrayList<>();
        mImageSurfaceHandler = WorkerHandlerManager.getHandler(WorkerHandlerManager.Tag.T_TYPE_IMAGE_SURFACE);
    }

    @Override
    public Surface createSurface(EsParams esParams, SurfaceProvider provider) {
        Size picSize = esParams.get(EsParams.Key.PIC_SIZE);
        Size previewSize = esParams.get(EsParams.Key.PREVIEW_SIZE);
        mSurfaceProviders.add(provider);
        return provider.onCreateSurface(previewSize, picSize, mImageSurfaceHandler);
    }


    @Override
    public void release() {
        for (SurfaceProvider provider : mSurfaceProviders) {
            provider.release();
        }
        mSurfaceProviders.clear();
    }
}
