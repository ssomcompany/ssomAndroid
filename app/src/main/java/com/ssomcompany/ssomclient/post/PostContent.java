package com.ssomcompany.ssomclient.post;

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


    static {
        // Add 3 sample items.
        addItem(new PostItem("1", "아 완전 심심하다 집에 가긴 싫고 ㅠㅠ 여자 넷이 있는데 놀아줄 사람 진짜 없는거니??? ㅋㅋㅋ 우리 빙수만 먹다 집에 갈듯"));
        addItem(new PostItem("2", "완전 맛있는 족발집에서 소주 한잔 하실분 모십니다. 매너 좋은 남자들 있구요! 편하게 얘기 하실분 대 환영입니다."));
        addItem(new PostItem("3", "달다구리 \n 먹고시퐁..."));
    }

    private static void addItem(PostItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }
    public static void init(){
        ITEMS.clear();
        ITEM_MAP.clear();
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
