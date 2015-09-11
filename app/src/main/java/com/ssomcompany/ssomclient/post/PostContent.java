package com.ssomcompany.ssomclient.post;

import com.ssomcompany.ssomclient.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cellz on 2015. 9. 11..
 */
    public class PostContent {

    public static List<PostItem> ITEMS = new ArrayList<PostItem>();
    public static Map<String, PostItem> ITEM_MAP = new HashMap<String, PostItem>();


    static {
        // Add 3 sample items.
        addItem(new PostItem("1", "아 완전 심심하다 집에 가긴 싫고 ㅠㅠ 여자 넷이 있는데 "));
        addItem(new PostItem("2", "Item 2"));
        addItem(new PostItem("3", "Item 3"));
    }

    private static void addItem(PostItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    public static class PostItem {
        public String id;
        public String content;
        public String imgResource;

        public PostItem(String id, String content) {
            this.id = id;
            this.content = content;
        }

        public String getImage() { return imgResource; }
        @Override
        public String toString() {
            return content;
        }
    }
}
