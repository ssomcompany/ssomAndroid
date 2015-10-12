package com.ssomcompany.ssomclient.post;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.ssomcompany.ssomclient.PostDataChangeInterface;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLDecoder;
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
    public static void init(final Context context,final PostDataChangeInterface callback){
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
                        String content = URLDecoder.decode((String) obj.get("content"),"UTF-8");
                        PostItem item = new PostItem((String)obj.get("postId"),content);
                        item.imgResource = (String)(obj.get("imageUrl"));
                        item.category  = (String) obj.get("category");
                        item.minAge = (int) obj.get("minAge");
                        item.maxAge = (int) obj.get("maxAge");
                        item.userCount = (int) obj.get("userCount");
                        item.userId = (String) obj.get("userId");
                        item.ssom = (String) obj.get("ssom");
                        item.lat = (double) obj.get("latitude");
                        item.lng = (double) obj.get("longitude");
                        PostContent.addItem(item);
                    }
                    if(callback!=null) {
                        callback.onPostItemChanged();
                    }
                }catch (Exception e){
                    Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
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
        public String category;
        public int minAge;
        public int maxAge;
        public int userCount;
        public String ssom;
        public double lat;
        public double lng;


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
