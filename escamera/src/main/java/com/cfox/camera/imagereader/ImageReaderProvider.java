package com.cfox.camera.imagereader;

import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.util.Size;
import android.view.Surface;

import java.nio.ByteBuffer;


public abstract class ImageReaderProvider extends ImageSurfaceProvider implements ImageReader.OnImageAvailableListener {

    private ImageReader mImageReader;

    public ImageReaderProvider(TYPE type) {
        super(type);
    }

    @Override
    public Surface createImageSurface(Size previewSize, Size captureSize, Handler handler) {
        mImageReader = createImageReader(previewSize, captureSize);
        mImageReader.setOnImageAvailableListener(this, handler);
        return mImageReader.getSurface();
    }

    @Override
    public void release() {
        if (mImageReader != null) {
            mImageReader.close();
        }
    }

    public abstract ImageReader createImageReader(Size previewSize, Size captureSize);

    public abstract void onImageAvailable(ImageReader reader);

    public byte[] getByteFromReader(ImageReader reader) {
        Image image = reader.acquireLatestImage();
        byte[] bytes = getByteFromImage(image);
        image.close();
        return bytes;
    }

    public byte[] getByteFromImage(Image image) {
        int totalSize = 0;
        for (Image.Plane plane : image.getPlanes()) {
            totalSize += plane.getBuffer().remaining();
        }
        ByteBuffer totalBuffer = ByteBuffer.allocate(totalSize);
        for (Image.Plane plane : image.getPlanes()) {
            totalBuffer.put(plane.getBuffer());
        }
        return totalBuffer.array();
    }
}
