package com.cfox.camera.mode.impl;

import com.cfox.camera.EsException;
import com.cfox.camera.surface.SurfaceProvider;
import com.cfox.camera.surface.SurfaceProviderReaderManager;
import com.cfox.camera.surface.SurfaceProviderSurfaceManagerImpl;
import com.cfox.camera.surface.ImageReaderProvider;
import com.cfox.camera.sessionmanager.PhotoSessionManager;
import com.cfox.camera.log.EsLog;
import com.cfox.camera.mode.PhotoMode;
import com.cfox.camera.mode.BaseMode;
import com.cfox.camera.surface.SurfaceManager;
import com.cfox.camera.utils.EsError;
import com.cfox.camera.EsParams;

import java.util.List;

import io.reactivex.Observable;

/**
 * 整理 request 和 surface 。 返回数据整理
 */
public class PhotoModeImpl extends BaseMode implements PhotoMode {
    private final PhotoSessionManager mPhotoSessionManager;
    private final SurfaceProviderReaderManager mSurfaceProviderReaderManager;
    public PhotoModeImpl(PhotoSessionManager photoSessionManager) {
        super(photoSessionManager);
        mPhotoSessionManager = photoSessionManager;
        mSurfaceProviderReaderManager = new SurfaceProviderSurfaceManagerImpl();
    }

    @Override
    public void onRequestStop() {
        mSurfaceProviderReaderManager.release();
    }

    @Override
    protected Observable<EsParams> applySurface(final EsParams esParams) {
        return Observable.create(emitter -> {
            SurfaceManager manager = esParams.get(EsParams.Key.SURFACE_MANAGER);
            List<SurfaceProvider> surfaceProviders = esParams.get(EsParams.Key.IMAGE_READER_PROVIDERS);
            if (manager.isAvailable()) {
                for (SurfaceProvider provider : surfaceProviders) {
                    if (provider.getType() == ImageReaderProvider.TYPE.PREVIEW) {
                        manager.addPreviewSurface(mSurfaceProviderReaderManager.createSurface(esParams, provider));
                    } else if (provider.getType() == ImageReaderProvider.TYPE.CAPTURE) {
                        manager.addCaptureSurface(mSurfaceProviderReaderManager.createSurface(esParams, provider));
                    }
                }
                emitter.onNext(esParams);
            } else  {
                emitter.onError(new EsException("surface isAvailable = false , check SurfaceProvider implement", EsError.ERROR_CODE_SURFACE_UN_AVAILABLE));
            }
        });
    }

    @Override
    public Observable<EsParams> requestCapture(EsParams esParams) {
        EsLog.d("requestCapture: ......" + esParams);
        return mPhotoSessionManager.capture(esParams);
    }
}
