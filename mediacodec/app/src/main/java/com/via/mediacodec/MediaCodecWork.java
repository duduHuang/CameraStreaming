package com.via.mediacodec;


import android.app.Activity;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.camera2.CameraCaptureSession;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by NedHuang on 2016/8/10.
 */
public class MediaCodecWork implements Camera.PreviewCallback {

    private final static String TAG = "MediaCodecWork";
    private Activity mAct = null;
    private Camera mCamera;
    private Camera.CameraInfo mCameraInfo;
    private CameraCaptureSession mCameraCaptureSession;
    private Camera.Parameters mParameters;
    private final static int MAX_SURFACEVIEW_NUMBER = 2;
    private final static String mMime = "video/avc";

    private SurfaceView[] mSurfaceView = new SurfaceView[MAX_SURFACEVIEW_NUMBER];

    private MediaCodec mEncoder = null, mDecoder = null;
    private MediaFormat mEncoderFormat = null, mDecoderFormat = null;

    public MediaCodecWork(Activity a, SurfaceView[] s) {
        mAct = a;
        mCamera = Camera.open();
        mCameraInfo = new Camera.CameraInfo();
        mSurfaceView[0] = s[0];
        mSurfaceView[1] = s[1];
    }

    public void release() {
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }

    public void setCameraPreView() {
        try {
            mCamera.setPreviewDisplay(mSurfaceView[0].getHolder());

            Camera.getCameraInfo(0, mCameraInfo);
//            int rotation = mAct.getWindowManager().getDefaultDisplay().getRotation();
//            int degrees = 0;
//            switch (rotation) {
//                case Surface.ROTATION_0:
//                    degrees = 0;
//                    break;
//                case Surface.ROTATION_90:
//                    degrees = 90;
//                    break;
//                case Surface.ROTATION_180:
//                    degrees = 180;
//                    break;
//                case Surface.ROTATION_270:
//                    degrees = 270;
//                    break;
//            }
//            int result = (mCameraInfo.orientation - degrees + 360) % 360;
//            mCamera.setDisplayOrientation(result);

            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
        mParameters = camera.getParameters();
        int format = mParameters.getPreviewFormat();
        if (format == ImageFormat.NV21 || format == ImageFormat.YUY2 || format == ImageFormat.NV16) {
            int w = mParameters.getPreviewSize().width;
            int h = mParameters.getPreviewSize().height;

            Toast.makeText(mAct, "onPreviewFrame: preview format: " + format + " size: " + bytes.length + " w: " + w + " h: " + h, Toast.LENGTH_SHORT).show();

            // Handle preview frame work.

            if (mEncoder == null) {
                setEncoder(w, h);
            }

            if (mDecoder == null) {
                setDecoder(w, h);
            }


        }
    }

    private void setEncoder(int w, int h) {
        try {
            int colorFormat = selectColorFormat(selectCodec(mMime), mMime);
            mEncoder = MediaCodec.createDecoderByType(mMime);
            mEncoderFormat = MediaFormat.createVideoFormat(mMime, w, h);
            mEncoderFormat.setInteger(MediaFormat.KEY_BIT_RATE, 125000);
            mEncoderFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 15);
            mEncoderFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar);
            mEncoderFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, colorFormat);
            mEncoderFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 5);
            mEncoder.configure(mEncoderFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            mEncoder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static MediaCodecInfo selectCodec(String mimeType) {
        int numCodecs = MediaCodecList.getCodecCount();
        for (int i = 0; i < numCodecs; i++) {
            MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);

            if (!codecInfo.isEncoder()) {
                continue;
            }

            String[] types = codecInfo.getSupportedTypes();
            for (int j = 0; j < types.length; j++) {
                if (types[j].equalsIgnoreCase(mimeType)) {
                    return codecInfo;
                }
            }
        }
        return null;
    }

    private static int selectColorFormat(MediaCodecInfo codecInfo, String mimeType) {
        MediaCodecInfo.CodecCapabilities capabilities = codecInfo.getCapabilitiesForType(mimeType);
        for (int i = 0; i < capabilities.colorFormats.length; i++) {
            int colorFormat = capabilities.colorFormats[i];
            if (isRecognizedFormat(colorFormat)) {
                return colorFormat;
            }
        }
        return 0;
    }

    private static boolean isRecognizedFormat(int colorFormat) {
        switch (colorFormat) {
            // these are the formats we know how to handle for this testcase MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar:
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedPlanar:
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar:
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedSemiPlanar:
            case MediaCodecInfo.CodecCapabilities.COLOR_TI_FormatYUV420PackedSemiPlanar:
                return true;
            default:
                return false;
        }
    }

    private void setDecoder(int w, int h) {
        try {
            mDecoderFormat = MediaFormat.createVideoFormat(mMime, w, h);
            mDecoder = MediaCodec.createDecoderByType(mMime);
            mDecoder.configure(mDecoderFormat, mSurfaceView[1].getHolder().getSurface(), null, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
