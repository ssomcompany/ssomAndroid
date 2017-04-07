package com.ssomcompany.ssomclient.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.ssomcompany.ssomclient.R;
import com.ssomcompany.ssomclient.common.CommonConst;
import com.ssomcompany.ssomclient.common.FilterType;
import com.ssomcompany.ssomclient.common.SsomPreferences;
import com.ssomcompany.ssomclient.common.UiUtils;
import com.ssomcompany.ssomclient.common.UniqueIdGenUtil;
import com.ssomcompany.ssomclient.common.Util;
import com.ssomcompany.ssomclient.control.SsomPermission;
import com.ssomcompany.ssomclient.control.ViewListener;
import com.ssomcompany.ssomclient.network.APICaller;
import com.ssomcompany.ssomclient.network.NetworkConstant;
import com.ssomcompany.ssomclient.network.NetworkManager;
import com.ssomcompany.ssomclient.network.NetworkUtil;
import com.ssomcompany.ssomclient.network.api.SsomPostCreate;
import com.ssomcompany.ssomclient.network.api.UploadFiles;
import com.ssomcompany.ssomclient.network.model.SsomResponse;
import com.ssomcompany.ssomclient.widget.SsomToast;
import com.ssomcompany.ssomclient.widget.dialog.CommonDialog;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SsomWriteActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = SsomWriteActivity.class.getSimpleName();

    private static final int REQUEST_IMAGE_CAPTURE = 10001;
    private static final int REQUEST_IMAGE_CROP = 10002;
    private static final int REQUEST_SELECT_PICTURE = 10003;
    private static final int REQUEST_CHECK_WRITE_EXTERNAL_STORAGE = 4;

    ////// view 영역 //////
    private FrameLayout btnBack;

    private ImageView imgEmpty;
    private ImageView imgShadow;
    private ImageView imgProfile;

    private FrameLayout imgCamera;

    private TextView tvSsomBalloon;
    private TextView tvSsoaBalloon;

    private TextView tvOneLetter;

    private TextView tvOurAge;
    private TextView tvOurPeople;

    // set select views
    private TextView tvTwentyEarly;
    private TextView tvTwentyMiddle;
    private TextView tvTwentyLate;
    private TextView tvThirtyAll;
    private TextView tvOnePeople;
    private TextView tvTwoPeople;
    private TextView tvThreePeople;
    private TextView tvFourPeopleOrMore;

    private EditText editWriteContent;
    private TextView btnCancel;
    private TextView btnApply;

    // local variables
    private FilterType age = FilterType.twentyEarly;
    private FilterType people = FilterType.onePerson;
    private Location myLocation;
    // camera 변수
    private String mCurrentPhotoPath;
    private Uri mContentUri;
    private String picturePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        setContentView(R.layout.activity_write);

        initLayout();
    }

    private void initLayout() {
        Log.d(TAG, "initLayout()");
        //////////// view 객체 생성 ///////////////
        // action bar button
        btnBack = (FrameLayout) findViewById(R.id.btn_back);

        // set image when camera or gallery sent a image
        imgEmpty = (ImageView) findViewById(R.id.img_empty);

        // delete view when picture loaded
        imgProfile = (ImageView) findViewById(R.id.img_profile);
        imgShadow = (ImageView) findViewById(R.id.img_shadow);

        // camera
        imgCamera = (FrameLayout) findViewById(R.id.image_layout);

        // category ssom or ssoa
        tvSsomBalloon = (TextView) findViewById(R.id.tv_ssom_balloon);
        tvSsoaBalloon = (TextView) findViewById(R.id.tv_ssoa_balloon);

        // one letter
        tvOneLetter = (TextView) findViewById(R.id.tv_one_letter);

        // text view change their value dynamically
        tvOurAge = (TextView) findViewById(R.id.tv_our_age);
        tvOurPeople = (TextView) findViewById(R.id.tv_our_people);

        // view for age settings
        tvTwentyEarly = (TextView) findViewById(R.id.tv_write_age_20_early);
        tvTwentyMiddle = (TextView) findViewById(R.id.tv_write_age_20_middle);
        tvTwentyLate = (TextView) findViewById(R.id.tv_write_age_20_late);
        tvThirtyAll = (TextView) findViewById(R.id.tv_write_age_30_all);

        // view for people settings
        tvOnePeople = (TextView) findViewById(R.id.tv_write_people_1);
        tvTwoPeople = (TextView) findViewById(R.id.tv_write_people_2);
        tvThreePeople = (TextView) findViewById(R.id.tv_write_people_3);
        tvFourPeopleOrMore = (TextView) findViewById(R.id.tv_write_people_4_n_over);

        // content
        editWriteContent = (EditText) findViewById(R.id.edit_write_content);

        // button
        btnCancel = (TextView) findViewById(R.id.btn_cancel);
        btnApply = (TextView) findViewById(R.id.btn_apply);

        // 올사가 있는 경우 로딩함. 없는경우 empty 이미지 보여짐
        if(!TextUtils.isEmpty(getTodayImageUrl())) {
            Glide.with(this).load(getTodayImageUrl()).fitCenter().into(imgProfile);
            imgEmpty.setVisibility(View.INVISIBLE);
            imgShadow.setVisibility(View.INVISIBLE);
        }

        // text 입력
        tvOurAge.setText(getSpannableText(R.string.write_select_our_age, age.getTitle()));
        tvOurPeople.setText(getSpannableText(R.string.write_select_our_people, people.getTitle()));

        ////////// listener 등록 ////////////
        // content line 4줄 max 설정
        editWriteContent.addTextChangedListener(new TextWatcher() {
            String previousString = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                previousString = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (editWriteContent.getLineCount() >= 5) {
                    editWriteContent.setText(previousString);
                    editWriteContent.setSelection(editWriteContent.length());
                    showToastMessageShort(R.string.write_content_max_lines);
                }
            }
        });

        // implements clickListener
        btnBack.setOnClickListener(this);
        imgCamera.setOnClickListener(this);
        tvSsomBalloon.setOnClickListener(this);
        tvSsoaBalloon.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnApply.setOnClickListener(this);

        // listener for age settings
        tvTwentyEarly.setOnClickListener(writeAgeClickListener);
        tvTwentyMiddle.setOnClickListener(writeAgeClickListener);
        tvTwentyLate.setOnClickListener(writeAgeClickListener);
        tvThirtyAll.setOnClickListener(writeAgeClickListener);

        // listener for people settings
        tvOnePeople.setOnClickListener(writePeopleClickListener);
        tvTwoPeople.setOnClickListener(writePeopleClickListener);
        tvThreePeople.setOnClickListener(writePeopleClickListener);
        tvFourPeopleOrMore.setOnClickListener(writePeopleClickListener);

        initSelectView();
    }

    // setting init information
    private void initSelectView() {
        Log.d(TAG, "initSelectView()");
        tvSsomBalloon.setSelected(true);
        tvSsoaBalloon.setSelected(false);
        tvTwentyEarly.setSelected(true);
        tvOnePeople.setSelected(true);
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

    private void cropImage() {
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        // indicate image type and Uri of image
        cropIntent.setDataAndType(mContentUri, "image/*");
        // set crop properties
        cropIntent.putExtra("crop", "true");
        // indicate aspect of desired crop
        cropIntent.putExtra("aspectX", 480);
        cropIntent.putExtra("aspectY", 360);
        // indicate output X and Y
        cropIntent.putExtra("outputX", 480);
        cropIntent.putExtra("outputY", 360);

        // if thumbnail needed below
        cropIntent.putExtra("scale", true);
//        cropIntent.putExtra("scaleUpIfNeeded", true);
//        cropIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
//        cropIntent.putExtra("return-data", false);

        // retrieve data on return
        cropIntent.putExtra("return-data", false);
        cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, mContentUri);
        startActivityForResult(cropIntent, REQUEST_IMAGE_CROP);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_IMAGE_CAPTURE:
//                    cropImage();
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    Glide.with(this).load(imageBitmap).fitCenter().into(imgProfile);
                    imgEmpty.setVisibility(View.INVISIBLE);
                    imgShadow.setVisibility(View.INVISIBLE);
                    break;
                case REQUEST_IMAGE_CROP:
//                    Bundle extras = data.getExtras();
//                    Log.d(TAG, "crop finished : " + extras);
//                    if (extras != null) {
                        Glide.with(this).load(mContentUri).fitCenter().into(imgProfile);
                        imgEmpty.setVisibility(View.INVISIBLE);
                        imgShadow.setVisibility(View.INVISIBLE);
//                    }
                    break;
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
                        imgEmpty.setVisibility(View.INVISIBLE);
                        imgShadow.setVisibility(View.INVISIBLE);

                        picturePath = Util.rotatePhoto(this, picturePath);
                        Glide.with(this).load(picturePath).fitCenter().into(imgProfile);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    View.OnClickListener writeAgeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v == tvTwentyEarly) {
                tvTwentyEarly.setSelected(true);
                tvTwentyMiddle.setSelected(false);
                tvTwentyLate.setSelected(false);
                tvThirtyAll.setSelected(false);
                age = FilterType.twentyEarly;
            } else if(v == tvTwentyMiddle) {
                tvTwentyEarly.setSelected(false);
                tvTwentyMiddle.setSelected(true);
                tvTwentyLate.setSelected(false);
                tvThirtyAll.setSelected(false);
                age = FilterType.twentyMiddle;
            } else if(v == tvTwentyLate) {
                tvTwentyEarly.setSelected(false);
                tvTwentyMiddle.setSelected(false);
                tvTwentyLate.setSelected(true);
                tvThirtyAll.setSelected(false);
                age = FilterType.twentyLate;
            } else if(v == tvThirtyAll) {
                tvTwentyEarly.setSelected(false);
                tvTwentyMiddle.setSelected(false);
                tvTwentyLate.setSelected(false);
                tvThirtyAll.setSelected(true);
                age = FilterType.thirtyOver;
            }

            tvOurAge.setText(getSpannableText(R.string.write_select_our_age, age.getTitle()));
        }
    };

    View.OnClickListener writePeopleClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v == tvOnePeople) {
                tvOnePeople.setSelected(true);
                tvTwoPeople.setSelected(false);
                tvThreePeople.setSelected(false);
                tvFourPeopleOrMore.setSelected(false);
                people = FilterType.onePerson;
            } else if(v == tvTwoPeople) {
                tvOnePeople.setSelected(false);
                tvTwoPeople.setSelected(true);
                tvThreePeople.setSelected(false);
                tvFourPeopleOrMore.setSelected(false);
                people = FilterType.twoPeople;
            } else if(v == tvThreePeople) {
                tvOnePeople.setSelected(false);
                tvTwoPeople.setSelected(false);
                tvThreePeople.setSelected(true);
                tvFourPeopleOrMore.setSelected(false);
                people = FilterType.threePeople;
            } else if(v == tvFourPeopleOrMore) {
                tvOnePeople.setSelected(false);
                tvTwoPeople.setSelected(false);
                tvThreePeople.setSelected(false);
                tvFourPeopleOrMore.setSelected(true);
                people = FilterType.fourPeople;
            }

            tvOurPeople.setText(getSpannableText(R.string.write_select_our_people, people.getTitle()));
        }
    };

    private SpannableStringBuilder getSpannableText(int strRes, String count) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        String firstStr = String.format(getString(strRes), count);
        SpannableString redSpannable= new SpannableString(firstStr);
        redSpannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.red_pink)),
                strRes == R.string.write_select_our_people ? 0 : 7,
                strRes == R.string.write_select_our_people ? count.length() : 7 + count.length() - 1, 0);
        builder.append(redSpannable);
        return builder;
    }

    @Override
    public void onClick(View v) {
        if(v == btnBack || v == btnCancel) {
            if(!TextUtils.isEmpty(editWriteContent.getText())) {
                UiUtils.makeCommonDialog(SsomWriteActivity.this, CommonDialog.DIALOG_STYLE_ALERT_BUTTON,
                        R.string.dialog_notice, 0, R.string.dialog_write_delete_message, 0,
                        R.string.dialog_delete, R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }, null);
            } else {
                finish();
            }
        } else if(v == imgCamera) {
//            moveToCamera();
            SsomPermission.getInstance()
                    .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .setOnPermissionListener(new ViewListener.OnPermissionListener() {
                        @Override
                        public void onPermissionGranted() {
                            moveToGallery();
                        }

                        @Override
                        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                            Log.d(TAG, "denied permission size : " + deniedPermissions.size());

                            // 이 권한을 필요한 이유를 설명해야하는가?
                            if (ActivityCompat.shouldShowRequestPermissionRationale(SsomWriteActivity.this,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                                // 다이어로그같은것을 띄워서 사용자에게 해당 권한이 필요한 이유에 대해 설명합니다
                                // 해당 설명이 끝난뒤 requestPermissions()함수를 호출하여 권한허가를 요청해야 합니다
                                makeDialogForRequestStoragePermission();
                            } else {
                                ActivityCompat.requestPermissions(SsomWriteActivity.this,
                                        deniedPermissions.toArray(new String[]{deniedPermissions.get(0)}),
                                        REQUEST_CHECK_WRITE_EXTERNAL_STORAGE);
                            }
                        }
                    }).checkPermission();
        } else if(v == tvSsomBalloon) {
            tvSsomBalloon.setSelected(true);
            tvSsoaBalloon.setSelected(false);

            tvSsomBalloon.setTextAppearance(this, R.style.ssom_font_16_white_single);
            tvSsoaBalloon.setTextAppearance(this, R.style.ssom_font_12_white_single);

            tvSsomBalloon.setPadding(0, 0, Util.convertDpToPixel(12), 0);
            tvSsoaBalloon.setPadding(0, 0, Util.convertDpToPixel(9), 0);

            tvOneLetter.setText(R.string.detail_category_ssom);
            editWriteContent.setHint(R.string.write_content_hint_ssom);
            btnApply.setBackgroundResource(R.drawable.btn_write_apply_ssom);
        } else if(v == tvSsoaBalloon) {
            tvSsomBalloon.setSelected(false);
            tvSsoaBalloon.setSelected(true);

            tvSsomBalloon.setTextAppearance(this, R.style.ssom_font_12_white_single);
            tvSsoaBalloon.setTextAppearance(this, R.style.ssom_font_16_white_single);

            tvSsomBalloon.setPadding(0, 0, Util.convertDpToPixel(9), 0);
            tvSsoaBalloon.setPadding(0, 0, Util.convertDpToPixel(12), 0);

            tvOneLetter.setText(R.string.detail_category_ssoa);
            editWriteContent.setHint(R.string.write_content_hint_ssoa);
            btnApply.setBackgroundResource(R.drawable.btn_write_apply_ssoa);
        } else if(v == btnApply) {
            if(imgEmpty.getVisibility() == View.VISIBLE) {
                showToastMessageShort(R.string.cannot_write_with_empty_picture);
                return;
            } else if(TextUtils.isEmpty(editWriteContent.getText())) {
                showToastMessageShort(R.string.cannot_write_with_empty_content);
                return;
            }

            myLocation = locationTracker.getLocation();
            if(myLocation == null) {
                showToastMessageShort(R.string.cannot_write_with_unknown_location);
                return;
            }

            if(!TextUtils.isEmpty(picturePath)) {
//                APICaller.ssomImageUpload(this, new Response.Listener<NetworkResponse>() {
//                    @Override
//                    public void onResponse(NetworkResponse response) {
//                        dismissProgressDialog();
//                        if(response.statusCode != 200 || response.data == null) {
//                            showErrorMessage();
//                            return;
//                        }
//
//                        String jsonData = new String(response.data);
//                        Gson gson = new Gson();
//                        Map data = gson.fromJson(jsonData, Map.class);
//
//                        if(data.get(CommonConst.Intent.FILE_ID) == null) {
//                            showErrorMessage();
//                            return;
//                        }
//
//                        String fileId = data.get(CommonConst.Intent.FILE_ID).toString();
//                        final String imageUrl = NetworkUtil.getSsomHostUrl().concat(NetworkConstant.API.IMAGE_PATH).concat(fileId);
//                        getSession().put(SsomPreferences.PREF_SESSION_TODAY_IMAGE_URL, imageUrl);
//                        Bitmap saveBitmap = BitmapFactory.decodeFile(picturePath);
//                        int orientation = Util.getOrientationFromUri(picturePath);
//                        if(orientation != 0) {
//                            Matrix matrix = new Matrix();
//                            matrix.postRotate(orientation);
//                            saveBitmap = Bitmap.createBitmap(saveBitmap, 0, 0, saveBitmap.getWidth(), saveBitmap.getHeight(), matrix, true);
//                        }
//
//                        NetworkManager.getInstance().addBitmapToCache(imageUrl, saveBitmap);
//
//                        createPost(fileId);
//                    }
//                }, picturePath);

                final UploadFiles uploadTask = new UploadFiles() {
                    @Override
                    protected void onPostExecute(String result) {
                        super.onPostExecute(result);
                        Log.d(TAG, "Response from server: " + result);
                        try {
                            Gson gson = new Gson();
                            Map data = gson.fromJson(result, Map.class);
                            if (data.get(CommonConst.Intent.FILE_ID) != null) {
                                String fileId = data.get(CommonConst.Intent.FILE_ID).toString();
                                final String imageUrl = NetworkUtil.getSsomHostUrl().concat(NetworkConstant.API.IMAGE_PATH).concat(fileId);
                                getSession().put(SsomPreferences.PREF_SESSION_TODAY_IMAGE_URL, imageUrl);
                                createPost(fileId);
                            } else {
                                showErrorMessage();
                            }
                        } catch (JsonSyntaxException e) {
                            Log.e(TAG, "error with message : " + e.toString());
                            SsomToast.makeText(SsomWriteActivity.this, "업로드할 수 없습니다.\n문제가 지속될 경우 관리자에게\n문의하시기 바랍니다.");
                            finish();
                        }
                    }
                };

                uploadTask.execute(getToken(), picturePath);
                showProgressDialog(true, new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        uploadTask.cancel(true);
                    }
                });
            } else {
                createPost(null);
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
                        UiUtils.makeToastMessage(getApplicationContext(), "저장소에 접근할 권한이 없어 글을 \n작성할 수 없어요 T.T");
                    }
                });
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
                moveToGallery();
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
            } else {
                onDestroy();
            }
        }
    }

    private void createPost(final String fileId) {
        Log.d(TAG, "createPost()");

        showProgressDialog(false);
        APICaller.ssomPostCreate(getToken(), "" + System.currentTimeMillis(), UniqueIdGenUtil.getId(getApplicationContext()),
                Util.getEncodedString(editWriteContent.getText().toString()),
                TextUtils.isEmpty(fileId) ? getTodayImageUrl() : NetworkUtil.getSsomHostUrl().concat(NetworkConstant.API.IMAGE_PATH).concat(fileId),
                age.getValue(), people.getValue(),
                tvSsomBalloon.isSelected() ? CommonConst.SSOM : CommonConst.SSOA, myLocation.getLatitude(), myLocation.getLongitude(),
                new NetworkManager.NetworkListener<SsomResponse<SsomPostCreate.Response>>() {
                    @Override
                    public void onResponse(SsomResponse<SsomPostCreate.Response> response) {
                        if (response != null) {
                            if (response.isSuccess()) {
                                Log.d(TAG, "success to create post!");
                                setResult(RESULT_OK);
                                finish();
                            } else {
                                Log.d(TAG, "failed to create post!");
                                showErrorMessage();
                            }
                            dismissProgressDialog();
                        }
                    }
                });
    }

    @Override
    protected void onSaveInstanceState(Bundle stateBundle) {
        super.onSaveInstanceState(stateBundle);

        // save file url in bundle as it will be null on screen orientation
        // changes
        stateBundle.putString("file_url", picturePath);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        picturePath = savedInstanceState.getString("file_url");
    }
}
