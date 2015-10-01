package com.ssomcompany.ssomclient.post;

import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

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

    public static List<PostItem> ITEMS = new ArrayList<>();
    public static Map<String, PostItem> ITEM_MAP = new HashMap<>();

    private static void addItem(PostItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.postId, item);
    }
    public static void init(final PostItemListAdapter adapter){
        ITEMS.clear();
        ITEM_MAP.clear();

        RequestQueue queue = Volley.newRequestQueue(adapter.getContext());
        String url = "http://54.64.154.188/posts";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,url,null,new Response.Listener<JSONArray>(){
            @Override
            public void onResponse(JSONArray jsonArray) {
                try {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = (JSONObject)jsonArray.get(i);
                        Log.i("kshgizmo",obj.toString());
                        PostItem item = new PostItem((String)obj.get("postId"),(String)obj.get("content"));
                        item.imgResource = (String)(obj.get("imageUrl"));
                        PostContent.addItem(item);
                    }
                    adapter.notifyDataSetChanged();
                }catch (Exception e){
                    Toast.makeText(adapter.getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(adapter.getContext(),PostContent.ITEMS.toString(),Toast.LENGTH_SHORT).show();
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(adapter.getContext(),volleyError.getMessage(),Toast.LENGTH_SHORT).show();
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
