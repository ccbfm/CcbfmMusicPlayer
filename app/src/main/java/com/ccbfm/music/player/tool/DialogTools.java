package com.ccbfm.music.player.tool;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import com.ccbfm.music.player.R;

public final class DialogTools {

    public static Dialog buildDeleteDialog(Context context, int messageId, DialogInterface.OnClickListener positive){
        return buildDialog(context, "删除", messageId, positive);
    }

    public static Dialog buildRestoreDialog(Context context, int messageId, DialogInterface.OnClickListener positive){
        return buildDialog(context, "恢复", messageId, positive);
    }

    public static Dialog buildDialog(Context context, String title, int messageId, DialogInterface.OnClickListener positive){
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.PermissionDialog);
        builder.setTitle(title);
        builder.setMessage(messageId);
        builder.setPositiveButton(R.string.music_confirm, positive);
        builder.setNegativeButton(R.string.music_cancel, null);
        return builder.create();
    }

    public static Dialog buildDialog(Context context, String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.PermissionDialog);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.music_confirm, null);
        return builder.create();
    }
}
