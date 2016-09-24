package com.ssomcompany.ssomclient.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.ssomcompany.ssomclient.R;
import com.ssomcompany.ssomclient.common.CommonConst;
import com.ssomcompany.ssomclient.common.FilterType;
import com.ssomcompany.ssomclient.common.UiUtils;
import com.ssomcompany.ssomclient.common.UniqueIdGenUtil;
import com.ssomcompany.ssomclient.common.Util;
import com.ssomcompany.ssomclient.network.APICaller;
import com.ssomcompany.ssomclient.network.BaseVolleyRequest;
import com.ssomcompany.ssomclient.network.NetworkConstant;
import com.ssomcompany.ssomclient.network.NetworkManager;
import com.ssomcompany.ssomclient.network.NetworkUtil;
import com.ssomcompany.ssomclient.network.api.SsomPostCreate;
import com.ssomcompany.ssomclient.network.model.SsomResponse;
import com.ssomcompany.ssomclient.widget.dialog.CommonDialog;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class SsomWriteActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = SsomWriteActivity.class.getSimpleName();

    private static final int REQUEST_IMAGE_CAPTURE = 10001;
    private static final int REQUEST_IMAGE_CROP = 10002;
    private static final int REQUEST_SELECT_PICTURE = 10003;

    ////// view 영역 //////
    private FrameLayout btnBack;

    private ImageView imgEmpty;
    private ImageView imgShadow;
    private ImageView imgProfile;

    private ImageView imgCamera;

    private TextView tvSsomBalloon;
    private TextView tvSsoaBalloon;

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
    private LinearLayout btnApply;

    // local variables
    private FilterType age = FilterType.twentyEarly;
    private FilterType people = FilterType.onePerson;
    private Location myLocation;
    // camera 변수
    private String mCurrentPhotoPath;
    private Bitmap imageBitmap;
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
        imgCamera = (ImageView) findViewById(R.id.img_camera);

        // category ssom or ssoa
        tvSsomBalloon = (TextView) findViewById(R.id.tv_ssom_balloon);
        tvSsoaBalloon = (TextView) findViewById(R.id.tv_ssoa_balloon);

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
        btnApply = (LinearLayout) findViewById(R.id.btn_apply);

        // text 입력
        tvOurAge.setText(String.format(getResources().getString(R.string.write_select_our_age), age.getTitle()));
        tvOurPeople.setText(String.format(getResources().getString(R.string.write_select_our_people), people.getTitle()));

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
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//
//        startActivityForResult(Intent.createChooser(intent,"Select Picture"), REQUEST_SELECT_PICTURE);
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
                    imgProfile.setImageBitmap(imageBitmap);
                    imgEmpty.setVisibility(View.INVISIBLE);
                    imgShadow.setVisibility(View.INVISIBLE);
                    break;
                case REQUEST_IMAGE_CROP:
//                    Bundle extras = data.getExtras();
//                    Log.d(TAG, "crop finished : " + extras);
//                    if (extras != null) {
                        imgProfile.setImageURI(mContentUri);
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
                        imgProfile.setImageBitmap(BitmapFactory.decodeFile(picturePath));
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

            tvOurAge.setText(String.format(getResources().getString(R.string.write_select_our_age), age.getTitle()));
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

            tvOurPeople.setText(String.format(getResources().getString(R.string.write_select_our_people), people.getTitle()));
        }
    };

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
            moveToGallery();
        } else if(v == tvSsomBalloon) {
            tvSsomBalloon.setSelected(true);
            tvSsoaBalloon.setSelected(false);

            tvSsomBalloon.setTextAppearance(this, R.style.ssom_font_16_white_single);
            tvSsoaBalloon.setTextAppearance(this, R.style.ssom_font_12_white_single);

            tvSsomBalloon.setPadding(0, 0, Util.convertDpToPixel(12), 0);
            tvSsoaBalloon.setPadding(0, 0, Util.convertDpToPixel(9), 0);

            btnApply.setBackgroundResource(R.drawable.btn_write_apply_ssom);
        } else if(v == tvSsoaBalloon) {
            tvSsomBalloon.setSelected(false);
            tvSsoaBalloon.setSelected(true);

            tvSsomBalloon.setTextAppearance(this, R.style.ssom_font_12_white_single);
            tvSsoaBalloon.setTextAppearance(this, R.style.ssom_font_16_white_single);

            tvSsomBalloon.setPadding(0, 0, Util.convertDpToPixel(9), 0);
            tvSsoaBalloon.setPadding(0, 0, Util.convertDpToPixel(12), 0);

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

            uploadImage();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(imageBitmap != null) imageBitmap.recycle();
        if (!TextUtils.isEmpty(mCurrentPhotoPath)) {
            File f = new File(mCurrentPhotoPath);
            f.delete();
            mCurrentPhotoPath = "";
        }
    }

    // TODO upload image and create post
