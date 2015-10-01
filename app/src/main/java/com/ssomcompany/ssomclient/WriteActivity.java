package com.ssomcompany.ssomclient;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresPermission;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.ssomcompany.ssomclient.network.BaseVolleyRequest;
import com.ssomcompany.ssomclient.network.UniqueIdGenUtil;
import com.ssomcompany.ssomclient.post.RoundImage;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class WriteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        initWrite();
        initCancel();
        initCategory();
        initCamera();

    }
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Bitmap imageBitmap;
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void initCamera(){
        ImageView camera = (ImageView) findViewById(R.id.write_camera);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
    }
    private RoundImage getCircleBitmap(Bitmap bitmap){
        int viewHeight = 532;
        float width = bitmap.getWidth();
        float height = bitmap.getHeight();
        Log.i("kshgizmo","before : "+width + "/ "+height);
        // Calculate image's size by maintain the image's aspect ratio

        float percente = width / 100;
        float scale = viewHeight / percente;
        width *= (scale / 100);
        height *= (scale / 100);

        Log.i("kshgizmo","after : "+width + "/ "+height);
        // Resizing image
        Bitmap bitmapimg = Bitmap.createScaledBitmap(bitmap, (int) width, (int) width, true);

        return new RoundImage(bitmapimg);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            ImageView mImageView = (ImageView) findViewById(R.id.write_photo);

            mImageView.setImageDrawable(getCircleBitmap(imageBitmap));
        }
    }
    private void initCategory(){
        ImageView rice = (ImageView) findViewById(R.id.category_rice);
        ImageView beer = (ImageView) findViewById(R.id.category_beer);
        ImageView coffee = (ImageView) findViewById(R.id.category_coffee);
        ImageView any = (ImageView) findViewById(R.id.category_any);
        rice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectCategory(R.id.category_rice);
            }
        });
        beer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectCategory(R.id.category_beer);
            }
        });
        coffee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectCategory(R.id.category_coffee);
            }
        });
        any.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectCategory(R.id.category_any);
            }
        });


    }
    int ids[] = {R.id.category_rice,R.id.category_beer,R.id.category_coffee,R.id.category_any};
    int pressImages[] = {R.drawable.icon_rice_press,R.drawable.icon_beer_press,R.drawable.icon_cof_press,R.drawable.icon_all_perss};
    int disImages[] = {R.drawable.icon_rice_dis,R.drawable.icon_beer_dis,R.drawable.icon_cof_dis,R.drawable.icon_all_dis};
    private void selectCategory(int categoryId) {
        Toast.makeText(getApplicationContext(),"select "+categoryId,Toast.LENGTH_SHORT).show();
        for (int i=0;i<ids.length;i++) {
            if(categoryId == ids[i]){
                //select
                ImageView selImage = (ImageView) findViewById(ids[i]);
                selImage.setImageResource(pressImages[i]);
            }else{
                //deselect
                ImageView disImage = (ImageView) findViewById(ids[i]);
                disImage.setImageResource(disImages[i]);
            }
        }
    }

    private void initCancel(){
        ImageView btnCancel = (ImageView) findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "cancel write", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        });
    }
    private void initWrite() {
        ImageView btnWrite = (ImageView) findViewById(R.id.btn_write_post);
        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
                //creatPost();
            }
        });
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
                Toast.makeText(WriteActivity.this, "upload complete : "+fileId, Toast.LENGTH_SHORT).show();
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
            String url = "http://54.64.154.188/posts";
            EditText messageBox = (EditText) findViewById(R.id.message);
            final String text = messageBox.getText().toString();
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("postId", "" + System.currentTimeMillis());
            jsonBody.put("userId", UniqueIdGenUtil.getId(getApplicationContext()));
            jsonBody.put("content", text);
            jsonBody.put("imageUrl","http://54.64.154.188/file/images/"+fileId);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    Toast.makeText(getApplicationContext(), "create post : "+jsonObject.toString(), Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Toast.makeText(getApplicationContext(), volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            queue.add(jsonObjectRequest);
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
