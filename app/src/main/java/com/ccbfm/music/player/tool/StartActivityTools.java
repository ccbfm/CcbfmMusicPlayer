package com.ccbfm.music.player.tool;

import android.content.Intent;
import android.os.Bundle;

import com.ccbfm.music.player.ui.activity.BaseActivity;
import com.ccbfm.music.player.ui.fragment.BaseFragment;

public class StartActivityTools {

    public static void start(BaseActivity baseActivity, Class<? extends BaseActivity> baseActivityClass) {
        startForResult(baseActivity, baseActivityClass, null, 0);
    }

    public static void start(BaseFragment baseFragment, Class<? extends BaseActivity> baseActivityClass) {
        startForResult(baseFragment, baseActivityClass, null, 0);
    }

    public static void start(BaseActivity baseActivity, Class<? extends BaseActivity> baseActivityClass,
                             Bundle bundle) {
        startForResult(baseActivity, baseActivityClass, bundle, 0);
    }

    public static void start(BaseFragment baseFragment, Class<? extends BaseActivity> baseActivityClass,
                             Bundle bundle) {
        startForResult(baseFragment, baseActivityClass, bundle, 0);
    }

    public static void startForResult(BaseActivity baseActivity, Class<? extends BaseActivity> baseActivityClass,
                                      int requestCode) {
        startForResult(baseActivity, baseActivityClass, null, requestCode);
    }

    public static void startForResult(BaseFragment baseFragment, Class<? extends BaseActivity> baseActivityClass,
                                      int requestCode) {
        startForResult(baseFragment, baseActivityClass, null, requestCode);
    }

    public static void startForResult(BaseActivity baseActivity, Class<? extends BaseActivity> baseActivityClass,
                                      Bundle bundle, int requestCode) {
        Intent intent = new Intent(baseActivity, baseActivityClass);
        if(bundle != null) {
            intent.putExtras(bundle);
        }
        baseActivity.startActivityForResult(intent, requestCode);
    }

    public static void startForResult(BaseFragment baseFragment, Class<? extends BaseActivity> baseActivityClass,
                                      Bundle bundle, int requestCode) {
        Intent intent = new Intent(baseFragment.getContext(), baseActivityClass);
        if(bundle != null) {
            intent.putExtras(bundle);
        }
        baseFragment.startActivityForResult(intent, requestCode);
    }
}
