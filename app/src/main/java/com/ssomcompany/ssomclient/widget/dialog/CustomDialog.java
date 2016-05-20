package com.ssomcompany.ssomclient.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ssomcompany.ssomclient.R;

import java.util.ArrayList;
import java.util.Collections;

public class CustomDialog extends Dialog {

    private CustomDialog(Context context) {
        super(context);
    }

    private CustomDialog(Context context, int theme) {
        super(context, theme);
    }

    private enum ListState {
        NONE, SINGLE, MULTI;
    }

    public static class Builder {
        private final Context context;
        private CharSequence title = null;
        private CharSequence message = null;
        private String posBtnTxt = null;
        private String negBtnTxt = null;
        private String neuBtnTxt = null;
        private int titleStyle = 0;
        private int posBtnResId = 0;
        private int negBtnResId = 0;
        private int neuBtnResId = 0;
        private View contentView;
        private View headerView;
        private View footerView;
        private ViewGroup dialogLayoutView;
        private CharSequence[] items = null;

        private OnClickListener posBtnClickListener;
        private OnClickListener negBtnClickListener;
        private OnClickListener neuBtnClickListener;
        private OnClickListener itemClickListener;
        private OnMultiChoiceClickListener multiListener;
        private OnKeyListener keyListener;
        private OnCancelListener cancelListener;
        private OnDismissListener dismissListener;
        private ListState state = ListState.NONE;
        private ListAdapter itemAdapter;
        private CommonDialogChoiceAdapter choiceAdapter;

        private boolean verticalButton = false;
        private boolean showListIcon = true;
        private boolean cancelable = true;
        private boolean touchCancelable = false;
        private boolean autoDismissEnable = true;
        private int singleChoiceInitCheckItem = -1;
        private int[] multiChoiceInitCheckItems;
        private int dialogLayoutPaddingLeft = -1;
        private int dialogLayoutPaddingRight = -1;
        private static final String TAG = "CustomDialog";

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setTitle(int title) {
            this.title = context.getText(title);
            return this;
        }

        public Builder setTitle(CharSequence title) {
            this.title = title;
            return this;
        }

        public Builder setTitleStyle(int titleStyle) {
            this.titleStyle = titleStyle;
            return this;
        }

        public Builder setMessage(CharSequence message) {
            this.message = message;
            return this;
        }

        public Builder setMessage(int message) {
            this.message = context.getText(message);
            return this;
        }

        public Builder setPositiveButton(int positiveButtonText, OnClickListener listener) {
            this.posBtnTxt = (String) context.getText(positiveButtonText);
            this.posBtnClickListener = listener;
            return this;
        }

        public Builder setPositiveButton(String positiveButtonText, OnClickListener listener) {
            this.posBtnTxt = positiveButtonText;
            this.posBtnClickListener = listener;
            return this;
        }

