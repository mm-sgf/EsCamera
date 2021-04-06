package com.cfox.camera.capture;

public interface PreviewStateListener {

    void onFirstFrameCallback();

    void onFocusStateChange(int state);
}
