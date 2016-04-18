package com.lost.zou.camera.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.lost.zou.camera.R;
import com.lost.zou.camera.common.view.imagezoomcrop.GOTOConstants;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ImageCropResultActivity extends AppCompatActivity {

    @Bind(R.id.iv_result)
    ImageView mImageView;

    private String mImagePath;


    public static void newInstance(Context context, String path) {
        Intent intent = new Intent(context, ImageCropResultActivity.class);
        intent.putExtra(GOTOConstants.IntentExtras.IMAGE_PATH, path);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_crop_result);
        ButterKnife.bind(this);

        mImagePath = getIntent().getStringExtra(GOTOConstants.IntentExtras.IMAGE_PATH);
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    private void init() {
        showCroppedImage(mImagePath);
    }

    private void showCroppedImage(String mImagePath) {
        if (mImagePath != null) {
            Bitmap myBitmap = BitmapFactory.decodeFile(mImagePath);
            mImageView.setImageBitmap(myBitmap);
        }
    }
}
