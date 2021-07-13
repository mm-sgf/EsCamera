package com.cfox.camera.surface;

import android.util.Size;
import android.view.Surface;

import com.cfox.camera.log.EsLog;

import java.util.ArrayList;
import java.util.List;

/**
 * 进行 Surface 管理。
 *
 * 对camera 输出的surface 进行管理，包括预览 surface 和 capture surface
 */
public class SurfaceManager {

    private static final SurfaceManager sSurfaceManager = new SurfaceManager();

    private PreviewSurfaceProvider mPreviewSurfaceProvider;
    private final List<Surface> mCaptureSurface;
    private final List<Surface> mPreviewSurface;

    private SurfaceManager() {
        this.mCaptureSurface = new ArrayList<>();
        this.mPreviewSurface = new ArrayList<>();
    }

    public static SurfaceManager getInstance() {
        return sSurfaceManager;
    }

    public void setPreivewSurfaceProvider(PreviewSurfaceProvider previewSurfaceProvider) {
        release();
        EsLog.e(" set ===surface ===>");
        this.mPreviewSurfaceProvider = previewSurfaceProvider;
    }

    /**
     * 只获取预览surface list
     */
    public List<Surface> getPreviewSurface() {
        EsLog.e("get preview " + mPreviewSurfaceProvider.getSurface());
        mPreviewSurface.add(mPreviewSurfaceProvider.getSurface());
        return mPreviewSurface;
    }

    public Class getPreviewSurfaceClass(){
        return mPreviewSurfaceProvider.getPreviewSurfaceClass();
    }


    /**
     * 获取所有surface list , 包括预览 surface 和 capture surface
     */
    public List<Surface> getTotalSurface() {
        List<Surface> surfaceList = new ArrayList<>(mCaptureSurface);
        surfaceList.addAll(getPreviewSurface());
        if (surfaceList.size() == 0) {
            EsLog.e("getTotalSurface surface list size is 0");
        }
        return surfaceList;
    }

    public void addPreviewSurface(Surface surface) {
        mPreviewSurface.add(surface);
    }

    public void addCaptureSurface(Surface surface) {
        mCaptureSurface.add(surface);
    }

    public List<Surface> getReaderSurface() {
        return mCaptureSurface;
    }

    public void setAspectRatio(Size size) {
        mPreviewSurfaceProvider.setAspectRatio(size);
    }

    public boolean isAvailable() {
        return mPreviewSurfaceProvider.isAvailable();
    }

    public void release() {
        EsLog.d("release===>");
        mPreviewSurfaceProvider = null;
        mCaptureSurface.clear();
        mPreviewSurface.clear();
    }

}
