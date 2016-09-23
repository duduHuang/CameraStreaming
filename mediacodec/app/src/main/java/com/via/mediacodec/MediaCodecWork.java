package com.via.mediacodec;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.os.AsyncTask;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Created by NedHuang on 2016/8/10.
 */
public class MediaCodecWork extends AsyncTask<byte[], Integer, Integer> {

    private final static String TAG = "MediaCodecWork";
    private final static int MAX_SURFACEVIEW_NUMBER = 2;
    private final static String mMime = "video/avc";
    private MediaCodec mEncoder = null, mDecoder = null;
    private MediaFormat mEncoderFormat = null, mDecoderFormat = null;

    @Override
    protected Integer doInBackground(byte[]... params) {
        return null;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    private void qq() {
//        int rotation = mAct.getWindowManager().getDefaultDisplay().getRotation();
//        int degrees = 0;
//        switch (rotation) {
//            case Surface.ROTATION_0:
//                degrees = 0;
//                break;
//            case Surface.ROTATION_90:
//                degrees = 90;
//                break;
//            case Surface.ROTATION_180:
//                degrees = 180;
//                break;
//            case Surface.ROTATION_270:
//                degrees = 270;
//                break;
//        }
//        int result = (mCameraInfo.orientation - degrees + 360) % 360;
//        mCamera.setDisplayOrientation(90);
    }

    protected void setEncoder(int w, int h) {
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

    protected void setDecoder(int w, int h, SurfaceView surfaceView) {
        try {
            mDecoderFormat = MediaFormat.createVideoFormat(mMime, w, h);
            mDecoder = MediaCodec.createDecoderByType(mMime);
            mDecoder.configure(mDecoderFormat, surfaceView.getHolder().getSurface(), null, 0);
            mDecoder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
