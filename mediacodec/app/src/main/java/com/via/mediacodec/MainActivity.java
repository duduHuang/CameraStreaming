package com.via.mediacodec;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

/**
 * Created by NedHuang on 2016/8/10.
 */
public class MainActivity extends Activity {

    private MainActivity mAct = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        FragmentMainAct f = new FragmentMainAct();
        f.init(mAct);
    }
}
