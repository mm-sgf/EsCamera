package com.cfox.camera.utils;


import com.cfox.camera.BuildConfig;
import com.cfox.camera.log.EsLog;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public class CameraObserver<T> implements Observer<T> {
    @Override
    public void onSubscribe(@NonNull Disposable d) {

    }

    @Override
    public void onNext(@NonNull T t) {

    }

    @Override
    public void onError(@NonNull Throwable e) {
        EsLog.e("onError: " + e);
        if (BuildConfig.DEBUG) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onComplete() {

    }
}
