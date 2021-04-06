package com.cfox.camera.mode.impl;


import com.cfox.camera.EsException;
import com.cfox.camera.imagesurface.ImageReaderManager;
import com.cfox.camera.imagesurface.ImageSurfaceManagerImpl;
import com.cfox.camera.imagesurface.ImageReaderProvider;
import com.cfox.camera.sessionmanager.VideoSessionManger;
import com.cfox.camera.mode.VideoMode;
import com.cfox.camera.mode.BaseMode;
import com.cfox.camera.surface.SurfaceManager;
import com.cfox.camera.utils.EsError;
import com.cfox.camera.EsParams;

import java.util.List;

import io.reactivex.Observable;

public class VideoModeImpl extends BaseMode implements VideoMode {
    private final VideoSessionManger mVideoSessionManger;
    private final ImageReaderManager mImageReaderManager;

    public VideoModeImpl(VideoSessionManger videoSessionManger) {
        super(videoSessionManger);
        mVideoSessionManger = videoSessionManger;
        mImageReaderManager = new ImageSurfaceManagerImpl();
    }

    @Override
    public void onRequestStop() {
        mImageReaderManager.closeImageReaders();
    }

    @Override
    protected Observable<EsParams> applySurface(final EsParams esParams) {
        return Observable.create(emitter -> {
            SurfaceManager manager = esParams.get(EsParams.Key.SURFACE_MANAGER);
            List<ImageReaderProvider> imageReaderProviders = esParams.get(EsParams.Key.IMAGE_READER_PROVIDERS);
            if (manager.isAvailable()) {
                for (ImageReaderProvider provider : imageReaderProviders) {
                    if (provider.getType() == ImageReaderProvider.TYPE.PREVIEW) {
                        manager.addPreviewSurface(mImageReaderManager.createImageSurface(esParams, provider));
                    } else if (provider.getType() == ImageReaderProvider.TYPE.CAPTURE) {
                        manager.addCaptureSurface(mImageReaderManager.createImageSurface(esParams, provider));
                    }
                }
                emitter.onNext(esParams);
            } else {
                emitter.onError(new EsException("surface isAvailable = false , check SurfaceProvider implement", EsError.ERROR_CODE_SURFACE_UN_AVAILABLE));
            }
        });
    }
}