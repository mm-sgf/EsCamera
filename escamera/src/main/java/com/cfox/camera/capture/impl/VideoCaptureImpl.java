package com.cfox.camera.capture.impl;

import android.util.Pair;
import android.util.Range;
import android.util.Size;

import com.cfox.camera.ConfigWrapper;
import com.cfox.camera.EsParams;
import com.cfox.camera.camera.info.CameraInfo;
import com.cfox.camera.camera.info.CameraInfoHelper;
import com.cfox.camera.camera.info.CameraInfoManager;
import com.cfox.camera.camera.info.CameraInfoManagerImpl;
import com.cfox.camera.capture.business.Business;
import com.cfox.camera.capture.business.impl.VideoBusinessImpl;
import com.cfox.camera.log.EsLog;
import com.cfox.camera.request.PreviewRequest;
import com.cfox.camera.capture.PreviewStateListener;
import com.cfox.camera.capture.VideoCapture;
import com.cfox.camera.mode.VideoMode;
import com.cfox.camera.request.RepeatRequest;
import com.cfox.camera.surface.SurfaceManager;
import com.cfox.camera.utils.CameraObserver;

import io.reactivex.annotations.NonNull;

public class VideoCaptureImpl implements VideoCapture {
    private final VideoMode mVideoMode;
    private final CameraInfoManager mCameraInfoManager;
    private final Business mBusiness;
    private final SurfaceManager mSurfaceManager;

    public VideoCaptureImpl(VideoMode videoMode, ConfigWrapper configWrapper) {
        mVideoMode = videoMode;
        mBusiness = new VideoBusinessImpl(configWrapper);
        mCameraInfoManager = CameraInfoManagerImpl.CAMERA_INFO_MANAGER;
        mSurfaceManager = SurfaceManager.getInstance();
    }

    @Override
    public final void onStartPreview(@NonNull PreviewRequest request, final PreviewStateListener listener) {
        final EsParams esParams = new EsParams();
        mSurfaceManager.setPreivewSurfaceProvider(request.getPreviewSurfaceProvider());
        esParams.put(EsParams.Key.SURFACE_MANAGER, mSurfaceManager);
        esParams.put(EsParams.Key.CAMERA_ID, request.getCameraId());
        esParams.put(EsParams.Key.FLASH_STATE, request.getFlashState());
        esParams.put(EsParams.Key.IMAGE_READER_PROVIDERS, request.getSurfaceProviders());

        // 切换Camera 信息管理中的 Camera 信息， 如前置camera  或 后置Camera
        CameraInfo cameraInfo = CameraInfoHelper.getInstance().getCameraInfo(request.getCameraId());
        mCameraInfoManager.initCameraInfo(cameraInfo);

        // 设置预览大小
        Size previewSizeForReq = request.getPreviewSize();
        Size previewSize = mBusiness.getPreviewSize(previewSizeForReq, mCameraInfoManager.getPreviewSize(mSurfaceManager.getPreviewSurfaceClass()));
        esParams.put(EsParams.Key.PREVIEW_SIZE, previewSize);
        mSurfaceManager.setAspectRatio(previewSize);

        // 设置图片大小
        Size pictureSizeForReq = request.getPictureSize();
        int imageFormat = request.getImageFormat();
        Size pictureSize = mBusiness.getPictureSize(pictureSizeForReq, mCameraInfoManager.getPictureSize(imageFormat));
        esParams.put(EsParams.Key.PIC_SIZE, pictureSize);

        EsLog.d("zoom size:" + mCameraInfoManager.getMaxZoom() + "   zoom area:" + mCameraInfoManager.getActiveArraySize());

        mVideoMode.requestPreview(esParams).subscribe(new CameraObserver<EsParams>() {
            @Override
            public void onNext(@NonNull EsParams resultParams) {
                EsLog.d("onNext: .requestPreview...." + resultParams);
                Integer afState = resultParams.get(EsParams.Key.AF_STATE);
                if (afState != null && listener != null) {
                    listener.onFocusStateChange(afState);
                }
            }
        });
    }

    @Override
    public final void onCameraRepeating(@NonNull RepeatRequest request) {
        EsParams esParams = new EsParams();
        Float zoomSize = request.getZoomSize();
        if (zoomSize != null) {
            esParams.put(EsParams.Key.ZOOM_VALUE, zoomSize);
        }

        Integer flash = request.getFlashState();
        if (flash != null) {
            esParams.put(EsParams.Key.FLASH_STATE, flash);
        }

        Integer ev = request.getEv();
        if (ev != null) {
            esParams.put(EsParams.Key.EV_SIZE, ev);
        }

        Pair<Float, Float> afTouchXy = request.getAfTouchXY();
        if (afTouchXy != null) {
            esParams.put(EsParams.Key.AF_TRIGGER, afTouchXy);
        }

        esParams.put(EsParams.Key.RESET_FOCUS, request.isResetFocus());

        EsLog.d("CameraRepeating==>" + esParams);

        mVideoMode.requestCameraRepeating(esParams).subscribe(new CameraObserver<>());
    }

    @Override
    public final void onStop() {
        EsParams esParams = new EsParams();
        mVideoMode.requestStop(esParams).doOnNext(esParams1 -> {
            EsLog.d("mSurfaceManager.release>>>>>>");
            mSurfaceManager.release();
        }).subscribe(new CameraObserver<>());
    }

    @Override
    public final Range<Integer> getEvRange() {
        return mCameraInfoManager.getEvRange();
    }

    @Override
    public final Range<Float> getFocusRange() {
        return mCameraInfoManager.getFocusRange();
    }
}