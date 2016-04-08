package com.ssomcompany.ssomclient.widget.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListAdapter;

import com.ssomcompany.ssomclient.BaseApplication;
import com.ssomcompany.ssomclient.R;

public class CommonDialog extends DialogFragment {

    private static final String KEY_STYLE = "style";
    private static final String TAG = "CommonDialog";
    // Style
    public static final int DIALOG_STYLE_ALERT_BUTTON = 20;
    public static final int DIALOG_STYLE_ALERT_VERTICAL_BUTTON = 21;
    public static final int DIALOG_STYLE_SPINNER = 22;
    public static final int DIALOG_STYLE_SPINNER_TITLE = 23;
    public static final int DIALOG_STYLE_LIST = 24;
    public static final int DIALOG_STYLE_SINGLE_CHOICE = 25;
    public static final int DIALOG_STYLE_MULTI_CHOICE = 26;
    public static final int DIALOG_STYLE_CUSTOM = 30;
    public static final int DIALOG_STYLE_SPINNER_DIM = 31;

    // Basic
    private int styleNum;

    private CharSequence title = null;
    private CharSequence message = null;
    private String positiveName = null;
    private String negativeName = null;
    private String neutralName = null;
    private DialogInterface.OnClickListener positiveListener;
    private DialogInterface.OnClickListener negativeListener;
    private DialogInterface.OnClickListener neutralListener;
    private DialogInterface.OnClickListener itemClickListener;

    // Touch Cancel
    private boolean isTouchCancelable = false;

    // Auto dissmiss Enable
    private boolean autoDissmissEnable = true;

    // Listener
    private OnKeyListener keyListener = null;
    private DialogInterface.OnCancelListener cancelListener;
    private DialogInterface.OnDismissListener dismissListener;

    // CustomView
    private View customView = null;

    // Single, Multi choice
    private ListAdapter listAdapter;
    private CharSequence[] choiceList = null;
    private DialogInterface.OnMultiChoiceClickListener multiListener;
    private int checkItem = -1;
    private int[] checkItems;
    private boolean isShowListIcon = true;

    // ListView Header, Footer
    private View headerView = null;
    private View footerView = null;

    private int dialogLayoutPaddingLeft = -1;
    private int dialogLayoutPaddingRight = -1;

    public static CommonDialog getInstance(int num) {
        CommonDialog mDialogUtil = new CommonDialog();

        Bundle args = new Bundle();
        args.putInt(KEY_STYLE, num);
        mDialogUtil.setArguments(args);

        return mDialogUtil;
    }

    public static CommonDialog getInstance(int num, int opt) {
        // it like newInstance()
        CommonDialog mDialogUtil = new CommonDialog();

        Bundle args = new Bundle();
        args.putInt(KEY_STYLE, num);
        mDialogUtil.setArguments(args);

        return mDialogUtil;
    }

    public void setStyle(int num) {
        Bundle args = getArguments();
        if (args == null) {
            args = new Bundle();
        }
        args.putInt(KEY_STYLE, num);
        setArguments(args);
    }

    public void setTitle(CharSequence title) {
        this.title = title;
    }

    public void setTitle(int id) {
        title = BaseApplication.getInstance().getApplicationContext().getResources().getString(id);
    }

    public void setMessage(CharSequence msg) {
        message = msg;
        if (styleNum == DIALOG_STYLE_SPINNER_TITLE) {
            if (getActivity() == null || getDialog() == null) {
                return;
            }
            ((CustomProgressDialog) getDialog()).setMessage(message);
        }
    }

    public void setMessage(int id) {
        message = BaseApplication.getInstance().getApplicationContext().getResources().getString(id);
        if (styleNum == DIALOG_STYLE_SPINNER_TITLE) {
            if (getActivity() == null || getDialog() == null) {
                return;
            }
            ((CustomProgressDialog) getDialog()).setMessage(message);
        }
    }

    public void setPositiveButton(int posName, DialogInterface.OnClickListener listener) {
        positiveListener = listener;
        positiveName = BaseApplication.getInstance().getApplicationContext().getResources().getString(posName);
    }

    public void setNegativeButton(int negName, DialogInterface.OnClickListener listener) {
        negativeListener = listener;
        negativeName = BaseApplication.getInstance().getApplicationContext().getResources().getString(negName);
    }

    public void setNeutralButton(int netName, DialogInterface.OnClickListener listener) {
        neutralListener = listener;
        neutralName = BaseApplication.getInstance().getApplicationContext().getResources().getString(netName);
    }

    public void setPositiveButton(String posName, DialogInterface.OnClickListener listener) {
        positiveListener = listener;
        positiveName = posName;
    }

