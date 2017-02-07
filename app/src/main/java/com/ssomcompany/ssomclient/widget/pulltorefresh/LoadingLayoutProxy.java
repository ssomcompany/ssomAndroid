
package com.ssomcompany.ssomclient.widget.pulltorefresh;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import java.util.HashSet;

public class LoadingLayoutProxy implements ILoadingLayout {

    private final PullToRefreshBase<?> mPullToRefreshView;
    private final HashSet<LoadingLayout> mLoadingLayouts;

    LoadingLayoutProxy(PullToRefreshBase<?> pullToRefreshView) {
        mPullToRefreshView = pullToRefreshView;
        mLoadingLayouts = new HashSet<LoadingLayout>();
    }

    /**
     * This allows you to add extra LoadingLayout instances to this proxy. This
     * is only necessary if you keep your own instances, and want to have them
     * included in any
     * {@link PullToRefreshBase#createLoadingLayoutProxy(boolean, boolean)
     * createLoadingLayoutProxy(...)} calls.
     * 
     * @param layout - LoadingLayout to have included.
     */
    public void addLayout(LoadingLayout layout) {
        if (null != layout) {
            mLoadingLayouts.add(layout);
        }
    }

    @Override
    public void setLastUpdatedLabel(CharSequence label) {
        for (LoadingLayout layout : mLoadingLayouts) {
            layout.setLastUpdatedLabel(label);
        }

        mPullToRefreshView.refreshLoadingViewsSize();
    }

    @Override
    public void setLoadingDrawable(Drawable drawable) {
        for (LoadingLayout layout : mLoadingLayouts) {
            layout.setLoadingDrawable(drawable);
        }

        mPullToRefreshView.refreshLoadingViewsSize();
    }

    @Override
    public void setRefreshingLabel(CharSequence refreshingLabel) {
        for (LoadingLayout layout : mLoadingLayouts) {
            layout.setRefreshingLabel(refreshingLabel);
        }
    }

    @Override
    public void setPullLabel(CharSequence label) {
        for (LoadingLayout layout : mLoadingLayouts) {
            layout.setRefreshingLabel(label);
        }
    }

    @Override
    public void setReleaseLabel(CharSequence label) {
        for (LoadingLayout layout : mLoadingLayouts) {
            layout.setReleaseLabel(label);
        }
    }

    public void setTextTypeface(Typeface tf) {
        for (LoadingLayout layout : mLoadingLayouts) {
            layout.setTextTypeface(tf);
        }

        mPullToRefreshView.refreshLoadingViewsSize();
    }
}
