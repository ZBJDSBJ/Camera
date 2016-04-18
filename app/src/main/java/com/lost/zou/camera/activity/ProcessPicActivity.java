package com.lost.zou.camera.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.lost.zou.camera.R;
import com.lost.zou.camera.common.util.CameraUtil;
import com.lost.zou.camera.common.view.ClipSquareImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProcessPicActivity extends AppCompatActivity {
//    @Bind(R.id.photo_view_process)
//    PhotoView mProcessPic;

//    @Bind(R.id.CropImageView)
//    CropImageView mCropImageView;

    @Bind(R.id.clipImageView)
    ClipSquareImageView mCropImageView;

    @Bind(R.id.CroppedImageView)
    ImageView mCroppedImageView;

    private String mImagePath;

    public static void newInstance(Context context, String path) {
        Intent intent = new Intent(context, ProcessPicActivity.class);
        intent.putExtra(CameraUtil.IMAGE_PATH_URI, path);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_pic);

        ButterKnife.bind(this);

        mImagePath = getIntent().getStringExtra(CameraUtil.IMAGE_PATH_URI);

        Log.i("zou", "mImagePath = " + mImagePath);
        init();
    }

    private void init() {
//        mProcessPic.enable();
//        mCropImageView.setFixedAspectRatio(true);

        // 有的系统返回的图片是旋转了，有的没有旋转，所以处理
        int degreee = CameraUtil.readBitmapDegree(mImagePath);
        Bitmap bitmap = CameraUtil.createBitmap(mImagePath);
        if (bitmap != null) {
            if (degreee == 0) {
                mCropImageView.setImageBitmap(bitmap);
            } else {
                mCropImageView.setImageBitmap(CameraUtil.rotateBitmap(degreee, bitmap));
            }
        } else {
            finish();
        }

    }

    @OnClick(R.id.btn_cancel)
    void onCancel() {
//        finish();
        mCropImageView.setBorderWeight(1, 1);
    }


    @OnClick(R.id.btn_rotate)
    void onRotate() {
        mCropImageView.rotateView(90);
    }


    @OnClick(R.id.btn_crop)
    void onCrop() {
//        final Bitmap croppedImage = mCropImageView.getCroppedImage();
        Bitmap croppedImage = mCropImageView.clip();
        mCroppedImageView.setImageBitmap(croppedImage);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