    public void setNegativeButton(String negName, DialogInterface.OnClickListener listener) {
        negativeListener = listener;
        negativeName = negName;
    }

    public void setNeutralButton(String netName, DialogInterface.OnClickListener listener) {
        neutralListener = listener;
        neutralName = netName;
    }

    public void setDismissListener(DialogInterface.OnDismissListener dismiss) {
        dismissListener = dismiss;
    }

    public void setCancelListener(DialogInterface.OnCancelListener cancel) {
        cancelListener = cancel;
    }

    public void setKeyListener(DialogInterface.OnKeyListener listener) {
        keyListener = listener;
    }

    public void setCanceledOnTouchOutside(boolean cancelable) {
        isTouchCancelable = cancelable;
    }

    public void setItems(CharSequence[] list, DialogInterface.OnClickListener listener) {
        if (list == null || list.length <= 0) {
            return;
        }

        choiceList = list;
        itemClickListener = listener;
    }

    public void setItems(int itemsId, DialogInterface.OnClickListener listener) {
        choiceList = BaseApplication.getInstance().getApplicationContext().getResources().getTextArray(itemsId);
        itemClickListener = listener;
    }

    public void setListAdapter(ListAdapter adpater, DialogInterface.OnClickListener listener) {
        listAdapter = adpater;
        itemClickListener = listener;
    }

    public void setSingleChoiceItems(CharSequence[] items, int checkedItem, DialogInterface.OnClickListener listener) {
        if (items == null || items.length <= 0) {
            return;
        }
        choiceList = items;
        checkItem = checkedItem;
        itemClickListener = listener;
    }

    public void setSingleChoiceItems(int itemsId, int checkedItem, DialogInterface.OnClickListener listener) {
        choiceList = BaseApplication.getInstance().getApplicationContext().getResources().getTextArray(itemsId);
        checkItem = checkedItem;
        itemClickListener = listener;
    }

    public void setMultiChoiceItems(int itemsId, int[] checkedItems, DialogInterface.OnMultiChoiceClickListener listener) {
        choiceList = BaseApplication.getInstance().getApplicationContext().getResources().getTextArray(itemsId);
        checkItems = checkedItems;
        multiListener = listener;
    }

    public void setMultiChoiceItems(CharSequence[] list, int[] checkedItems, DialogInterface.OnMultiChoiceClickListener listener) {
        if (list == null || list.length <= 0) {
            return;
        }

        choiceList = list;
        checkItems = checkedItems;
        multiListener = listener;
    }

    public void setView(View view) {
        customView = view;
    }

    public void setListHeaderView(View view) {
        headerView = view;
    }

    public void setListFooterView(View view) {
        footerView = view;
    }

    public void setShowListIcon(boolean isShowListIcon) {
        this.isShowListIcon = isShowListIcon;
    }

    public void setPaddingDialogLayout(int left, int right) {
        dialogLayoutPaddingLeft = left;
        dialogLayoutPaddingRight = right;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getActivity() == null) {
            Log.e(TAG, "getActivity is null!!!!");
            return null;
        }
        styleNum = getArguments().getInt(KEY_STYLE);
        Dialog dialog = null;

        switch (styleNum) {
            case DIALOG_STYLE_SPINNER:
            case DIALOG_STYLE_SPINNER_TITLE:
                dialog = new CustomProgressDialog(getActivity(), R.style.DialogTheme);
                dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                ((CustomProgressDialog) dialog).setProgressStyle(DIALOG_STYLE_SPINNER);
                break;
            case DIALOG_STYLE_SPINNER_DIM:
                dialog = new CustomProgressDialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
                dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                ((CustomProgressDialog) dialog).setProgressStyle(DIALOG_STYLE_SPINNER_DIM);
                break;
            case DIALOG_STYLE_ALERT_BUTTON:
                CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
                builder.setMessage(message);
                setButtonEvent(builder);
                dialog = createDialog(builder);
                break;
            case DIALOG_STYLE_ALERT_VERTICAL_BUTTON:
                CustomDialog.Builder vertical = new CustomDialog.Builder(getActivity());
                vertical.setMessage(message);
                vertical.setView(customView);
                vertical.setVerticalButton(true);
                setButtonEvent(vertical);
                dialog = createDialog(vertical);
                break;
            case DIALOG_STYLE_LIST:
                CustomDialog.Builder list = new CustomDialog.Builder(getActivity());
                if (listAdapter != null) {
                    list.setSingleChoiceItems(listAdapter, checkItem, itemClickListener, isShowListIcon);
                }
                setListHeaderFooter(list);
                setButtonEvent(list);
                dialog = createDialog(list);
                break;
            case DIALOG_STYLE_SINGLE_CHOICE:
                CustomDialog.Builder single = new CustomDialog.Builder(getActivity());
                single.setSingleChoiceItems(choiceList, checkItem, itemClickListener, isShowListIcon);
                setListHeaderFooter(single);
                setButtonEvent(single);
                dialog = createDialog(single);
                break;
            case DIALOG_STYLE_MULTI_CHOICE:
                CustomDialog.Builder multi = new CustomDialog.Builder(getActivity());
                multi.setMultiChoiceItems(choiceList, checkItems, multiListener, isShowListIcon);
                setListHeaderFooter(multi);
                setButtonEvent(multi);
                dialog = createDialog(multi);
                break;
            case DIALOG_STYLE_CUSTOM:
                CustomDialog.Builder custom = new CustomDialog.Builder(getActivity());
                custom.setPaddingDialogLayout(dialogLayoutPaddingLeft, dialogLayoutPaddingRight);
                custom.setView(customView);
                setButtonEvent(custom);
                dialog = createDialog(custom);
                break;
            default:
                dialog = new Dialog(getActivity());
                break;
        }

