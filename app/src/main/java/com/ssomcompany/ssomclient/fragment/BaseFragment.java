package com.ssomcompany.ssomclient.fragment;

import android.content.DialogInterface;
import android.support.v4.app.Fragment;

import com.ssomcompany.ssomclient.activity.BaseActivity;
import com.ssomcompany.ssomclient.common.SsomPreferences;
import com.ssomcompany.ssomclient.widget.dialog.CommonDialog;

public abstract class BaseFragment extends Fragment {
    private static final String PROGRESS_DIALOG_TAG = "WAIT";
    private CommonDialog mProgressDialog = null;

    /**
     * Show progress dialog(Cancel by Back key)
     */
    public void showProgressDialog() {
        showProgressDialog(true);
    }

    /**
     * Show progress dialog
     *
     * @param isCancelable (Cancel by Back key)
     */
    public void showProgressDialog(boolean isCancelable) {
        try {
            makeProgressDialog();
            if (mProgressDialog.getFragmentManager() == null) {
                mProgressDialog.setCancelable(isCancelable);
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show(getActivity().getFragmentManager(), PROGRESS_DIALOG_TAG);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Make base progress dialog
     */
    private void makeProgressDialog() {
        try {
            if (mProgressDialog == null) {
                mProgressDialog = CommonDialog.getInstance(CommonDialog.DIALOG_STYLE_SPINNER);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Dismiss progress dialog
     */
    public void dismissProgressDialog() {
        try {
            if (mProgressDialog != null && mProgressDialog.getFragmentManager() != null) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Set cancel listener in ProgressDialog
     *
     * @param cancel OnCancelListener
     */
    public void setProgressDialogCancel(DialogInterface.OnCancelListener cancel) {
        makeProgressDialog();
        mProgressDialog.setCancelListener(cancel);
    }

    /**
     * Set dismiss listener in ProgressDialog
     *
     * @param dismiss OnDismissListener
     */
    public void setProgressDialogDismiss(DialogInterface.OnDismissListener dismiss) {
        makeProgressDialog();
        mProgressDialog.setDismissListener(dismiss);
    }

    /**
     * Set key listener in ProgressDialog
     *
     * @param listener OnKeyListener
     */
    public void setProgressDialogKeyListener(DialogInterface.OnKeyListener listener) {
        makeProgressDialog();
        mProgressDialog.setKeyListener(listener);
    }

    /**
     * Error message
     */
    public void showErrorMessage() {
        ((BaseActivity) getActivity()).showErrorMessage();
    }

    /**
     * Get Session preference
     */
    public SsomPreferences getSession() {
        return ((BaseActivity) getActivity()).getSession();
    }

    /**
     * Get Session Token
     */
    public String getToken() {
        return ((BaseActivity) getActivity()).getToken();
    }
}
