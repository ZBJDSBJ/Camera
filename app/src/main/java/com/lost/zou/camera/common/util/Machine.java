package com.lost.zou.camera.common.util;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;

/**
 * @zoubo
 */
// CHECKSTYLE:OFF
public class Machine {
    public static int LEPHONE_ICON_SIZE = 72;
    private static boolean sCheckTablet = false;
    private static boolean sIsTablet = false;

    // 硬件加速
    public static int LAYER_TYPE_NONE = 0x00000000;
    public static int LAYER_TYPE_SOFTWARE = 0x00000001;
    public static int LAYER_TYPE_HARDWARE = 0x00000002;
    public static boolean IS_HONEYCOMB = Build.VERSION.SDK_INT >= 11;
    public static boolean IS_HONEYCOMB_MR1 = Build.VERSION.SDK_INT >= 12;
    public static boolean IS_ICS = Build.VERSION.SDK_INT >= 14;
    public static boolean IS_ICS_MR1 = Build.VERSION.SDK_INT >= 15 && Build.VERSION.RELEASE.equals("4.0.4");// HTC oneX 4.0.4系统
    public static boolean sLevelUnder3 = Build.VERSION.SDK_INT < 11;// 版本小于3.0
    private static Method sAcceleratedMethod = null;
    public static boolean s_IS_SDK_ABOVE_KITKAT = Build.VERSION.SDK_INT >= 19;
    public static boolean s_IS_SDK_ABOVE_18 = Build.VERSION.SDK_INT >= 18;
    public static boolean s_IS_SDK_ABOVE_LOLLIPOP = Build.VERSION.SDK_INT >= 21;

    private final static String LEPHONEMODEL[] = {"3GW100", "3GW101", "3GC100", "3GC101"};
    private final static String MEIZUBOARD[] = {"m9", "M9", "mx", "MX"};
    private final static String M9BOARD[] = {"m9", "M9"};
    private final static String ONE_X_MODEL[] = {"HTC One X", "HTC One S"};
    public final static String XIAOMI = "xiaomi";
    public final static String XIAOMI4 = "xiaomi4";
    public final static String BRAND_OPPO = "oppo";
    public static final boolean IS_JELLY_BEAN = Build.VERSION.SDK_INT >= 16;
    public static final String XIAOMI_VERSION_CODE6 = "v6";
    public static final String XIAOMI_VERSION_CODE5 = "v5";

