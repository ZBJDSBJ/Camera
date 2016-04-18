package com.lost.zou.camera.activity;

import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.lost.zou.camera.R;
import com.lost.zou.camera.common.camera.CameraInterface;
import com.lost.zou.camera.common.camera.CameraInterface.CamOpenOverCallback;
import com.lost.zou.camera.common.view.CameraTextureView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CameraTwoActivity extends AppCompatActivity implements CamOpenOverCallback {
    @Bind(R.id.camera_texture_iew)
    CameraTextureView mTextureView;

    float previewRate = -1f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_two);
        ButterKnife.bind(this);  //依赖注入

        init();
    }

    private void init() {
        Thread openThread = new Thread() {
            @Override
            public void run() {
                CameraInterface.getInstance().doOpenCamera(CameraTwoActivity.this);
            }
        };
        openThread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void cameraHasOpened() {
        SurfaceTexture surface = mTextureView.getMySurfaceTexture();
        CameraInterface.getInstance().doStartPreview(surface, previewRate);
    }

    @OnClick(R.id.take_picture)
    void onTakePicture() {
        CameraInterface.getInstance().doTakePicture();
    }

}
