package com.ssomcompany.ssomclient;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.ssomcompany.ssomclient.network.UniqueIdGenUtil;
import com.ssomcompany.ssomclient.post.RoundImage;

import org.json.JSONObject;

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
            Bitmap imageBitmap = (Bitmap) extras.get("data");
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
                Toast.makeText(getApplicationContext(),"cancel write",Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        });
    }
    private void initWrite() {
        ImageView btnWrite = (ImageView) findViewById(R.id.btn_write_post);
        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    RequestQueue queue = Volley.newRequestQueue(getApplication());
                    String url = "http://54.64.154.188/posts";
                    EditText messageBox = (EditText) findViewById(R.id.message);
                    final String text = messageBox.getText().toString();
                    JSONObject jsonBody = new JSONObject();
                    jsonBody.put("postId", "" + System.currentTimeMillis());
                    jsonBody.put("userId", UniqueIdGenUtil.getId(getApplicationContext()));
                    jsonBody.put("content", text);

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            Toast.makeText(getApplicationContext(), jsonObject.toString(), Toast.LENGTH_SHORT).show();
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
        });
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