        public Builder setPositiveButton(String positiveButtonText, int positiveButtonResId, OnClickListener listener) {
            this.posBtnTxt = positiveButtonText;
            this.posBtnResId = positiveButtonResId;
            this.posBtnClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(int negativeButtonText, OnClickListener listener) {
            this.negBtnTxt = (String) context.getText(negativeButtonText);
            this.negBtnClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(String negativeButtonText, OnClickListener listener) {
            this.negBtnTxt = negativeButtonText;
            this.negBtnClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(String negativeButtonText, int negativeButtonResId, OnClickListener listener) {
            this.negBtnTxt = negativeButtonText;
            this.negBtnResId = negativeButtonResId;
            this.negBtnClickListener = listener;
            return this;
        }

        public Builder setNeutralButton(int negativeButtonText, OnClickListener listener) {
            this.neuBtnTxt = (String) context.getText(negativeButtonText);
            this.neuBtnClickListener = listener;
            return this;
        }

        public Builder setNeutralButton(String neutralButtonText, OnClickListener listener) {
            this.neuBtnTxt = neutralButtonText;
            this.neuBtnClickListener = listener;
            return this;
        }

        public Builder setNeutralButton(String neutralButtonText, int neutralButtonResId, OnClickListener listener) {
            this.neuBtnTxt = neutralButtonText;
            this.neuBtnResId = neutralButtonResId;
            this.neuBtnClickListener = listener;
            return this;
        }

        public Builder setView(View v) {
            this.contentView = v;
            return this;
        }

        public Builder setItems(CharSequence[] items, OnClickListener listener, boolean isShowListIcon) {
            if (items == null || items.length <= 0) {
                return this;
            }

            this.items = items;
            this.itemClickListener = listener;
            this.state = ListState.SINGLE;
            this.showListIcon = isShowListIcon;
            return this;
        }

        public Builder setItems(int itemsId, OnClickListener listener, boolean isShowListIcon) {
            if (context == null || context.getResources() == null) {
                Log.e(TAG, "getResources() is null!!!!");
                return null;
            }

            this.items = context.getResources().getTextArray(itemsId);
            this.itemClickListener = listener;
            this.state = ListState.SINGLE;
            this.showListIcon = isShowListIcon;
            return this;
        }

        public Builder setSingleChoiceItems(ListAdapter adpater, int checkedItem, OnClickListener listener, boolean isShowListIcon) {
            this.itemAdapter = adpater;
            this.itemClickListener = listener;
            this.singleChoiceInitCheckItem = checkedItem;
            this.state = ListState.SINGLE;
            this.showListIcon = isShowListIcon;
            return this;
        }

        public Builder setSingleChoiceItems(CharSequence[] items, int checkedItem, OnClickListener listener, boolean isShowListIcon) {
            if (items == null || items.length <= 0) {
                return this;
            }

            this.items = items;
            this.itemClickListener = listener;
            this.singleChoiceInitCheckItem = checkedItem;
            this.state = ListState.SINGLE;
            this.showListIcon = isShowListIcon;
            return this;
        }

        public Builder setSingleChoiceItems(int itemsId, int checkedItem, OnClickListener listener, boolean isShowListIcon) {
            if (context == null || context.getResources() == null) {
                Log.e(TAG, "getResources() is null!!!!");
                return null;
            }

            this.items = context.getResources().getTextArray(itemsId);
            this.itemClickListener = listener;
            this.singleChoiceInitCheckItem = checkedItem;
            this.state = ListState.SINGLE;
            this.showListIcon = isShowListIcon;
            return this;
        }

        public Builder setMultiChoiceItems(ListAdapter adapter, int[] checkedItems, OnMultiChoiceClickListener listener,
                boolean isShowListIcon) {
            this.itemAdapter = adapter;
            this.multiListener = listener;
            this.multiChoiceInitCheckItems = checkedItems;
            this.state = ListState.MULTI;
            this.showListIcon = isShowListIcon;
            return this;
        }

        public Builder setMultiChoiceItems(CharSequence[] items, int[] checkedItems, OnMultiChoiceClickListener listener,
                boolean isShowListIcon) {
            if (items == null || items.length <= 0) {
                return this;
            }

            this.items = items;
            this.multiListener = listener;
            this.multiChoiceInitCheckItems = checkedItems;
            this.state = ListState.MULTI;
            this.showListIcon = isShowListIcon;
            return this;
        }

        public Builder setMultiChoiceItems(int itemsId, int[] checkedItems, OnMultiChoiceClickListener listener,
                boolean isShowListIcon) {
            if (context == null || context.getResources() == null) {
                Log.e(TAG, "getResources() is null!!!!");
                return null;
            }

            this.items = context.getResources().getTextArray(itemsId);
            this.multiListener = listener;
            this.multiChoiceInitCheckItems = checkedItems;
            this.state = ListState.MULTI;
            this.showListIcon = isShowListIcon;
            return this;
        }

        public Builder setOnCancelListener(OnCancelListener listener) {
            this.cancelListener = listener;
            return this;
        }

        public Builder setOnDismissListener(OnDismissListener listener) {
            this.dismissListener = listener;
            return this;
        }

        public Builder setOnKeyListener(OnKeyListener listener) {
            this.keyListener = listener;
            return this;
        }

        public Builder setCancelable(boolean isCancel) {
            this.cancelable = isCancel;
            return this;
        }

        public Builder setCanceledOnTouchOutside(boolean isTouchCancel) {
            this.touchCancelable = isTouchCancel;
            return this;
        }

        public Builder setShowListIcon(boolean isShowListIcon) {
            this.showListIcon = isShowListIcon;
            return this;
        }

        public Builder setListHeaderView(View view) {
            this.headerView = view;
            return this;
        }

        public Builder setListFooterView(View view) {
            this.footerView = view;
            return this;
        }

        public CustomDialog show() {
            CustomDialog dialog = create();
            dialog.show();
            return dialog;
        }

        public CustomDialog create() {
            // instantiate the dialog with the custom Theme
            final CustomDialog dialog = new CustomDialog(context, R.style.DialogTheme);
            View layout = View.inflate(context, R.layout.view_custom_dialog, null);
            dialog.addContentView(layout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

            // set the dialog title
            TextView titleView = (TextView) layout.findViewById(R.id.dialogTitle);
            if (titleView != null) {
                if (!TextUtils.isEmpty(title)) {
                    titleView.setText(title);

                    if(titleStyle != 0) titleView.setTextAppearance(context, titleStyle);
                } else {
                    View titleLayout = layout.findViewById(R.id.dialogTitleLayout);
                    if (titleLayout != null) {
                        titleLayout.setVisibility(View.GONE);
                    }
                }
            }

            if (verticalButton) {
                // set the confirm button
                boolean isPosView = setButtonEvent(dialog, (Button) layout.findViewById(R.id.dialogVerticalPositiveButton), posBtnResId, posBtnTxt, posBtnClickListener,
                        DialogInterface.BUTTON_POSITIVE);
                // set the cancel button
                boolean isNegView = setButtonEvent(dialog, (Button) layout.findViewById(R.id.dialogVerticalNegativeButton), negBtnResId, negBtnTxt, negBtnClickListener,
                        DialogInterface.BUTTON_NEGATIVE);
                // set the neutral button
                boolean isNetView = setButtonEvent(dialog, (Button) layout.findViewById(R.id.dialogVerticalNeutralButton), neuBtnResId, neuBtnTxt, neuBtnClickListener,
                        DialogInterface.BUTTON_NEUTRAL);
                if (!isPosView && !isNegView && !isNetView) {
                    layout.findViewById(R.id.dialogVerticalButtonLayout).setVisibility(View.GONE);
                }
                layout.findViewById(R.id.dialogButtonLayout).setVisibility(View.GONE);
            } else {
                // set the confirm button
                boolean isPosView = setButtonEvent(dialog, (Button) layout.findViewById(R.id.dialogPositiveButton), posBtnResId, posBtnTxt, posBtnClickListener,
                        DialogInterface.BUTTON_POSITIVE);
                // set the cancel button
                boolean isNegView = setButtonEvent(dialog, (Button) layout.findViewById(R.id.dialogNegativeButton), negBtnResId, negBtnTxt, negBtnClickListener,
                        DialogInterface.BUTTON_NEGATIVE);
                // set the neutral button
                boolean isNetView = setButtonEvent(dialog, (Button) layout.findViewById(R.id.dialogNeutralButton), neuBtnResId, neuBtnTxt, neuBtnClickListener,
                        DialogInterface.BUTTON_NEUTRAL);
                if (!isPosView && !isNegView && !isNetView) {
                    layout.findViewById(R.id.dialogButtonLayout).setVisibility(View.GONE);
                }
                layout.findViewById(R.id.dialogVerticalButtonLayout).setVisibility(View.GONE);
            }

            // set the content message
            if (message != null) {
                TextView messageView = (TextView) layout.findViewById(R.id.dialogTextview);
                messageView.setVisibility(View.VISIBLE);
                messageView.setText(message);
            } else if (items != null || itemAdapter != null) {
                ListView dialogListview = (ListView) layout.findViewById(R.id.dialogListview);
                dialogListview.setVisibility(View.VISIBLE);
                if (headerView != null) {
                    dialogListview.addHeaderView(headerView, null, true);
                }
                if (footerView != null) {
                    dialogListview.addFooterView(footerView, null, true);
                }

                if (items != null) {
                    if (state == ListState.MULTI) {
                        dialogListview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                    } else {
                        dialogListview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                    }

                    ArrayList<CharSequence> itemList = new ArrayList<>();
                    Collections.addAll(itemList, items);
                    choiceAdapter = new CommonDialogChoiceAdapter(context, itemList, showListIcon, singleChoiceInitCheckItem,
                            state == ListState.MULTI);
                    dialogListview.setAdapter(choiceAdapter);
                } else {
                    dialogListview.setAdapter(itemAdapter);
                }

                if (singleChoiceInitCheckItem >= 0) {
                    dialogListview.setItemChecked(singleChoiceInitCheckItem, true);
                }
                if (multiChoiceInitCheckItems != null) {
                    for (int item : multiChoiceInitCheckItems) {
                        dialogListview.setItemChecked(item, true);
                        choiceAdapter.setChoiceItemPosition(item);
                    }
                }

                dialogListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (choiceAdapter != null) {
                            choiceAdapter.setChoiceItemPosition(position);
                        }

                        if (state == ListState.SINGLE) {
                            if (itemClickListener != null) {
                                itemClickListener.onClick(dialog, position);
                            }
                            if (!showListIcon) {
                                dialog.dismiss();
                            }
                        } else {
                            if (view instanceof CheckedTextView) {
                                if (multiListener != null) {
                                    multiListener.onClick(dialog, position, ((CheckedTextView) view).isChecked());
                                }
                            }
                        }
                    }
                });
            } else if (contentView != null) {
                // if no message set add the contentView to the dialog body
                dialogLayoutView = (ViewGroup) layout.findViewById(R.id.dialogLayout);
                if (dialogLayoutPaddingLeft != -1 || dialogLayoutPaddingRight != -1) {
                    dialogLayoutView.setPadding(dialogLayoutPaddingLeft, 0, dialogLayoutPaddingRight, 0);
                }

                if (contentView.getParent() != null && contentView.getParent() instanceof ViewGroup) {
                    ((ViewGroup) contentView.getParent()).removeView(contentView);
                }
                dialogLayoutView.addView(contentView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            }

            dialog.setContentView(layout);
            dialog.setOnCancelListener(cancelListener);
            dialog.setOnDismissListener(dismissListener);
            dialog.setOnKeyListener(keyListener);
            dialog.setCancelable(cancelable);
            dialog.setCanceledOnTouchOutside(touchCancelable);

            return dialog;
        }

        private boolean setButtonEvent(final Dialog dialog, Button button, int buttonResId, String buttonText, final OnClickListener listener,
                final int dialogClickInterface) {
            boolean isResult = false;

            if (button != null) {
                if (buttonText != null) {
                    button.setBackgroundResource(buttonResId);
                    button.setText(buttonText);
                    if (listener != null) {
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                listener.onClick(dialog, dialogClickInterface);
                                if (autoDismissEnable) {
                                    dialog.dismiss();
                                }
                            }
                        });
                    }
                    isResult = true;
                } else {
                    // if no button just set the visibility to GONE
                    button.setVisibility(View.GONE);
                }
            }

            return isResult;
        }

        public boolean isAutoDismissEnable() {
            return autoDismissEnable;
        }

        public void setAutoDismissEnable(boolean isAutoDismissEnable) {
            this.autoDismissEnable = isAutoDismissEnable;
        }

        public boolean isVerticalButton() {
            return verticalButton;
        }

        public void setVerticalButton(boolean isVerticalButton) {
            this.verticalButton = isVerticalButton;
        }

        public void setPaddingDialogLayout(int left, int right) {
            dialogLayoutPaddingLeft = left;
            dialogLayoutPaddingRight = right;
        }
    }

}
