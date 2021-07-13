package com.cfox.camera.request;

import android.util.Size;

import com.cfox.camera.surface.ImageReaderProvider;
import com.cfox.camera.surface.SurfaceProvider;
import com.cfox.camera.surface.PreviewSurfaceProvider;
import com.cfox.camera.EsParams;

import java.util.ArrayList;
import java.util.List;

public class PreviewRequest {
    private final List<SurfaceProvider> mSurfaceProviders = new ArrayList<>();
    private final PreviewSurfaceProvider mPreviewSurfaceProvider;
    private final String mCameraId;
    private final Size mPreviewSize;
    private final Size mPictureSize;
    private final int mImageFormat;
    private final int mFlashState;
    private PreviewRequest(Builder builder) {
        mSurfaceProviders.addAll(builder.mSurfaceProviders);
        mPreviewSurfaceProvider = builder.mPreviewSurfaceProvider;
        mCameraId = builder.mCameraId;
        mPictureSize = builder.mPictureSize;
        mImageFormat = builder.mImageFormat;
        mPreviewSize = builder.mPreviewSize;
        mFlashState = builder.mFlashState;
    }

    public PreviewSurfaceProvider getPreviewSurfaceProvider() {
        return mPreviewSurfaceProvider;
    }

    public List<SurfaceProvider> getSurfaceProviders() {
        return mSurfaceProviders;
    }

    public Size getPreviewSize() {
        return mPreviewSize;
    }

    public Size getPictureSize() {
        return mPictureSize;
    }

    public int getImageFormat() {
        return mImageFormat;
    }

    public String getCameraId() {
        return mCameraId;
    }

    public int getFlashState() {
        return mFlashState;
    }

    public static Builder createBuilder() {
        return new Builder();
    }

    public static class Builder {
        private final List<SurfaceProvider> mSurfaceProviders = new ArrayList<>();
        private PreviewSurfaceProvider mPreviewSurfaceProvider;
        private String mCameraId = EsParams.Value.CAMERA_ID.BACK;
        private Size mPreviewSize;
        private Size mPictureSize;
        private int mImageFormat;
        private int mFlashState = EsParams.Value.FLASH_STATE.OFF;
        public Builder setPreviewSurfaceProvider(PreviewSurfaceProvider provider) {
            this.mPreviewSurfaceProvider = provider;
            return this;
        }

        public Builder openBackCamera() {
            mCameraId = EsParams.Value.CAMERA_ID.BACK;
            return this;
        }

        public Builder openFontCamera() {
            mCameraId = EsParams.Value.CAMERA_ID.FONT;
            return this;
        }

        public Builder setPreviewSize(Size previewSize) {
            mPreviewSize = previewSize;
            return this;
        }

        public Builder setPictureSize(Size pictureSize, int imageFormat) {
            mPictureSize = pictureSize;
            mImageFormat = imageFormat;
            return this;
        }

        public Builder setFlash(FlashState flashState){
            mFlashState = flashState.getState();
            return this;
        }

        public Builder addSurfaceProvider(ImageReaderProvider provider){
            mSurfaceProviders.add(provider);
            return this;
        }

        public PreviewRequest builder() {
            return new PreviewRequest(this);
        }
    }
}
