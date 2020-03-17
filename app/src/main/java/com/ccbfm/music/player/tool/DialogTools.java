package com.ccbfm.music.player.tool;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import com.ccbfm.music.player.R;

public final class DialogTools {

    public static Dialog buildDeleteDialog(Context context, int messageId, DialogInterface.OnClickListener positive){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("删除");
        builder.setMessage(messageId);
        builder.setPositiveButton(R.string.music_confirm, positive);
        builder.setNegativeButton(R.string.music_cancel, null);
        return builder.create();
    }
}
