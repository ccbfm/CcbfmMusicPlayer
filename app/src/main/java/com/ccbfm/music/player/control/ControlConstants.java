package com.ccbfm.music.player.control;

public interface ControlConstants {
    int STATUS_IDLE = 0;
    int STATUS_PAUSE = 1;
    int STATUS_STOP = 2;
    int STATUS_RELEASE = 3;
    int STATUS_SEEK = 4;

    int STATUS_PREVIOUS = 5;
    int STATUS_NEXT = 6;
    int STATUS_MODE = 7;

    int STATUS_PREPARE = 11;
    int STATUS_PLAY = 12;
    int STATUS_SET_LIST = 13;

    //播发模式
    int MODE_SINGLE = 1; //单曲循环
    int MODE_LIST = 2; //列表循环
    int MODE_RANDOM = 3; //随机播发
}
