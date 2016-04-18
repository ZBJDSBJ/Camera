package com.lost.zou.camera.common.util.imageLoader;

import android.graphics.Bitmap;
import android.view.View;

/**
 * Created by Administrator on 2015/8/11.
 * 异步加载图片回调接口
 */
public interface AsyncImageLoaderListener {
    void onLoadingStarted(String arg0, View arg1);

    void onLoadingFailed(String arg0, View arg1);

    void onLoadingComplete(String arg0, View arg1, Bitmap arg2);

    void onLoadingCancelled(String arg0, View arg1);

}