//    private void uploadImage() {
//        if (mContentUri != null) {
//            showProgressDialog();
//            Log.v(TAG, "uri path = " + mContentUri);
//
//            imageBitmap = BitmapFactory.decodeFile(mContentUri.getPath());
//
//            APICaller.ssomImageUpload(makeFileByteArray(), new NetworkManager.NetworkListener<SsomResponse<SsomImageUpload.Response>>() {
//                @Override
//                public void onResponse(SsomResponse<SsomImageUpload.Response> response) {
//                    if (response.isSuccess()) {
//                        SsomImageUpload.Response data = response.getData();
//                        Log.i(TAG, "data : " + data);
//                        if (data != null && data.getFileId() != null && !"".equals(data.getFileId())) {
//                            createPost(data.getFileId());
//                        } else {
//                            // TODO reloading to use app
//                            Log.i(TAG, "data is null !!");
//                        }
//                    } else {
//                        Log.e(TAG, "Response error with code " + response.getResultCode() + ", message : " + response.getMessage(),
//                                response.getError());
//                    }
//                }
//            });
//        } else {
//            Log.e(TAG, "uploadImage() failed!!");
//        }
//    }

//    private byte[] makeFileByteArray() {
//        final String twoHyphens = "--";
//        final String lineEnd = "\r\n";
//        final String boundary = "apiclient-" + System.currentTimeMillis();
//        final int maxBufferSize = 1024 * 1024;
//
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        imageBitmap.compress(Bitmap.CompressFormat.PNG, 1, byteArrayOutputStream);
//
//        byte[] bitmapData = byteArrayOutputStream.toByteArray();
//        int bytesRead, bytesAvailable, bufferSize;
//        byte[] buffer;
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        DataOutputStream dos = new DataOutputStream(bos);
//        try {
//            dos.writeBytes(twoHyphens + boundary + lineEnd);
//            dos.writeBytes("Content-Disposition: form-data; name=\"pict\";filename=\""
//                    + "ssom_upload_from_camera.png" + "\"" + lineEnd);
//            dos.writeBytes(lineEnd);
//            ByteArrayInputStream fileInputStream = new ByteArrayInputStream(bitmapData);
//            bytesAvailable = fileInputStream.available();
//
//            bufferSize = Math.min(bytesAvailable, maxBufferSize);
//            buffer = new byte[bufferSize];
//
//            // read file and write it into form...
//            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
//
//            while (bytesRead > 0) {
//                dos.write(buffer, 0, bufferSize);
//                bytesAvailable = fileInputStream.available();
//                bufferSize = Math.min(bytesAvailable, maxBufferSize);
//                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
//            }
//
//            // send multipart form data necesssary after file data...
//            dos.writeBytes(lineEnd);
//            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
//
//            return bos.toByteArray();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return bitmapData;
//    }

    // TODO image upload 임시.. api 정의 시 변경 필요
    private DataOutputStream dos = null;
    private void uploadImage(){
        final String twoHyphens = "--";
        final String lineEnd = "\r\n";
        final String boundary = "apiclient-" + System.currentTimeMillis();
        final String mimeType = "multipart/form-data;boundary=" + boundary;
        final int maxBufferSize = 1024 * 1024;

        showProgressDialog();
        BaseVolleyRequest baseVolleyRequest = new BaseVolleyRequest(getToken(), Request.Method.POST,
                NetworkConstant.API.IMAGE_FILE_UPLOAD, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String jsonData = new String(response.data);
                Gson gson = new Gson();
                Map<String,String> data = gson.fromJson(jsonData, Map.class);
                String fileId = data.get("fileId");
                createPost(fileId);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());
            }
        }) {
            @Override
            public String getBodyContentType() {
                return mimeType;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
//                imageBitmap = BitmapFactory.decodeFile(mContentUri.getPath());
                imageBitmap = BitmapFactory.decodeFile(picturePath);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 1, byteArrayOutputStream);
                byte[] bitmapData = byteArrayOutputStream.toByteArray();
                int bytesRead, bytesAvailable, bufferSize;
                byte[] buffer;
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                dos = new DataOutputStream(bos);
                try {
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"pict\";filename=\""
                            + "ssom_upload_from_camera.png" + "\"" + lineEnd);
                    dos.writeBytes(lineEnd);
                    ByteArrayInputStream fileInputStream = new ByteArrayInputStream(bitmapData);
                    bytesAvailable = fileInputStream.available();

                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];

                    // read file and write it into form...
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    while (bytesRead > 0) {
                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    }

                    // send multipart form data necesssary after file data...
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    return bos.toByteArray();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return bitmapData;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getApplication());
        queue.add(baseVolleyRequest);
    }

    private void createPost(String fileId) {
        Log.d(TAG, "createPost()");

        showProgressDialog(false);
        APICaller.ssomPostCreate(getToken(), "" + System.currentTimeMillis(), UniqueIdGenUtil.getId(getApplicationContext()), Util.getEncodedString(editWriteContent.getText().toString()),
                NetworkUtil.getSsomHostUrl().concat(NetworkConstant.API.IMAGE_PATH).concat(fileId), age.getValue(), people.getValue(),
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
                        }
                    }
                });
    }
}
