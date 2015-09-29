package com.ssomcompany.ssomclient.post;

import android.content.Context;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ssomcompany.ssomclient.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kshgizmo on 2015. 9. 11..
 */
    public class PostContent {

    public static List<PostItem> ITEMS = new ArrayList<PostItem>();
    public static Map<String, PostItem> ITEM_MAP = new HashMap<String, PostItem>();

    private static void addItem(PostItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.postId, item);
    }
    public static void init(final Context context){
        ITEMS.clear();
        ITEM_MAP.clear();

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://54.64.154.188/posts";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,url,null,new Response.Listener<JSONArray>(){
            @Override
            public void onResponse(JSONArray jsonArray) {
                try {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = (JSONObject)jsonArray.get(i);
                        PostItem item = new PostItem((String)obj.get("postId"),(String)obj.get("content"));
                        PostContent.addItem(item);
                    }
                }catch (Exception e){
                    Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(context,PostContent.ITEMS.toString(),Toast.LENGTH_SHORT).show();
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(context,volleyError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(jsonArrayRequest);
    }
    public static class PostItem {
        public String postId;
        public String userId;
        public String content;
        public String imgResource;

        public PostItem(String postId, String content) {
            this.postId = postId;
            this.content = content;
        }

        public String getImage() { return imgResource; }
        @Override
        public String toString() {
            return content;
        }
    }
}
