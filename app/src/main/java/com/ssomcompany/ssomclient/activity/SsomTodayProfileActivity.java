package com.ssomcompany.ssomclient.activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ssomcompany.ssomclient.R;
import com.ssomcompany.ssomclient.common.CommonConst;
import com.ssomcompany.ssomclient.common.SsomPreferences;
import com.ssomcompany.ssomclient.common.UiUtils;
import com.ssomcompany.ssomclient.common.Util;
import com.ssomcompany.ssomclient.control.SsomPermission;
import com.ssomcompany.ssomclient.control.ViewListener;
import com.ssomcompany.ssomclient.network.APICaller;
import com.ssomcompany.ssomclient.network.NetworkConstant;
import com.ssomcompany.ssomclient.network.NetworkManager;
import com.ssomcompany.ssomclient.network.NetworkUtil;
import com.ssomcompany.ssomclient.network.api.UploadFiles;
import com.ssomcompany.ssomclient.network.model.BaseResponse;
import com.ssomcompany.ssomclient.widget.CircularNetworkImageView;
import com.ssomcompany.ssomclient.widget.dialog.CommonDialog;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SsomTodayProfileActivity extends BaseActivity implements View.OnClickListener {
    private static final int REQUEST_SELECT_PICTURE = 10001;
    private static final int REQUEST_IMAGE_CAPTURE = 10002;
    private static final int REQUEST_CHECK_WRITE_EXTERNAL_STORAGE = 1;

    CircularNetworkImageView profileImage;

    // camera 변수
    private String mCurrentPhotoPath;
    private Uri mContentUri;
    private String picturePath;

    enum ButtonType {
        GALLERY, CAMERA;
    }

    ButtonType currentType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileImage = (CircularNetworkImageView) findViewById(R.id.profile_img);
        ((TextView) findViewById(R.id.explanation_profile)).setText(
                Html.fromHtml(getString(R.string.today_photo_will_be_delete_in_the_morning)));

        findViewById(R.id.btn_close).setOnClickListener(this);
        findViewById(R.id.btn_load_image).setOnClickListener(this);
        findViewById(R.id.btn_take_picture).setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);
        findViewById(R.id.btn_save).setOnClickListener(this);

        profileImage.setImageUrl(getTodayImageUrl() + "?thumbnail=200", NetworkManager.getInstance().getImageLoader());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_close:
            case R.id.btn_cancel:
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.btn_load_image:
            case R.id.btn_take_picture:
                if(v.getId() == R.id.btn_load_image) {
                    currentType = ButtonType.GALLERY;
                } else {
                    currentType = ButtonType.CAMERA;
                }

                SsomPermission.getInstance()
                        .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .setOnPermissionListener(new ViewListener.OnPermissionListener() {
                            @Override
                            public void onPermissionGranted() {
                                if(currentType == ButtonType.GALLERY) {
                                    moveToGallery();
                                } else {
                                    moveToCamera();
                                }
                            }

                            @Override
                            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                                Log.d(TAG, "denied permission size : " + deniedPermissions.size());

                                // 이 권한을 필요한 이유를 설명해야하는가?
                                if (ActivityCompat.shouldShowRequestPermissionRationale(SsomTodayProfileActivity.this,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                                    // 다이어로그같은것을 띄워서 사용자에게 해당 권한이 필요한 이유에 대해 설명합니다
                                    // 해당 설명이 끝난뒤 requestPermissions()함수를 호출하여 권한허가를 요청해야 합니다
                                    makeDialogForRequestStoragePermission();
                                } else {
                                    ActivityCompat.requestPermissions(SsomTodayProfileActivity.this,
                                            deniedPermissions.toArray(new String[]{deniedPermissions.get(0)}),
                                            REQUEST_CHECK_WRITE_EXTERNAL_STORAGE);
                                }
                            }
                        }).checkPermission();
                break;
            case R.id.btn_save:
                if(TextUtils.isEmpty(picturePath) && TextUtils.isEmpty(mCurrentPhotoPath)) {
                    showToastMessageShort(R.string.cannot_write_with_empty_picture);
                    return;
                }

                final UploadFiles uploadTask = new UploadFiles() {
                    @Override
                    protected void onPostExecute(String result) {
                        super.onPostExecute(result);
                        Log.d(TAG, "Response from server: " + result);
                        Gson gson = new Gson();
                        Map data = gson.fromJson(result, Map.class);
                        if(data.get(CommonConst.Intent.FILE_ID) != null) {
                            String fileId = data.get(CommonConst.Intent.FILE_ID).toString();
                            final String imageUrl = NetworkUtil.getSsomHostUrl().concat(NetworkConstant.API.IMAGE_PATH).concat(fileId);
                            getSession().put(SsomPreferences.PREF_SESSION_TODAY_IMAGE_URL, imageUrl);
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            showErrorMessage();
                        }
                        dismissProgressDialog();
                    }
                };

                uploadTask.execute(getToken(), currentType == ButtonType.GALLERY ? picturePath : mCurrentPhotoPath);
                showProgressDialog(true, new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        uploadTask.cancel(true);
                    }
                });
                break;
        }
    }

    // gallery 실행
    private void moveToGallery() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, REQUEST_SELECT_PICTURE);
    }

    // camera app 실행
    private void moveToCamera() {
        Log.d(TAG, "moveToCamera()");
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                mContentUri = Uri.fromFile(photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.KOREA).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        );

        mCurrentPhotoPath = image.getAbsolutePath();

        return image;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CHECK_WRITE_EXTERNAL_STORAGE) {
            Map<String, Integer> permissionMap = new HashMap<>();

            for (int i = 0 ; i < permissions.length ; i++) {
                permissionMap.put(permissions[i], grantResults[i]);
            }

            // 거절을 클릭 한 경우에 해당함
            if(grantResults.length > 0 &&
                    permissionMap.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                makeDialogForRequestStoragePermission();
            } else {
                if(currentType == ButtonType.GALLERY) {
                    moveToGallery();
                } else {
                    moveToCamera();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_SELECT_PICTURE:
                    Log.d(TAG, "data : " + data);
                    if(data != null) {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = { MediaStore.Images.Media.DATA };
                        Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
                        Log.d(TAG, "cursor : " + cursor);
                        if(cursor != null) {
                            cursor.moveToFirst();
                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            picturePath = cursor.getString(columnIndex);
                            cursor.close();
                        }
                        Log.d(TAG, "path : " + picturePath);

                        picturePath = Util.rotatePhoto(this, picturePath);
                        profileImage.setLocalImageBitmap(BitmapFactory.decodeFile(picturePath));
                    }
                    break;
                case REQUEST_IMAGE_CAPTURE:
                    mContentUri = Uri.fromFile(new File(Util.rotatePhoto(this, mCurrentPhotoPath)));
                    File file = new File(mCurrentPhotoPath);
                    if(file.delete()) mCurrentPhotoPath = mContentUri.getPath();
                    profileImage.setLocalImageBitmap(BitmapFactory.decodeFile(mCurrentPhotoPath));
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (!TextUtils.isEmpty(mCurrentPhotoPath)) {
            File f = new File(mCurrentPhotoPath);
            if(f.delete()) {
                mCurrentPhotoPath = "";
                picturePath = "";
            } else {
                onDestroy();
            }
        }
    }

    private void makeDialogForRequestStoragePermission() {
        UiUtils.makeCommonDialog(this, CommonDialog.DIALOG_STYLE_ALERT_BUTTON, R.string.dialog_notice, 0,
                R.string.dialog_explain_storage_permission_message, R.style.ssom_font_16_custom_666666,
                R.string.dialog_move, R.string.dialog_close,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent();
                        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        i.addCategory(Intent.CATEGORY_DEFAULT);
                        i.setData(Uri.parse("package:" + getPackageName()));
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        startActivityForResult(i, REQUEST_CHECK_WRITE_EXTERNAL_STORAGE);
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UiUtils.makeToastMessage(getApplicationContext(), "저장소에 접근할 권한이 없어 오늘의 사진을 변경할 수 없어요 T.T");
                    }
                });
    }
}
