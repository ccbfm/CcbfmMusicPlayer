package com.ccbfm.music.player.tool;

import android.content.Context;
import android.view.View;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.ccbfm.music.player.App;
import com.ccbfm.music.player.callback.Callback;

import java.lang.ref.WeakReference;
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
public class TimePickerTools {
    private static String sExitTime;

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
                    ToastTools.showToast(context, "无效时间");
                } else {
                    new Thread(new ExitRunnable(delayTime)).start();
                    sExitTime = DateTools.dateToString(date, DateTools.FORMAT_YMDHMS);
                    ToastTools.showToast(context, "设置成功");
                    if (callback != null) {
                        callback.callback();
                    }
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

    private static class ExitRunnable implements Runnable {
        private WeakReference<Context> mReference;
        private long mDelayTime;

        private ExitRunnable(long delayTime) {
            mReference = new WeakReference<>(App.getApp().getApplicationContext());
            mDelayTime = delayTime;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(mDelayTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            LogTools.d("ExitRunnable", "run", "mReference=" + mReference.get());
            if (mReference.get() != null) {
                SystemTools.killAppProcess(mReference.get());
            }
        }
    }

}
