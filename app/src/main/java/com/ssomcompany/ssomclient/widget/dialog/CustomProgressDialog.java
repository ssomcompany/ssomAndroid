
package com.ssomcompany.ssomclient.widget.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.ssomcompany.ssomclient.R;

public class CustomProgressDialog extends AlertDialog {

    public static final int STYLE_SPINNER = 0;
    public static final int STYLE_SPINNER_MESSAGE = 1;

    private TextView progressMessage;
    private CharSequence mMessage;
    private int mProgressStyle = STYLE_SPINNER;

    public CustomProgressDialog(Context context) {
        super(context);
    }

    public CustomProgressDialog(Context context, int theme) {
        super(context, theme);
    }

    public void setProgressStyle(int style) {
        mProgressStyle = style;
    }

    @Override
    public void setMessage(CharSequence message) {
        if (mProgressStyle == STYLE_SPINNER_MESSAGE) {
            mMessage = message;

            if (progressMessage != null) {
                progressMessage.setText(mMessage);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (mProgressStyle == STYLE_SPINNER) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.view_custom_progress_dialog);
        } else {
            View view = View.inflate(getContext(),
                    R.layout.view_custom_progress_message_dialog, null);
            progressMessage = (TextView) view.findViewById(R.id.progressMessage);
            progressMessage.setText(mMessage);
            setView(view);

            if (mMessage != null) {
                setMessage(mMessage);
            }

            super.onCreate(savedInstanceState);
        }
    }

}