        if (keyListener != null) {
            dialog.setOnKeyListener(keyListener);
        }

        dialog.setCanceledOnTouchOutside(isTouchCancelable);
        return dialog;
    }

    private void setButtonEvent(CustomDialog.Builder builder) {
        if (positiveListener != null && !TextUtils.isEmpty(positiveName)) {
            builder.setPositiveButton(positiveName, positiveListener);
        }

        if (negativeListener != null && !TextUtils.isEmpty(negativeName)) {
            builder.setNegativeButton(negativeName, negativeListener);
        }

        if (neutralListener != null && !TextUtils.isEmpty(neutralName)) {
            builder.setNeutralButton(neutralName, neutralListener);
        }
    }

    private void setListHeaderFooter(CustomDialog.Builder builder) {
        builder.setListHeaderView(headerView);
        builder.setListFooterView(footerView);
    }

    private Dialog createDialog(CustomDialog.Builder builder) {
        builder.setTitle(title);
        builder.setAutoDissmissEnable(autoDissmissEnable);
        return builder.create();
    }

    @Override
    public void onSaveInstanceState(Bundle stateToOut) {
        setShowsDialog(false);
        if (stateToOut != null && stateToOut.isEmpty()) {
            stateToOut.putBoolean("KEY", true);
        }
        super.onSaveInstanceState(stateToOut);
        setShowsDialog(true);
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        if (manager == null) {
            return;
        }

        if (tag != null && manager.findFragmentByTag(tag) != null) {
            Fragment oldFragment = manager.findFragmentByTag(tag);
            FragmentTransaction ft = manager.beginTransaction();
            ft.remove(oldFragment);
            ft.commit();
        }

        try {
            show(manager, tag, true);
        } catch (Exception e) {
            Log.e(TAG, "show error. message = " + e.getMessage());
        }
    }

    @Override
    public void dismiss() {
        try {
            if (getFragmentManager() != null) {
                super.dismissAllowingStateLoss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (getFragmentManager() == null) {
            return;
        }

        if (dismissListener != null) {
            dismissListener.onDismiss(dialog);
        }

        super.onDismiss(dialog);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if (getFragmentManager() == null) {
            return;
        }

        if (cancelListener != null) {
            cancelListener.onCancel(dialog);
        }
    }

    @Override
    public boolean isCancelable() {
        return super.isCancelable();
    }

    @Override
    public void setCancelable(boolean cancelable) {
        super.setCancelable(cancelable);
    }

    public boolean isShowing() {
        boolean result = false;
        if (getDialog() != null && getDialog() instanceof CustomProgressDialog) {
            result = ((CustomProgressDialog) getDialog()).isShowing();
        }
        return result;
    }

    private void show(FragmentManager manager, String tag, boolean allowStateLoss) {
        FragmentTransaction ft = manager.beginTransaction();
        show(ft, tag, allowStateLoss);
    }

    private int show(FragmentTransaction transaction, String tag, boolean allowStateLoss) {
        transaction.remove(this);
        transaction.add(this, tag);
        return allowStateLoss ? transaction.commitAllowingStateLoss() : transaction.commit();
    }

    public boolean isAutoDissmissEnable() {
        return autoDissmissEnable;
    }

    public void setAutoDissmissEnable(boolean isAutoDissmissEnable) {
        this.autoDissmissEnable = isAutoDissmissEnable;
    }
}
