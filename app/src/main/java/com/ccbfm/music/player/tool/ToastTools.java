package com.ccbfm.music.player.tool;

import android.content.Context;
import android.widget.Toast;

public final class ToastTools {

    private static Toast sToast;

    public static void showToast(Context context, String message){
        if(context == null){
            return;
        }
        if(sToast != null){
            sToast.cancel();
        }
        sToast = Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_SHORT);
        sToast.show();
    }
}
