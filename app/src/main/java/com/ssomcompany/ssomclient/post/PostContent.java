package com.ssomcompany.ssomclient.post;

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
        addItem(new PostItem("1", "Item 1"));
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

        public PostItem(String id, String content) {
            this.id = id;
            this.content = content;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
