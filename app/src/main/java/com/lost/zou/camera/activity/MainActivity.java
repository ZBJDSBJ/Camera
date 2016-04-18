package com.lost.zou.camera.activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.lost.zou.camera.R;
import com.lost.zou.camera.common.Constant;
import com.lost.zou.camera.common.util.CameraUtil;
import com.lost.zou.camera.common.util.FileUtil;
import com.lost.zou.camera.common.util.PublicUtils;
import com.lost.zou.camera.common.view.imagezoomcrop.GOTOConstants;
import com.lost.zou.camera.fragment.PicModeSelectDialogFragment;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);  //依赖注入
        mContext = this;

        FileUtil.createNewFile(Constant.Path.ACCOUNT_DIR);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.tv_take_picture)
    void onTakePicture() {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, CameraActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.tv_take_picture_two)
    void onTakePictureTwo() {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, CameraTwoActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.tv_pick)
    void onPick() {
        startAlbum();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case CameraUtil.START_ALBUM_REQUESTCODE:  //相册选择
                if (resultCode == RESULT_OK && null != data) {
                    String uri = CameraUtil.getFilePath(data.getData(), mContext);
                    Log.i("zou", "mImagePath = " + uri);

                    ProcessPicActivity.newInstance(mContext, uri);
                } else if (resultCode == RESULT_CANCELED) {
                    PublicUtils.showToast("取消选择");
                }
                break;

            default:
                break;
        }
    }

    private void startAlbum() {
        try {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
            intent.setType("image/*");
            startActivityForResult(intent, CameraUtil.START_ALBUM_REQUESTCODE);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            try {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, CameraUtil.START_ALBUM_REQUESTCODE);
            } catch (Exception e2) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }
    }


    @OnClick(R.id.tv_pick2)
    void onPick2() {
        showAddProfilePicDialog();
    }

    private void showAddProfilePicDialog() {
        PicModeSelectDialogFragment dialogFragment = new PicModeSelectDialogFragment();
        dialogFragment.setiPicModeSelectListener(new PicModeSelectDialogFragment.IPicModeSelectListener() {
            @Override
            public void onPicModeSelected(String mode) {
                String action = mode.equalsIgnoreCase(GOTOConstants.PicModes.CAMERA) ? GOTOConstants.IntentExtras.ACTION_CAMERA : GOTOConstants.IntentExtras.ACTION_GALLERY;
                ImageCropActivity.newInstance(mContext, action);
            }
        });
        dialogFragment.show(getFragmentManager(), "picModeSelector");
    }

}
