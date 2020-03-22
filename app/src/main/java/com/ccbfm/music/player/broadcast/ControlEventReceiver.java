package com.ccbfm.music.player.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.KeyEvent;

import com.ccbfm.music.player.control.MusicControl;

/**
 *
 */
public class ControlEventReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (action == null) {
            return;
        }
        MusicControl musicControl = MusicControl.getInstance();
        switch (action) {
            case Intent.ACTION_MEDIA_BUTTON:
                KeyEvent keyEvent = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
                if (keyEvent != null) {
                    int keyAction = keyEvent.getAction();
                    int keyCode = keyEvent.getKeyCode();
                    if (keyAction != KeyEvent.ACTION_UP) {
                        return;
                    }

                    switch (keyCode) {
                        case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                            musicControl.previous();
                            break;
                        case KeyEvent.KEYCODE_MEDIA_NEXT:
                            musicControl.next();
                            break;
                        case KeyEvent.KEYCODE_MEDIA_PLAY:
                            musicControl.play();
                            break;
                        case KeyEvent.KEYCODE_MEDIA_PAUSE:
                            musicControl.pause();
                            break;
                    }
                    break;
                }
                break;
            case Intent.ACTION_HEADSET_PLUG:
                if (intent.hasExtra("state")) {
                    if (intent.getIntExtra("state", 2) == 0) {
                        //拔出
                        if(musicControl.isPlaying()) {
                            musicControl.pause();
                        }
                    } else if (intent.getIntExtra("state", 2) == 1) {
                        //插入
                    }
                }
                break;
        }
    }

    public static ControlEventReceiver registerReceiver(Context context) {
        ControlEventReceiver receiver = new ControlEventReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_BUTTON);
        filter.addAction(Intent.ACTION_HEADSET_PLUG);
        context.registerReceiver(receiver, filter);
        return receiver;
    }

    public static void unregisterReceiver(Context context,
                                        ControlEventReceiver receiver) {
        context.unregisterReceiver(receiver);
    }
}
