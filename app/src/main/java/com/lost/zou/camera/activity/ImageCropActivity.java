package com.lost.zou.camera.activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.Toast;

import com.lost.zou.camera.R;
import com.lost.zou.camera.common.Constant;
import com.lost.zou.camera.common.util.FileUtil;
import com.lost.zou.camera.common.util.ImageUtils;
import com.lost.zou.camera.common.view.imagezoomcrop.GOTOConstants;
import com.lost.zou.camera.common.view.imagezoomcrop.InternalStorageContentProvider;
import com.lost.zou.camera.common.view.imagezoomcrop.cropoverlay.CropOverlayView;
import com.lost.zou.camera.common.view.imagezoomcrop.photoview.IGetImageBounds;
import com.lost.zou.camera.common.view.imagezoomcrop.photoview.PhotoView;
import com.lost.zou.camera.common.view.imagezoomcrop.photoview.RotationSeekBar;
import com.nostra13.universalimageloader.utils.L;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.provider.MediaStore.Images;

public class ImageCropActivity extends AppCompatActivity {
    private Context mContext;

    @Bind(R.id.iv_photo)
    PhotoView mPhotoView;
    @Bind(R.id.crop_overlay)
    CropOverlayView mCropOverlayView;

    @Bind(R.id.bar_rotation)
    RotationSeekBar mRotationBar;


    private static final int ANCHOR_CENTER_DELTA = 10;
    private final CompressFormat mOutputFormat = CompressFormat.JPEG;

    //File for capturing camera images
    private File mFileTemp;
    public static final String TEMP_PHOTO_FILE_NAME = "temp_photo.jpg";
    public static final int REQUEST_CODE_PICK_GALLERY = 0x1;
    public static final int REQUEST_CODE_TAKE_PICTURE = 0x2;
    public static final String ERROR_MSG = "error_msg";
    public static final String ERROR = "error";

    //Temp file to save cropped image
    private String mImagePath;
    private Uri mSaveUri = null;
    private Uri mImageUri = null;


    public static void newInstance(Context context, String action) {
        Intent intent = new Intent(context, ImageCropActivity.class);
        intent.putExtra(GOTOConstants.IntentExtras.ACTION_INTENT_EXTRA, action);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_crop);
        ButterKnife.bind(this);

        mContext = this;

        initListener();

        createTempFile();
        if (savedInstanceState == null || !savedInstanceState.getBoolean("restoreState")) {
            String action = getIntent().getStringExtra(GOTOConstants.IntentExtras.ACTION_INTENT_EXTRA);
            if (null != action) {
                switch (action) {
                    case GOTOConstants.IntentExtras.ACTION_CAMERA:
                        getIntent().removeExtra("ACTION");
                        takePic();
                        return;
                    case GOTOConstants.IntentExtras.ACTION_GALLERY:
                        getIntent().removeExtra("ACTION");
                        picAlbum();
                        return;
                }
            }
        }
        mImagePath = mFileTemp.getPath();
        mSaveUri = ImageUtils.getImageUri(mImagePath);
        mImageUri = ImageUtils.getImageUri(mImagePath);

        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    private void init() {
        Bitmap bitmap = ImageUtils.getBitmap(mImageUri, mContext);
        Drawable drawable = new BitmapDrawable(getResources(), bitmap);

        float minScale = mPhotoView.setMinimumScaleToFit(drawable);
        mPhotoView.setMaximumScale(minScale * 3);
        mPhotoView.setMediumScale(minScale * 2);
        mPhotoView.setScale(minScale);
        mPhotoView.setImageDrawable(drawable);
    }

