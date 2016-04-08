
package com.ssomcompany.ssomclient.widget.dialog;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;

import com.ssomcompany.ssomclient.R;
import com.ssomcompany.ssomclient.common.Util;

import java.util.ArrayList;

public class CommonDialogChoiceAdapter extends BaseAdapter {

    private final Context context;
    private final boolean isImageVisible;
    private final boolean isMultiMode;
    private int choiceItemPosition;
    private final ArrayList<CharSequence> choiceList;
    private final ArrayList<Boolean> checkList;
    private boolean smallMode = false;

    public CommonDialogChoiceAdapter(Context context, ArrayList<CharSequence> choiceList, boolean isImageVisible, int choiceItemPosition,
                                     boolean isMultiMode) {
        this.context = context;
        this.choiceList = choiceList;
        this.isMultiMode = isMultiMode;
        this.isImageVisible = isImageVisible;
        this.choiceItemPosition = choiceItemPosition;

        checkList = new ArrayList<Boolean>();
        for (int i = 0; i < choiceList.size(); i++) {
            checkList.add(choiceItemPosition == i);
        }
    }

    @Override
    public int getCount() {
        return choiceList.size();
    }

    @Override
    public Object getItem(int position) {
        return choiceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.view_dialog_list_item, null);
        }
        CheckedTextView itemText = (CheckedTextView) convertView;
        if (smallMode) {
            AbsListView.LayoutParams layoutParam = (AbsListView.LayoutParams) itemText.getLayoutParams();
            if (layoutParam == null) {
                layoutParam = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
            }

            layoutParam.height = Util.convertDpToPixel(41);
            itemText.setLayoutParams(layoutParam);
        }

        if (checkList.get(position)) {
            itemText.setTextAppearance(context, R.style.ssom_font_16_green_blue);
        } else {
            itemText.setTextAppearance(context, R.style.ssom_font_16_green_blue);
        }

        if (isImageVisible) {
            int iconRes = 0;
//            if (isMultiMode) {
//                iconRes = R.drawable.checkbox_selector;
//            } else {
//                iconRes = R.drawable.radiobutton_selector;
//            }

            itemText.setCompoundDrawablesWithIntrinsicBounds(iconRes, 0, 0, 0);
        } else {
            itemText.setCheckMarkDrawable(null);
        }

        if (!TextUtils.isEmpty(choiceList.get(position))) {
            itemText.setText(Html.fromHtml(choiceList.get(position).toString()));
        }
        return convertView;
    }

    public int getChoiceItemPosition() {
        return choiceItemPosition;
    }

    public void setChoiceItemPosition(int choiceItemPosition) {
        if (!isMultiMode) {
            if (this.choiceItemPosition >= 0) {
                checkList.set(this.choiceItemPosition, false);
            }
        }

        checkList.set(choiceItemPosition, isMultiMode ? !checkList.get(choiceItemPosition) : true);
        this.choiceItemPosition = choiceItemPosition;

        notifyDataSetChanged();
    }

    public boolean isSmallMode() {
        return smallMode;
    }

    public void setSmallMode(boolean smallMode) {
        this.smallMode = smallMode;
    }
}
