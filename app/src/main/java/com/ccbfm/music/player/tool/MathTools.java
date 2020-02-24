package com.ccbfm.music.player.tool;

public class MathTools {

    public static int calculationIndex(int index, int size){
        if (index > size - 1) {
            index = size - 1;
        } else if(index < 0){
            index = 0;
        }
        return index;
    }
}
