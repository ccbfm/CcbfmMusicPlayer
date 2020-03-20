package com.ccbfm.music.player.tool;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;

import androidx.annotation.NonNull;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bigkoo.pickerview.view.TimePickerView;
import com.ccbfm.music.player.App;
import com.ccbfm.music.player.callback.Callback;
import com.ccbfm.music.player.control.MusicControl;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间选择器的属性大全
 * <p>
 * .setType(new boolean[]{true, true, true, false, false, false}) //年月日时分秒 的显示与否，不设置则默认全部显示
 * .setLabel("年", "月", "日", "", "", "")//默认设置为年月日时分秒
 * .setSubmitText("确定")//确定按钮文字
 * .setCancelText("取消")//取消按钮文字
 * .setTitleText("请选择")//标题
 * .setSubCalSize(18)//确定和取消文字大小
 * .setTitleSize(20)//标题文字大小
 * .setTitleColor(Color.GREEN)//标题文字颜色
 * .setSubmitColor(Color.GREEN)//确定按钮文字颜色
 * .setCancelColor(Color.GREEN)//取消按钮文字颜色
 * .setTitleBgColor(0xFF333333)//标题背景颜色 Night mode
 * .setBgColor(0xFF000000)//滚轮背景颜色 Night mode
 * .setOutSideCancelable(false)//点击屏幕，点在控件外部范围时，是否取消显示
 * .isCyclic(false)//是否循环滚动
 * .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
 * .setDividerColor(Color.YELLOW)//设置分割线的颜色
 * .setTextColorCenter(Color.RED)//设置选中项的颜色
 * .setTextColorOut(Color.BLUE)//设置没有被选中项的颜色
 * .setContentSize(21)//滚轮文字大小
 * .setDate(selectedDate)//// 如果不设置的话，默认是系统时间
 * .setLineSpacingMultiplier(1.2f)//设置两横线之间的间隔倍数
 * .setTextXOffset(-10,0,10,0,0,0)//设置X轴倾斜角度[ -90 , 90°]
 * .setRangDate(startDate,endDate)////起始终止年月日设定
 * .setBackgroundId(0x00FFFFFF) //设置外部遮罩颜色
 * .setDecorView(null)//设置要将pickerview显示到的容器id 必须是viewgroup
 * .isDialog(false)//是否显示为对话框样式
 */
public class MenuFunctionTools {
    private static String sExitTime;
    private static Handler sHandler;

    public static String getExitTime() {
        return sExitTime;
    }

    public static void showExitTimePicker(final Context context, final Callback callback) {
        Calendar startCalendar = Calendar.getInstance();
        final long startTime = startCalendar.getTime().getTime();
        TimePickerBuilder builder = new TimePickerBuilder(context, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                long time = date.getTime();
                long delayTime = time - startTime;
                if (delayTime <= 0) {
                    boolean result = cancelExitMessage();
                    if (result) {
                        sExitTime = "";
                    }
                    ToastTools.showToast(context, result ? "取消定时" : "无效时间");
                } else {
                    sendExitMessage(delayTime);
                    sExitTime = DateTools.dateToString(date, DateTools.FORMAT_YMDHMS);
                    ToastTools.showToast(context, "设置成功");
                }
                if (callback != null) {
                    callback.callback();
                }
            }
        });
        builder.setTitleText("退出时间");
        builder.setType(new boolean[]{true, true, true, true, true, false});
        builder.setLabel("年", "月", "日", "时", "分", "");
        builder.setRangDate(startCalendar, null);
        TimePickerView tpView = builder.build();
        tpView.setDate(Calendar.getInstance());
        tpView.show();
    }

    private static boolean cancelExitMessage() {
        if (sHandler == null) {
            return false;
        }
        boolean result = sHandler.hasMessages(EXECUTE_EXIT);
        if (result) {
            sHandler.removeMessages(EXECUTE_EXIT);
        }
        return result;
    }

    private static void sendExitMessage(long delayTime) {
        if (sHandler == null) {
            sHandler = new ExitHandler(Executors.getHandlerLooper());
        }
        sHandler.removeMessages(EXECUTE_EXIT);
        sHandler.sendEmptyMessageDelayed(EXECUTE_EXIT, delayTime);
    }

    private static final int EXECUTE_EXIT = 1;

    private static class ExitHandler extends Handler {

        private ExitHandler(@NonNull Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == EXECUTE_EXIT) {
                SystemTools.killAppProcess(App.getApp());
            }
        }
    }

    public static final String[] PLAY_MODE_STRING = new String[]{"列表循环", "单曲循环", "随机播放"};

    public static void showPlayModePicker(final Context context, final Callback callback) {
        OptionsPickerBuilder builder = new OptionsPickerBuilder(context, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                SPTools.putIntValue(SPTools.KEY_INIT_PLAY_MODE, options1);
                if (callback != null) {
                    callback.callback();
                }
                MusicControl.getInstance().mode();
                ToastTools.showToast(context, PLAY_MODE_STRING[options1]);
            }
        });
        builder.setTitleText("播放模式");
        builder.isAlphaGradient(true);
        builder.setLineSpacingMultiplier(2);
        OptionsPickerView<String> pickerView = builder.build();
        pickerView.setPicker(Arrays.asList(PLAY_MODE_STRING));
        pickerView.setSelectOptions(SPTools.getIntValue(SPTools.KEY_INIT_PLAY_MODE));
        pickerView.show();
    }

}
