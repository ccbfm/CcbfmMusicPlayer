package com.ccbfm.music.player.ui.widget.visualizer;

import android.media.AudioRecord;
import android.media.audiofx.Visualizer;

import com.ccbfm.music.player.tool.LogTools;

import java.util.Arrays;
import java.util.List;

public class VisualizerTools {

    private static final class Singleton {
        private static final VisualizerTools INSTANCE = new VisualizerTools();
    }

    public static VisualizerTools getInstance() {
        return VisualizerTools.Singleton.INSTANCE;
    }

    private VisualizerTools() {

    }

    public static void startVisualizer(int audioSessionId, BaseVisualizer visualizer) {
        if (visualizer != null) {
            visualizer.setAudioSessionId(audioSessionId);
        }
    }

    private Visualizer mVisualizer;
    private List<BaseVisualizer> mVisualizerViews;
    private int mAudioSessionId;

    public void addVisualizer(int audioSessionId, BaseVisualizer... visualizers) {
        if (visualizers == null || audioSessionId <= 0) {
            return;
        }
        if(mAudioSessionId == audioSessionId){
            return;
        }
        mAudioSessionId = audioSessionId;
        try {
            mVisualizerViews = Arrays.asList(visualizers);

            if (mVisualizer != null) {
                mVisualizer.release();
            }
            mVisualizer = new Visualizer(audioSessionId);
            mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);

            mVisualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
                @Override
                public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes,
                                                  int samplingRate) {
                    if (mVisualizerViews != null) {
                        for (BaseVisualizer bv : mVisualizerViews) {
                            bv.setRawAudioBytes(bytes);
                        }
                    }
                }

                @Override
                public void onFftDataCapture(Visualizer visualizer, byte[] bytes,
                                             int samplingRate) {
                }
            }, Visualizer.getMaxCaptureRate() / 2, true, false);
            mVisualizer.setEnabled(true);
        } catch (Exception e){
            LogTools.e("VisualizerTools", "addVisualizer", "Exception ", e);
        }
    }

    public void releaseVisualizer() {
        if (mVisualizer != null) {
            mVisualizer.release();
            mVisualizer = null;
        }
        if (mVisualizerViews != null) {
            mVisualizerViews.clear();
            mVisualizerViews = null;
        }
    }
}
