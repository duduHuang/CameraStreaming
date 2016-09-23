package com.via.mediacodec;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.camera2.CameraCaptureSession;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.IOException;

/**
 * Created by NedHuang on 2016/8/10.
 */
public class FragmentMainAct extends Fragment implements Camera.PreviewCallback, SurfaceHolder.Callback {

    private final static String TAG = "FragmentMainAct";
    private final static int MAX_SURFACEVIEW_NUMBER = 2;
    private Activity mAct = null;
    private FragmentMainAct instance = this;
    private View vf;
    private ViewGroup vg;
    private Button mBtnExit, mBtnCamera, mBtnEncode;
    private SurfaceView[] mSurfaceView = new SurfaceView[MAX_SURFACEVIEW_NUMBER];

    private Camera mCamera;
    private Camera.CameraInfo mCameraInfo;
    private CameraCaptureSession mCameraCaptureSession;
    private Camera.Parameters mParameters;
    MediaCodecWork mMediaCodecWork = new MediaCodecWork();

    public void init(Activity a) {
        mAct = a;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        vg = container;
        vf = inflater.inflate(R.layout.fragmentmain, container, false);
        initButtonAndSurfaceView();
        mBtnCamera.setOnClickListener(mOnClickListener);
        mBtnEncode.setOnClickListener(mOnClickListener);
        mBtnExit.setOnClickListener(mOnClickListener);
        return vf;
    }

    private void initButtonAndSurfaceView() {
        mBtnExit = (Button) vf.findViewById(R.id.btn_exit);
        mBtnCamera = (Button) vf.findViewById(R.id.btn_camera);
        mBtnEncode = (Button) vf.findViewById(R.id.btn_encode);
        mSurfaceView[0] = (SurfaceView) vf.findViewById(R.id.surface_camera);
        mSurfaceView[1] = (SurfaceView) vf.findViewById(R.id.surface_encode);
    }

    @Override
    public void onPause() {
        release();
        super.onPause();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mCamera.setOneShotPreviewCallback(this);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btn_camera) {
                mCamera = Camera.open();
                mCameraInfo = new Camera.CameraInfo();
                mParameters = mCamera.getParameters();
                try {
                    mCamera.setPreviewDisplay(mSurfaceView[0].getHolder());
                    Camera.getCameraInfo(0, mCameraInfo);
                    mCamera.startPreview();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (v.getId() == R.id.btn_exit) {
                onPause();
            } else if (v.getId() == R.id.btn_encode) {
                final int format = mParameters.getPreviewFormat();
                if (format == ImageFormat.NV21 || format == ImageFormat.YUY2 || format == ImageFormat.NV16) {
                    final int w = mParameters.getPreviewSize().width;
                    final int h = mParameters.getPreviewSize().height;
                    mMediaCodecWork.setEncoder(w, h);
                    mMediaCodecWork.setDecoder(w, h, mSurfaceView[1]);
                }
            }
        }
    };


    public void release() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }
}
