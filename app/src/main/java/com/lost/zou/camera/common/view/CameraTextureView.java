package com.lost.zou.camera.common.view;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;

import com.lost.zou.camera.common.camera.CameraInterface;

/**
 * Created by zoubo on 16/3/18.
 * 用TextureView预览Camera
 */
public class CameraTextureView extends TextureView implements TextureView.SurfaceTextureListener {

    private Context mContext;
    private SurfaceTexture mSurface;  //用Textureview预览的话需要传进去一个SurfaceTexture

    public CameraTextureView(Context context) {
        this(context, null);
    }

    public CameraTextureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context);
    }

    private void init(Context context) {
        mContext = context;
        this.setSurfaceTextureListener(this);
    }


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mSurface = surface;
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        Log.i("zou", "onSurfaceTextureSizeChanged...");

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        CameraInterface.getInstance().doStopCamera();

        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        Log.i("zou", "onSurfaceTextureUpdated...");
    }

    public SurfaceTexture getMySurfaceTexture() {
        return mSurface;
    }
}
