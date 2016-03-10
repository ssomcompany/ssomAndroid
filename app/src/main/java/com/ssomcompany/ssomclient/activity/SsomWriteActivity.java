package com.ssomcompany.ssomclient.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
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

public class SsomWriteActivity extends AppCompatActivity {
    private static String ssomType = CommonConst.Ssom.SSOM;

    private View ssomTypeSsom;
    private View ssomTypeSsoa;
    private ImageView iconSt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        initWrite();
        initCancel();
        initCamera();
        initSsomType();
    }

    private void initSsomType() {
//        View view = findViewById(R.id.write_ssom);
//        ssomTypeSsom = findViewById(R.id.write_ssom_ssom);
//        ssomTypeSsoseyo = findViewById(R.id.write_ssom_ssoseyo);
//        iconSt = (ImageView) findViewById(R.id.write_icon_st);

//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                toggleSsomType();
//            }
//        });
    }

    private void toggleSsomType() {
        if(CommonConst.Ssom.SSOM.equals(ssomType)){
            ssomType = CommonConst.Ssom.SSOA;
            ssomTypeSsom.setVisibility(View.INVISIBLE);
            ssomTypeSsoa.setVisibility(View.VISIBLE);
            iconSt.setImageResource(R.drawable.icon_wirte_st_r);
        }else{
            ssomType= CommonConst.Ssom.SSOM;
            ssomTypeSsom.setVisibility(View.VISIBLE);
            ssomTypeSsoa.setVisibility(View.INVISIBLE);
            iconSt.setImageResource(R.drawable.icon_wirte_st_b);
        }
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;
    int minAge = 20;
    int maxAge = 21;
    int count = 1;
    private Bitmap imageBitmap;
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void initCamera(){
//        ImageView camera = (ImageView) findViewById(R.id.write_camera);
//        camera.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dispatchTakePictureIntent();
//            }
//        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
//            ImageView mImageView = (ImageView) findViewById(R.id.write_photo);

//            mImageView.setImageDrawable(Util.getCircleBitmap(imageBitmap, 535)); //TODO 535pixel to dp
        }
    }

    int pressImages[] = {R.drawable.icon_rice_press,R.drawable.icon_beer_press,R.drawable.icon_cof_press,R.drawable.icon_all_perss};
    int disImages[] = {R.drawable.icon_rice_dis,R.drawable.icon_beer_dis,R.drawable.icon_cof_dis,R.drawable.icon_all_dis};

    private void initCancel(){
//        ImageView btnCancel = (ImageView) findViewById(R.id.btn_cancel);
//        btnCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), "cancel write", Toast.LENGTH_SHORT).show();
//                onBackPressed();
//            }
//        });
    }
    private void initWrite() {
//        View btnWrite = findViewById(R.id.btn_write_post);
//        btnWrite.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                uploadImage();
//                //creatPost();
//            }
//        });
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
                creatPost(fileId);
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
    private void creatPost(String fileId) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_write, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
