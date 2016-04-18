package com.lost.zou.camera.common.crashreport;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Debug;

import java.lang.reflect.Method;

/**
 * 内存工具
 *
 */
public class MemoryUtil {
    private Method mMethod = null;
    private ActivityManager mActivityManager = null;
    private int[] mPid = null;

    public MemoryUtil(Context context) {
        try {
            mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            mPid = new int[]{android.os.Process.myPid()};

            Class<?> activityClass = Class.forName("android.app.ActivityManager");
            mMethod = activityClass.getDeclaredMethod("getProcessMemoryInfo", int[].class);
        } catch (Exception e) {
        }
    }

    private Debug.MemoryInfo getMem() {
        Debug.MemoryInfo mem = null;
        if (mMethod != null) {
            try {
                Debug.MemoryInfo[] info = (Debug.MemoryInfo[]) mMethod.invoke(mActivityManager,
                        mPid);
                mem = info[0];
            } catch (Exception e) {

            }
        }
        return mem;
    }

    public String getMemInfos() {
        String memory = null;
        Debug.MemoryInfo info = getMem();
        if (info != null) {
            memory = "{\ndalvikPss:" + info.dalvikPss + " kb" + "  dalvikSharedDirty="
                    + info.dalvikSharedDirty + " kb" + "  dalvikPrivateDirty="
                    + info.dalvikPrivateDirty + " kb" + "\nnativePss=" + info.nativePss + " kb"
                    + "  nativeSharedDirty=" + info.nativeSharedDirty + " kb"
                    + "  nativePrivateDirty=" + info.nativePrivateDirty + " kb" + "\notherPss="
                    + info.otherPss + " kb" + "  otherSharedDirty=" + info.otherSharedDirty + " kb"
                    + "  otherPrivateDirty=" + info.otherPrivateDirty + " kb";

            long totalMemory = Runtime.getRuntime().totalMemory();
            long freeMemory = Runtime.getRuntime().freeMemory();
            long allocMemory = totalMemory - freeMemory;

            memory += "\ndalvik:total = " + (totalMemory / 1024.0f / 1024.0f) + "M";
            memory += "  alloc = " + (allocMemory / 1024.0f / 1024.0f) + "M";
            memory += "  freeM = " + (freeMemory / 1024.0f) + "K";

            final long nhs = Debug.getNativeHeapSize() >> 10;
            final long nhas = Debug.getNativeHeapAllocatedSize() >> 10;
            final long nhfs = Debug.getNativeHeapFreeSize() >> 10;
            memory += "\nnative:Heaps=" + nhs + "  AllocS=" + nhas + "  FreeS=" + nhfs + "\n}";
        }
        return memory;
    }
}
