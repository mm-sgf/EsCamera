package com.cfox.camera.mode;


import com.cfox.camera.sessionmanager.SessionManager;
import com.cfox.camera.log.EsLog;
import com.cfox.camera.EsParams;
import com.cfox.camera.utils.WorkerHandlerManager;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;

public abstract class BaseMode implements IMode {

    private final SessionManager mSessionManager;

    protected BaseMode(SessionManager sessionManager) {
        this.mSessionManager = sessionManager;
    }

    @Override
    public void init() {
        mSessionManager.init();
    }

    public Observable<EsParams> requestPreview(EsParams esParams) {
        return applySurface(esParams).flatMap((Function<EsParams, ObservableSource<EsParams>>) applyResultParams -> {
            EsLog.d("open camera request ===>params:" + applyResultParams);
            // open camera request
            return mSessionManager.onOpenCamera(applyResultParams);
        }).flatMap((Function<EsParams, ObservableSource<EsParams>>) openResultParams -> {
            EsLog.d("create session before ....." + openResultParams);
            // 创建 camera session
            return mSessionManager.onCreatePreviewSession(openResultParams);
        }).flatMap((Function<EsParams, ObservableSource<EsParams>>) sessionResultParams -> {
            EsLog.d("onSendRepeatingRequest ......" + sessionResultParams);
            return mSessionManager.onPreviewRepeatingRequest(sessionResultParams);
        }).subscribeOn(WorkerHandlerManager.getScheduler(WorkerHandlerManager.Tag.T_TYPE_CAMERA));
    }

    @Override
    public Observable<EsParams> requestCameraRepeating(EsParams esParams) {
        return mSessionManager.onRepeatingRequest(esParams);
    }

    @Override
    public Observable<EsParams> requestStop(EsParams esParams) {
        onRequestStop();
        return mSessionManager.close(esParams).subscribeOn(
                AndroidSchedulers.from(WorkerHandlerManager.getLooper(WorkerHandlerManager.Tag.T_TYPE_CAMERA)));
    }

    public void onRequestStop() { }

    protected abstract Observable<EsParams> applySurface(EsParams esParams);

}

