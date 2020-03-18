package com.ccbfm.music.player.tool;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ccbfm.android.permission.AndroidPermission;
import com.ccbfm.android.permission.PermissionDeniedHintAdapter;
import com.ccbfm.music.player.R;


public final class AndroidPermissionTool {

    private static Dialog sDialog;

    public static void init() {
        AndroidPermission.init(new PermissionDeniedHintAdapter() {

            @Override
            public void onPermissionDeniedHintHide() {
                if (sDialog != null && sDialog.isShowing()) {
                    sDialog.dismiss();
                    sDialog = null;
                }
            }

            @Override
            public void onPermissionDeniedHintShow(Activity activity, String[] permissions) {
                if (sDialog != null && sDialog.isShowing()) {
                    sDialog.dismiss();
                }
                Dialog dialog = createDialog(activity, permissions);
                dialog.show();
                sDialog = dialog;
            }
        });
    }

    private static Dialog createDialog(final Activity activity, String[] permissions) {
        return DialogTools.buildDialog(activity, "权限", R.string.music_permission_hint, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = getIntent(activity);
                activity.startActivity(intent);
            }
        });
    }

    private static Intent getIntent(Context context) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
        return localIntent;
    }

}
