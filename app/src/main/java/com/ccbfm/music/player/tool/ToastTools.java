package com.ccbfm.music.player.tool;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

public final class ToastTools {

    private static Toast sToast;
    private static final Handler HANDLER = new Handler(Looper.getMainLooper());

    public static void showToast(final Context context, final String message){
        if(Looper.myLooper() != Looper.getMainLooper()){
            HANDLER.post(new Runnable() {
                @Override
                public void run() {
                    showToastMain(context, message);
                }
            });
        } else {
            showToastMain(context, message);
        }
    }

    private static void showToastMain(Context context, String message){
        if(context == null || TextUtils.isEmpty(message)){
            return;
        }

        if(sToast != null){
            sToast.cancel();
        }
        sToast = Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_SHORT);
        sToast.show();
    }
}
