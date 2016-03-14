package com.ssomcompany.ssomclient.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.ssomcompany.ssomclient.network.BaseVolleyRequest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

public class SsomWriteActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = SsomWriteActivity.class.getSimpleName();

    private static String ssomType = CommonConst.SSOM;
    private static final int REQUEST_IMAGE_CAPTURE = 10001;

    private FrameLayout btnBack;

    private ImageView imgProfile;
    private ImageView imgShadow;

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
    private int age = 20;
    private int people = 1;
    private Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        initLayout();
    }

    private void initLayout() {
        //////////// view 객체 생성 ///////////////
        // action bar button
        btnBack = (FrameLayout) findViewById(R.id.btn_back);

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

        ////////// listener 등록 ////////////
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
        tvSsomBalloon.setSelected(true);
        tvSsoaBalloon.setSelected(false);
        tvTwentyEarly.setSelected(true);
        tvOnePeople.setSelected(true);
    }

    // camera app 실행
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");

            //TODO 535pixel to dp
//            ImageView mImageView = (ImageView) findViewById(R.id.write_photo);
//            mImageView.setImageDrawable(Util.getCircleBitmap(imageBitmap, 535));
        }
    }

    private DataOutputStream dos = null;
    private void uploadImage(){
        final String twoHyphens = "--";
        final String lineEnd = "\r\n";
        final String url = "http://54.64.154.188/file/upload";
        final String boundary = "apiclient-" + System.currentTimeMillis();
        final String mimeType = "multipart/form-data;boundary=" + boundary;
        final int maxBufferSize = 1024 * 1024;

        BaseVolleyRequest baseVolleyRequest = new BaseVolleyRequest(Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String jsonData = new String(response.data);
                Gson gson = new Gson();
                Map<String,String> data = gson.fromJson(jsonData,Map.class);
                String fileId = data.get("fileId");
                createPost(fileId);
                Toast.makeText(SsomWriteActivity.this, "upload complete : "+fileId, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("kshgizmo",error.toString());
            }
        }) {
            @Override
            public String getBodyContentType() {
                return mimeType;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
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
        try {
            RequestQueue queue = Volley.newRequestQueue(getApplication());
            //String url = "http://54.64.154.188/posts";
//            String url = NetworkManager.getInstance().getNetworkUrl(NetworkManager.TYPE.POST);

//            EditText messageBox = (EditText) findViewById(R.id.message);
//            final String text = messageBox.getText().toString();
//            JSONObject jsonBody = new JSONObject();
//            jsonBody.put("postId", "" + System.currentTimeMillis());
//            jsonBody.put("userId", UniqueIdGenUtil.getId(getApplicationContext()));
//            jsonBody.put("content", URLEncoder.encode(text,"UTF-8"));
//            jsonBody.put("imageUrl","http://54.64.154.188/file/images/"+fileId);
//            jsonBody.put("minAge",minAge);
//            jsonBody.put("maxAge",maxAge);
//            jsonBody.put("userCount", count);
//            jsonBody.put("ssom", ssomType);
//            Location myLocation = LocationUtil.getLocation(this);
//            if(myLocation!=null) {
//                jsonBody.put("latitude", myLocation.getLatitude());
//                jsonBody.put("longitude", myLocation.getLongitude());
//            }else{
//                Toast.makeText(this,"위치정보를 가지고올수 없어 글을 작성할수 없습니다.",Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
//                @Override
//                public void onResponse(JSONObject jsonObject) {
//                    onBackPressed();
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError volleyError) {
//                    Toast.makeText(getApplicationContext(), "error create post : "+volleyError.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            });
//            queue.add(jsonObjectRequest);
        }catch(Exception e){
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
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
                age = 20;
            } else if(v == tvTwentyMiddle) {
                tvTwentyEarly.setSelected(false);
                tvTwentyMiddle.setSelected(true);
                tvTwentyLate.setSelected(false);
                tvThirtyAll.setSelected(false);
                age = 25;
            } else if(v == tvTwentyLate) {
                tvTwentyEarly.setSelected(false);
                tvTwentyMiddle.setSelected(false);
                tvTwentyLate.setSelected(true);
                tvThirtyAll.setSelected(false);
                age = 29;
            } else if(v == tvThirtyAll) {
                tvTwentyEarly.setSelected(false);
                tvTwentyMiddle.setSelected(false);
                tvTwentyLate.setSelected(false);
                tvThirtyAll.setSelected(true);
                age = 30;
            }
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
                people = 1;
            } else if(v == tvTwoPeople) {
                tvOnePeople.setSelected(false);
                tvTwoPeople.setSelected(true);
                tvThreePeople.setSelected(false);
                tvFourPeopleOrMore.setSelected(false);
                people = 2;
            } else if(v == tvThreePeople) {
                tvOnePeople.setSelected(false);
                tvTwoPeople.setSelected(false);
                tvThreePeople.setSelected(true);
                tvFourPeopleOrMore.setSelected(false);
                people = 3;
            } else if(v == tvFourPeopleOrMore) {
                tvOnePeople.setSelected(false);
                tvTwoPeople.setSelected(false);
                tvThreePeople.setSelected(false);
                tvFourPeopleOrMore.setSelected(true);
                people = 4;
            }
        }
    };

    @Override
    public void onClick(View v) {
        if(v == btnBack || v == btnCancel) {
            finish();
        } else if(v == imgCamera) {
            dispatchTakePictureIntent();
        } else if(v == tvSsomBalloon) {
            tvSsomBalloon.setSelected(true);
            tvSsoaBalloon.setSelected(false);
        } else if(v == tvSsoaBalloon) {
            tvSsomBalloon.setSelected(false);
            tvSsoaBalloon.setSelected(true);
        } else if(v == btnApply) {
            // TODO interaction listener 등록
        }
    }
}
