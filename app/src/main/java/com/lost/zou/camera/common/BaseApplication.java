package com.lost.zou.camera.common;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.lost.zou.camera.R;
import com.lost.zou.camera.common.crashreport.CrashReport;
import com.lost.zou.camera.common.util.DrawUtil;
import com.lost.zou.camera.common.util.imageLoader.AsyncImageLoader;

import de.greenrobot.event.EventBus;


/**
 * application
 */
public class BaseApplication extends Application {

    /**
     * 异步线程，用于处理一般比较短暂的耗时操作，如数据库读写操作等<br>
     */
    protected static final HandlerThread SHORT_TASK_WORKER_THREAD = new HandlerThread("Short-Task-Worker-Thread");

    static {
        SHORT_TASK_WORKER_THREAD.start();
    }

    protected final static Handler SHORT_TASK_HANDLER = new Handler(
            SHORT_TASK_WORKER_THREAD.getLooper());
    protected final static Handler MAIN_LOOPER_HANDLER = new Handler(
            Looper.getMainLooper());

    private static final HandlerThread SHORT_TIME_WORKER_THREAD = new HandlerThread("short worker thread");

    protected final static EventBus GLOBAL_EVENT_BUS = EventBus.getDefault();

    protected static BaseApplication sInstance;
    protected static RequestQueue GLOBAL_REQUESTQUEUE;

    public BaseApplication() {
        sInstance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        DrawUtil.resetDensity(this);
        GLOBAL_REQUESTQUEUE = Volley.newRequestQueue(this);

        // 启动CrashReport
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                CrashReport crashReport = new CrashReport();
                crashReport.start(BaseApplication.this);
            }
        }.start();

        final int memClass = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE))
                .getMemoryClass();
        int cacheSize = 1024 * 1024 * memClass / 8;
        AsyncImageLoader.getInstance().init(this, R.drawable.default_img, cacheSize, Constant.Path.IMAGE_CACHE_DIR);

    }


    /**
     * 获取一个全局的网络请求<br>
     *
     * @return
     */
    public static RequestQueue getGlobalRequestQueue() {
        return GLOBAL_REQUESTQUEUE;
    }


    /**
     * 获取一个全局的EventBus实例<br>
     *
     * @return
     */
    public static EventBus getGlobalEventBus() {
        return GLOBAL_EVENT_BUS;
    }

    /**
     * 使用全局EventBus post一个事件<br>
     *
     * @param event
     */
    public static void postEvent(Object event) {
        GLOBAL_EVENT_BUS.post(event);
    }

    /**
     * 使用全局EventBus post一个Sticky事件<br>
     *
     * @param event
     */
    public static void postStickyEvent(Object event) {
        GLOBAL_EVENT_BUS.postSticky(event);
    }

    /**
     * 注册事件
     *
     * @param object
     */
    public static void globalRegisterEvent(Object object) {
        GLOBAL_EVENT_BUS.register(object);
    }

    /**
     * 反注册事件
     *
     * @param object
     */
    public static void globalUnRegisterEvent(Object object) {
        GLOBAL_EVENT_BUS.unregister(object);
    }

    /**
     * 提交一个Runable到短时任务线程执行<br>
     * <p>
     * <strong>NOTE:</strong>
     * 只充许提交比较短暂的耗时操作，如数据库读写操作等，像网络请求这类可能耗时较长的<i>不能</i>提交，<br>
     * 以免占用线程影响其他的重要数据库操作。
     * </p>
     *
     * @param r
     * @see #postRunOnShortTaskThread(Runnable, long)
     * @see #removeFromShortTaskThread(Runnable)
     */
    public static void postRunOnShortTaskThread(Runnable r) {
        postRunnableByHandler(SHORT_TASK_HANDLER, r);
    }

    /**
     * 提交一个Runable到短时任务线程执行<br>
     * <p>
     * <strong>NOTE:</strong>
     * 只充许提交比较短暂的耗时操作，如数据库读写操作等，像网络请求这类可能耗时较长的<i>不能</i>提交，<br>
     * 以免占用线程影响其他的重要数据库操作。
     * </p>
     *
     * @param r
     * @param delayMillis 延迟指定的毫秒数执行.
     * @see #postRunOnShortTaskThread(Runnable)
     * @see #removeFromShortTaskThread(Runnable)
     */
    public static void postRunOnShortTaskThread(Runnable r, long delayMillis) {
        postRunnableByHandler(SHORT_TASK_HANDLER, r, delayMillis);
    }

    /**
     * 从短时任务线程移除一个先前post进去的Runable<b>
     *
     * @param r
     * @see #postRunOnShortTaskThread(Runnable)
     * @see #postRunOnShortTaskThread(Runnable, long)
     */
    public static void removeFromShortTaskThread(Runnable r) {
        removeRunnableFromHandler(SHORT_TASK_HANDLER, r);
    }

    /**
     * 提交一个Runable到UI线程执行<br>
     *
     * @param r
     * @see #removeFromUiThread(Runnable)
     */
    public static void postRunOnUiThread(Runnable r) {
        postRunnableByHandler(MAIN_LOOPER_HANDLER, r);
    }

    /**
     * 提交一个Runable到UI线程执行<br>
     *
     * @param r
     * @param delayMillis 延迟指定的毫秒数执行.
     * @see #postRunOnUiThread(Runnable)
     * @see #removeFromUiThread(Runnable)
     */
    public static void postRunOnUiThread(Runnable r, long delayMillis) {
        postRunnableByHandler(MAIN_LOOPER_HANDLER, r, delayMillis);
    }

    /**
     * 从UI线程移除一个先前post进去的Runable<b>
     *
     * @param r
     * @see #postRunOnUiThread(Runnable)
     */
    public static void removeFromUiThread(Runnable r) {
        removeRunnableFromHandler(MAIN_LOOPER_HANDLER, r);
    }

    /**
     * 是否运行在UI线程<br>
     *
     * @return
     */
    public static boolean isRunOnUiThread() {
        return Thread.currentThread() == Looper.getMainLooper().getThread();
    }

    private static void postRunnableByHandler(Handler handler, Runnable r) {
        handler.post(r);
    }

    private static void postRunnableByHandler(Handler handler, Runnable r,
                                              long delayMillis) {
        if (delayMillis <= 0) {
            postRunnableByHandler(handler, r);
        } else {
            handler.postDelayed(r, delayMillis);
        }
    }

    private static void removeRunnableFromHandler(Handler handler, Runnable r) {
        handler.removeCallbacks(r);
    }

    public static Context getAppContext() {
        return sInstance;
    }


}
