package com.via.mediacodec;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by NedHuang on 2016/8/10.
 */
public class FragmentMainAct extends Fragment {

    private final static String TAG = "FragmentMainAct";
    private final static int MAX_SURFACEVIEW_NUMBER = 2;
    private Activity mAct = null;
    private View vf;
    private ViewGroup vg;
    private Button mBtnExit, mBtnCamera, mBtnEncode;
    private SurfaceView[] mSurfaceView = new SurfaceView[MAX_SURFACEVIEW_NUMBER];
    private MediaCodecWork mMediaCodeWork = null;

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
        if (mMediaCodeWork != null) {
            mMediaCodeWork.release();
            mMediaCodeWork = null;
        }
        super.onPause();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btn_camera) {
                if (mMediaCodeWork != null) {
                    mMediaCodeWork.release();
                    mMediaCodeWork = null;
                }
                mMediaCodeWork = new MediaCodecWork(mAct, mSurfaceView);
                mMediaCodeWork.setCameraPreView();
            } else if (v.getId() == R.id.btn_exit) {

            } else if (v.getId() == R.id.btn_encode) {

            }
        }
    };
}
