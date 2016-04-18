package com.lost.zou.camera.common.util;

import android.app.ActivityManager;
import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import com.lost.zou.camera.common.BaseApplication;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zoubo
 */
public class PublicUtils {
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);


    public static final void showToast(Context context, String textDesc) {
        Toast.makeText(context, textDesc, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(String showString) {
        Toast toast = Toast.makeText(BaseApplication.getAppContext(), showString + "", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    /**
     * 根据资源的名字获取它的ID
     *
     * @param name    要获取的资源的名字
     * @param defType 资源的类型，如drawable, string
     * @return 资源的id
     */
    public static int getResId(Context context, String name, String defType) {
        String packageName = context.getApplicationInfo().packageName;
        return context.getResources().getIdentifier(name, defType, packageName);
    }

    public static boolean isServiceWorked(Context context, String serviceName) {
        ActivityManager myManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager.getRunningServices(Integer.MAX_VALUE);
        for (int i = 0; i < runningService.size(); i++) {
            if (runningService.get(i).service.getClassName().toString().equals(serviceName)) {
                return true;
            }
        }
        return false;
    }


    public static int generateViewId() {
        for (; ; ) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    /**
     * 去除空格
     */
    public static String removeAllSpace(String str) {
        String tmpStr = str.replace(" ", "");
        return tmpStr;
    }

    /***
     * 转换毫秒为时间格式
     *
     * @param secondTime
     * @return
     */
    public static String transTime(long secondTime) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");//初始化Formatter的转换格式。
        String hms = formatter.format(secondTime - 8 * 60 * 60 * 1000);
        return hms;
    }

}
