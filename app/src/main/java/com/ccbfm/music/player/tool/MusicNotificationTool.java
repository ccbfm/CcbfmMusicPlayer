package com.ccbfm.music.player.tool;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.ccbfm.music.player.R;
import com.ccbfm.music.player.service.MusicService;
import com.ccbfm.music.player.ui.activity.MusicActivity;

import static android.app.Notification.CATEGORY_MESSAGE;
import static android.app.Notification.DEFAULT_ALL;
import static android.app.Notification.FLAG_ONGOING_EVENT;
import static androidx.core.app.NotificationCompat.PRIORITY_MAX;

/**
 * @author ccbfm
 */
public class MusicNotificationTool {

    public static final int NOTIFY_ID_MUSIC = 0x110;

    private static final String PACKAGE_NAME = "com.ccbfm.music.player.notification";
    public static final String ACTION_PREVIOUS = PACKAGE_NAME + ".music_previous";
    public static final String ACTION_PLAY = PACKAGE_NAME + ".music_play";
    public static final String ACTION_NEXT = PACKAGE_NAME + ".music_next";
    public static final String ACTION_CLOSE = PACKAGE_NAME + ".music_close";

    public static RemoteViews createMusicView(Context context){
        if(context == null){
            throw new NullPointerException("Context 为空！");
        }

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.remote_view_notification_music);

        Intent serviceIntent = new Intent(context, MusicService.class);

        serviceIntent.setAction(ACTION_PREVIOUS);
        PendingIntent previousPendingIntent = PendingIntent.getService(context, 0,
                serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.music_notification_previous, previousPendingIntent);

        serviceIntent.setAction(ACTION_PLAY);
        PendingIntent playPendingIntent = PendingIntent.getService(context, 0,
                serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.music_notification_play, playPendingIntent);

        serviceIntent.setAction(ACTION_NEXT);
        PendingIntent nextPendingIntent = PendingIntent.getService(context, 0,
                serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.music_notification_next, nextPendingIntent);

        serviceIntent.setAction(ACTION_CLOSE);
        PendingIntent closePendingIntent = PendingIntent.getService(context, 0,
                serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.music_notification_close, closePendingIntent);

        return remoteViews;
    }


    public static Notification createNotification(Context context, RemoteViews remoteViews){
        if(context == null){
            throw new NullPointerException("Context 为空！");
        }
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(notificationManager == null){
            return null;
        }
        String channelId = context.getPackageName() + ".music.notification";
        //适配一下高版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = context.getPackageName() + ".music.name";
            NotificationChannel channel = new NotificationChannel(channelId,
                    name,
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(false); //是否在桌面icon展示小红点
            channel.setLightColor(Color.RED); //小红点颜色
            channel.setSound(null, null);//关了通知默认提示音
            channel.setShowBadge(false); //是否在久按桌面图标时显示此渠道的通知
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_music_player)//这玩意在通知栏上显示一个logo
                .setCategory(CATEGORY_MESSAGE)
                .setDefaults(DEFAULT_ALL)
                .setOngoing(true);

        //点击通知栏跳转的activity
        Intent intent = new Intent(context, MusicActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setAutoCancel(false);//点击不让消失
        builder.setSound(null);//关了通知默认提示音
        builder.setPriority(PRIORITY_MAX);//咱们通知很重要
        builder.setVibrate(null);//关了车震
        builder.setContentIntent(pendingIntent);//整个点击跳转activity安排上
        builder.setOnlyAlertOnce(false);
        builder.setContent(remoteViews);//把自定义view放上
        builder.setCustomBigContentView(remoteViews);//把自定义view放上

        Notification notification = builder.build();
        notification.flags |= FLAG_ONGOING_EVENT;
        notification.flags |= Notification.FLAG_NO_CLEAR;//不让手动清除 通知栏常驻
        notification.sound = null;//关了通知默认提示音
        return notification;
    }

    public static void showNotification(Service service,
                                        Notification notification,
                                        RemoteViews remoteViews,
                                        String title, boolean isPlaying){
        if(service == null){
            throw new NullPointerException("Context 为空！");
        }
        if(remoteViews == null){
            return;
        }
        if(!TextUtils.isEmpty(title)) {
            remoteViews.setTextViewText(R.id.music_notification_title, title);
        }
        remoteViews.setInt(R.id.music_notification_play, "setBackgroundResource",
                (isPlaying ? R.drawable.ic_play_to_pause_40dp : R.drawable.ic_pause_to_play_40dp));
        if(notification == null){
            return;
        }
        service.startForeground(NOTIFY_ID_MUSIC, notification);
    }

    public static String buildTitle(String...strings){
        if(strings == null || strings.length == 0){
            return null;
        }
        if(strings.length == 1){
            return strings[0];
        }
        StringBuilder sb = new StringBuilder();
        for (String string : strings) {
            sb.append(string);
        }
        return sb.toString();
    }
}