    public static boolean isLephone() {
        final String model = Build.MODEL;
        if (model == null) {
            return false;
        }
        final int size = LEPHONEMODEL.length;
        for (int i = 0; i < size; i++) {
            if (model.equals(LEPHONEMODEL[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * <br>功能简述:获取Android ID的方法
     * <br>功能详细描述:
     * <br>注意:
     *
     * @return
     */
    public static String getAndroidId(Context context) {
        String androidId = null;
        if (context != null) {
            androidId = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        }
        return androidId;
    }

    public static String getVersionCode(Context context) {
        String versionCode = null;
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            versionCode = pInfo.versionCode + "";
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * <br>功能简述:获取versionCode
     * <br>功能详细描述:
     * <br>注意:
     *
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        String versionCode = null;
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            versionCode = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    public static String getIMSI(Context context) {
        String imsi = null;
        try {
            if (context != null) {
                // 从系统服务上获取了当前网络的MCC(移动国家号)，进而确定所处的国家和地区
                TelephonyManager manager = (TelephonyManager) context
                        .getSystemService(Context.TELEPHONY_SERVICE);
                imsi = manager.getSubscriberId();
                imsi = (imsi == null) ? "000" : imsi;
            }
        } catch (Throwable e) {
        }

        return imsi;
    }

    public static String getIMEI(Context context) {
        try {
            return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE))
                    .getDeviceId();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static String getPhoneNumber(Context context) {
        String phone = null;
        try {
            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            phone = tm.getLine1Number();//手机号码
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return phone;
    }

    public static boolean isM9() {
        return isPhone(M9BOARD);
    }

    public static boolean isMeizu() {
        boolean isMeizu = isPhone(MEIZUBOARD);
        final String board = Build.BOARD;
        if (board == null) {
            return false;
        }
        if (board.contains("mx") || board.contains("MX")) {
            isMeizu = true;
        }
        return isMeizu;
    }

    public static boolean isONE_X() {
        return checkModel(ONE_X_MODEL);
    }

    private static boolean isPhone(String[] boards) {
        final String board = Build.BOARD;
        if (board == null) {
            return false;
        }
        final int size = boards.length;
        for (int i = 0; i < size; i++) {
            if (board.equals(boards[i])) {
                return true;
            }
        }
        return false;
    }

    private static boolean checkModel(String[] models) {
        final String board = Build.MODEL;
        if (board == null) {
            return false;
        }
        final int size = models.length;
        for (int i = 0; i < size; i++) {
            if (board.equals(models[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param context
     * @return
     */
    public static boolean isCnUser(Context context) {
        boolean result = false;

        if (context != null) {
            // 从系统服务上获取了当前网络的MCC(移动国家号)，进而确定所处的国家和地区
            TelephonyManager manager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);

            // SIM卡状态
            boolean simCardUnable = manager.getSimState() != TelephonyManager.SIM_STATE_READY;
            String simOperator = manager.getSimOperator();

            if (simCardUnable || TextUtils.isEmpty(simOperator)) {
                // 如果没有SIM卡的话simOperator为null，然后获取本地信息进行判断处理
                // 获取当前国家或地区，如果当前手机设置为简体中文-中国，则使用此方法返回CN
                String curCountry = Locale.getDefault().getCountry();
                if (curCountry != null && curCountry.contains("CN")) {
                    // 如果获取的国家信息是CN，则返回TRUE
                    result = true;
                } else {
                    // 如果获取不到国家信息，或者国家信息不是CN
                    result = false;
                }
            } else if (simOperator.startsWith("460")) {
                // 如果有SIM卡，并且获取到simOperator信息。
                /**
                 * 中国大陆的前5位是(46000) 中国移动：46000、46002 中国联通：46001 中国电信：46003
                 */
                result = true;
            }
        }

        return result;
    }


    public static boolean isHuawei() {
        boolean resault = false;
        String androidVersion = Build.VERSION.RELEASE;// os版本号
        String brand = Build.BRAND;// 商标
        if (androidVersion == null || brand == null) {
            return resault;
        }
        if (brand.equalsIgnoreCase("Huawei")) {
            resault = true;
        }
        return resault;
    }

    // 判断当前设备是否为平板
    private static boolean isPad() {
        if (DrawUtil.sDensity >= 1.5 || DrawUtil.sDensity <= 0) {
            return false;
        }
        if (DrawUtil.sWidthPixels < DrawUtil.sHeightPixels) {
            if (DrawUtil.sWidthPixels > 480 && DrawUtil.sHeightPixels > 800) {
                return true;
            }
        } else {
            if (DrawUtil.sWidthPixels > 800 && DrawUtil.sHeightPixels > 480) {
                return true;
            }
        }
        return false;
    }

    public static boolean isTablet(Context context) {
        if (sCheckTablet == true) {
            return sIsTablet;
        }
        sCheckTablet = true;
        sIsTablet = isPad();
        // if(null == context || DrawUtil.sDensity >= 1.5)
        // {
        // sIsTablet = isPad();
        // }
        // else
        // {
        // sIsTablet = (context.getResources().getConfiguration().screenLayout &
        // Configuration.SCREENLAYOUT_SIZE_MASK) >=
        // Configuration.SCREENLAYOUT_SIZE_LARGE;
        // }
        //
        return sIsTablet;
    }

    /**
     * 判断当前网络是否可以使用
     *
     * @param context
     * @return
     */
    public static boolean isNetworkOK(Context context) {
        boolean result = false;
        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    result = true;
                }
            }
        }

        return result;
    }

    /**
     * 设置硬件加速
     *
     * @param view
     */
    public static void setHardwareAccelerated(View view, int mode) {
        if (sLevelUnder3) {
            return;
        }
        try {
            if (null == sAcceleratedMethod) {
                sAcceleratedMethod = View.class.getMethod("setLayerType", new Class[]{
                        Integer.TYPE, Paint.class});
            }
            sAcceleratedMethod.invoke(view, new Object[]{Integer.valueOf(mode), null});
        } catch (Throwable e) {
            sLevelUnder3 = true;
        }
    }

    public static boolean isIceCreamSandwichOrHigherSdk() {
        return Build.VERSION.SDK_INT >= 14;
    }

    /**
     * 获取Android中的Linux内核版本号
     */
    public static String getLinuxKernel() {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("cat /proc/version");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (null == process) {
            return null;
        }

        // get the output line
        InputStream outs = process.getInputStream();
        InputStreamReader isrout = new InputStreamReader(outs);
        BufferedReader brout = new BufferedReader(isrout, 8 * 1024);
        String result = "";
        String line;

        // get the whole standard output string
        try {
            while ((line = brout.readLine()) != null) {
                result += line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!result.equals("")) {
            String keyword = "version ";
            int index = result.indexOf(keyword);
            line = result.substring(index + keyword.length());
            if (null != line) {
                index = line.indexOf(" ");
                return line.substring(0, index);
            }
        }
        return null;
    }

    /**
     * 获得手机内存的可用空间大小
     */
    public static long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    /**
     * 获得手机内存的总空间大小
     */
    public static long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    /**
     * 获得手机sdcard的可用空间大小
     */
    public static long getAvailableExternalMemorySize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    /**
     * 获得手机sdcard的总空间大小
     */
    public static long getTotalExternalMemorySize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    /**
     * 是否存在SDCard
     *
     * @return
     */
    public static boolean isSDCardExist() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取当前的语言
     *
     * @param context
     * @return
     */
    public static String getLanguage(Context context) {
        String language = context.getResources().getConfiguration().locale.getLanguage();
        return language;
    }

    /**
     * 判断应用软件是否运行在前台
     *
     * @param context
     * @param packageName 应用软件的包名
     * @return
     */
    public static boolean isTopActivity(Context context, String packageName) {
        try {
            ActivityManager activityManager = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
            if (tasksInfo.size() > 0) {
                // 应用程序位于堆栈的顶层
                if (packageName.equals(tasksInfo.get(0).topActivity.getPackageName())) {
                    return true;
                }
            }
        } catch (Exception e) {
        }
        return false;
    }


    /**
     * 判断是否是OPPO
     */
    public static boolean isOPPO() {
        boolean result = false;
        if (Build.VERSION.SDK_INT < 14) {
            return false;    //过滤4.0以下机型
        }
        String brand = Build.BRAND; // 商标
        if (brand == null) {
            return result;
        }
        Log.e("MM", "brand== " + brand);
        if (brand.toLowerCase().contains(BRAND_OPPO)) {
            result = true;
        }
        return result;
    }

    /**
     * 获取SD卡剩余空间
     *
     * @return
     */
    public static long getSDFreeSize() {
        // 取得SD卡文件路径
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        long blockSize = sf.getBlockSize();
        // 空闲的数据块的数量
        long freeBlocks = sf.getAvailableBlocks();
        // 返回SD卡空闲大小
        return freeBlocks * blockSize; // 单位K
    }

    public static String checkNetWordState(Context context) {
        NetworkInfo mobNetInfo = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (mobNetInfo != null) {
            if (mobNetInfo.isAvailable()) {
                int netType = mobNetInfo.getType();
                if (netType == ConnectivityManager.TYPE_WIFI) {
                    return "wifi";
                } else if (netType == ConnectivityManager.TYPE_MOBILE) {
                    if (mobNetInfo.getExtraInfo() != null) {
                        String netMode = mobNetInfo.getExtraInfo().toLowerCase();
                        if (netMode.equals("cmwap")) {
                            return "cmwap";
                        } else {
                            return "cmnet";
                        }
                    }
                }
            }
        }
        return "other";
    }
}