    private void initListener() {
        mPhotoView.setImageBoundsListener(new IGetImageBounds() {
            @Override
            public Rect getImageBounds() {
                return mCropOverlayView.getImageBounds();
            }
        });

        // initialize rotation seek bar
        mRotationBar.setOnSeekBarChangeListener(new RotationSeekBar.OnRotationSeekBarChangeListener(mRotationBar) {

            private float mLastAngle;

            @Override
            public void onRotationProgressChanged(@NonNull RotationSeekBar seekBar, float angle, float delta, boolean fromUser) {
                mLastAngle = angle;
                if (fromUser) {
                    mPhotoView.setRotationBy(delta, false);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                super.onStopTrackingTouch(seekBar);
                if (Math.abs(mLastAngle) < ANCHOR_CENTER_DELTA) {
                    mRotationBar.reset();
                    mPhotoView.setRotationBy(0, true);
                }
            }
        });
    }

    @OnClick(R.id.btn_undo)
    void onRotate() {
        mPhotoView.setRotationBy(0, true);
        mRotationBar.reset();
    }

    @OnClick(R.id.btn_reset)
    void onReset() {
        mPhotoView.reset();
        mRotationBar.reset();
    }

    @OnClick(R.id.btn_cancel)
    void onCancel() {
        finish();
    }

    @OnClick(R.id.btn_done)
    void onDone() {
        saveUploadCroppedImage();
    }

    /**
     * 保存截图，并传递截图 path
     */
    private void saveUploadCroppedImage() {
        boolean saved = saveOutput();
        if (saved) {
            //USUALLY Upload image to server here
            ImageCropResultActivity.newInstance(mContext, mImagePath);
            finish();
        } else {
            Toast.makeText(this, "Unable to save Image into your device.", Toast.LENGTH_LONG).show();
        }
    }

    private boolean saveOutput() {
        Bitmap croppedImage = mPhotoView.getCroppedImage();
        if (mSaveUri != null) {
            OutputStream outputStream = null;
            try {
                outputStream = mContext.getContentResolver().openOutputStream(mSaveUri);
                if (outputStream != null) {
                    croppedImage.compress(mOutputFormat, 90, outputStream);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                return false;
            } finally {
                closeSilently(outputStream);
            }
        } else {
            Log.e("zou", "not defined image url");
            return false;
        }
        croppedImage.recycle();
        return true;
    }

    private void createTempFile() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            FileUtil.createNewFile(Constant.Path.ACCOUNT_CROP_DIR);
            mFileTemp = new File(Constant.Path.ACCOUNT_CROP_DIR, TEMP_PHOTO_FILE_NAME);
        } else {
            mFileTemp = new File(getFilesDir(), TEMP_PHOTO_FILE_NAME);
        }
    }


    private void picAlbum() {
        try {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_CODE_PICK_GALLERY);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            try {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, REQUEST_CODE_PICK_GALLERY);
            } catch (Exception e2) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }
    }

    private void takePic() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            Uri mImageCaptureUri = null;
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                mImageCaptureUri = Uri.fromFile(mFileTemp);
            } else {
                /*
                 * The solution is taken from here: http://stackoverflow.com/questions/10042695/how-to-get-camera-result-as-a-uri-in-data-folder
                 */
                mImageCaptureUri = InternalStorageContentProvider.CONTENT_URI;
            }
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
            takePictureIntent.putExtra("return-data", true);
            startActivityForResult(takePictureIntent, REQUEST_CODE_TAKE_PICTURE);
        } catch (ActivityNotFoundException e) {
            Log.e("zou", "Can't take picture", e);
            Toast.makeText(this, "Can't take picture", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        createTempFile();
        if (requestCode == REQUEST_CODE_TAKE_PICTURE) {
            if (resultCode == RESULT_OK) {
                mImagePath = mFileTemp.getPath();
                mSaveUri = ImageUtils.getImageUri(mImagePath);
                mImageUri = ImageUtils.getImageUri(mImagePath);
                init();
            } else if (resultCode == RESULT_CANCELED) {
                userCancelled();
                return;
            } else {
                errored("Error while opening the image file. Please try again.");
                return;
            }

        } else if (requestCode == REQUEST_CODE_PICK_GALLERY) {
            if (resultCode == RESULT_CANCELED) {
                userCancelled();
                return;
            } else if (resultCode == RESULT_OK) {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(result.getData()); // Got the bitmap .. Copy it to the temp file for cropping
                    FileOutputStream fileOutputStream = new FileOutputStream(mFileTemp);
                    copyStream(inputStream, fileOutputStream);
                    fileOutputStream.close();
                    inputStream.close();
                    mImagePath = mFileTemp.getPath();
                    mSaveUri = ImageUtils.getImageUri(mImagePath);
                    mImageUri = ImageUtils.getImageUri(mImagePath);
                    init();
                } catch (Exception e) {
                    errored("Error while opening the image file. Please try again.");
                    L.e(e);
                    return;
                }
            } else {
                errored("Error while opening the image file. Please try again.");
                return;
            }

        }
    }

    private static void copyStream(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }


    public void closeSilently(Closeable c) {
        if (c == null) return;
        try {
            c.close();
        } catch (Throwable t) {
            // do nothing
        }
    }

    public void userCancelled() {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    public void errored(String msg) {
        Intent intent = new Intent();
        intent.putExtra(ERROR, true);
        if (msg != null) {
            intent.putExtra(ERROR_MSG, msg);
        }
        finish();
    }
}
