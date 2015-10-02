package com.ssomcompany.ssomclient.common;

import com.ssomcompany.ssomclient.R;

/**
 * Created by kshgizmo on 2015-10-03.
 */
public class CategoryUtil {
    public static int getCategoryIconId(String category){
        switch (category){
            case "rice":
                return R.drawable.icon_rice_st_b;
            case "beer":
                return R.drawable.icon_beer_st_b;
            case "coffee":
                return R.drawable.icon_cof_st_b;
            case "any" :
            default:
                return -1;
        }
    }
    public static String getCategoryDescription(String category){
        switch (category){
            case "rice":
                return "밥";
            case "beer":
                return "술";
            case "coffee":
                return "차";
            case "any" :
                return "아무거나";
            default:
                return "";
        }
    }
}
