/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package com.ssomcompany.ssomclient.widget.pulltorefresh;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import com.ssomcompany.ssomclient.R;

public class PullToRefreshListView extends PullToRefreshBase<ListView> {

    private static final String TAG = PullToRefreshListView.class.getSimpleName();
    private static final int REFRESH_POSITION = 2;
    private static final int MIN_ITEM_COUNT = 10;

    public PullToRefreshListView(Context context) {
        super(context);
    }

    public PullToRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullToRefreshListView(Context context, Mode mode) {
        super(context, mode);
    }

    public PullToRefreshListView(Context context, Mode mode, AnimationStyle style) {
        super(context, mode, style);
    }

    public interface PTRmScrollListener {
        void onScrollUp();
        void onScrollDown();
    }

    private PTRmScrollListener scrollListener;

    @Override
    public final Orientation getPullToRefreshScrollDirection() {
        return Orientation.VERTICAL;
    }

    @Override
    protected ListView createRefreshableView(Context context, AttributeSet attrs) {
        ListView listView;
        listView = new ListView(context, attrs);
        listView.setId(R.id.list);
        return listView;
    }

    @Override
    protected boolean isReadyForPullStart() {
        if (mRefreshableView.getChildCount() <= 0) {
            return true;
        }
        int firstVisiblePosition = mRefreshableView.getFirstVisiblePosition();
        if (firstVisiblePosition == 0) {
            return mRefreshableView.getChildAt(0).getTop() == mRefreshableView.getPaddingTop();
        } else {
            return false;
        }

    }

    @Override
    protected boolean isReadyForPullEnd() {
        int lastVisiblePosition = mRefreshableView.getLastVisiblePosition();
        if (mRefreshableView.getChildCount() > 0 && mRefreshableView.getCount() > MIN_ITEM_COUNT
                && mRefreshableView.getAdapter().getCount() > REFRESH_POSITION
                && lastVisiblePosition >= mRefreshableView.getAdapter().getCount() - 1 - REFRESH_POSITION) {
            return true;
            // return mRefreshableView.getChildAt(mRefreshableView.getChildCount() - 1).getBottom() <= mRefreshableView.getBottom();
        }
        return false;
    }

    @Override
    protected void setScrollEvent(ListView mRefreshableView) {
        if (mRefreshableView != null) {
            mRefreshableView.setOnScrollListener(new OnScrollListener() {
                private int oldFirstVisibleItem;

                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    if (mSwipeRefreshLayout != null && firstVisibleItem == 0 && getMode() != null && getMode().showHeaderLoadingLayout()) {
                        if (view != null && view.getChildCount() > 0 && view.getChildAt(0).getTop() == 0) {
                            mSwipeRefreshLayout.setEnabled(true);
                        } else {
                            mSwipeRefreshLayout.setEnabled(false);
                        }
                    } else {
                        if (mSwipeRefreshLayout != null) {
                            mSwipeRefreshLayout.setEnabled(false);
                        }
                    }

                    if (firstVisibleItem != oldFirstVisibleItem) {
                        if (firstVisibleItem < oldFirstVisibleItem) {  // scroll up
                            if(scrollListener != null) {
                                scrollListener.onScrollUp();
                            }
                        } else {  // scroll down
                            if(scrollListener != null) {
                                scrollListener.onScrollDown();
                            }
                        }
                    }

                    if (isReadyForPullEnd() && getState() != State.REFRESHING && getMode() != null && getMode().showFooterLoadingLayout()) {
                        setState(State.REFRESHING, true);
                        if (getOnRefreshListener2() != null) {
                            getOnRefreshListener2().onPullUpToRefresh(PullToRefreshListView.this);
                        } else if (getOnRefreshListener() != null) {
                            getOnRefreshListener().onRefresh(PullToRefreshListView.this);
                        }
                    }

                    oldFirstVisibleItem = firstVisibleItem;
                }
            });
        }
    }

    public void setOnPTRmScrollListener(PTRmScrollListener scrollListener) {
        this.scrollListener = scrollListener;
    }

    @Override
    protected void removeScrollEvent(ListView mRefreshableView) {
        if (mRefreshableView != null) {
            mRefreshableView.setOnScrollListener(null);
        }
    }

    private LoadingLayout loadingView;

    @Override
    protected void addFooter(ListView mRefreshableView) {
        if (getContext() == null) {
            Log.w(TAG, "Context is null");
            return;
        }
        if (loadingView == null) {
            TypedArray a = getContext().obtainStyledAttributes(null, R.styleable.PullToRefresh);
            loadingView = new RotateLoadingLayout(getContext(), Mode.PULL_FROM_END, Orientation.VERTICAL, a, isDrawerStyle);
            if (isDrawerStyle) {
                loadingView.setVisibleTopLine(false);
            }
            loadingView.setVisibleBottomLine(false);
            loadingView.refreshingImpl();
            mRefreshableView.addFooterView(loadingView);
        }
        setFooterVisible(false);
    }

    @Override
    protected void removeFooter(ListView mRefreshableView) {
        if (mRefreshableView.getFooterViewsCount() > 0 && loadingView != null) {
//            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
//                ListAdapter adapter = mRefreshableView.getAdapter();
//                mRefreshableView.setAdapter(null);
//                mRefreshableView.removeFooterView(loadingView);
//                mRefreshableView.setAdapter(adapter);
//            } else {
                mRefreshableView.removeFooterView(loadingView);
//            }
            loadingView = null;
        }
    }

    @Override
    protected void setFooterVisible(boolean visible) {
        if (loadingView != null) {
            if (visible) {
                loadingView.setVisibility(View.VISIBLE);
            } else {
                loadingView.setVisibility(View.GONE);
            }
        }
    }
}
