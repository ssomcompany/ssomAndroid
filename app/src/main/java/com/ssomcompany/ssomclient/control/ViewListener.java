package com.ssomcompany.ssomclient.control;

import com.ssomcompany.ssomclient.network.api.model.SsomItem;

import java.util.ArrayList;

/**
 * Created by AaronMac on 2016. 7. 28..
 */
public class ViewListener {
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);
    }

    public interface OnDetailFragmentInteractionListener {
        void onDetailFragmentInteraction(boolean isApply);
    }

    public interface OnPostItemInteractionListener {
        void onPostItemClick(ArrayList<SsomItem> ssomList, int position);
    }

    public interface OnFilterFragmentInteractionListener {
        void onFilterFragmentInteraction(boolean isApply);
    }

    public interface OnChatItemInteractionListener {
        void onChatItemClick(int position);
    }

    public interface OnTabChangedListener {
        void onTabChangedAction(ArrayList<SsomItem> ssomList);
    }

    public interface OnLoginFragmentInteractionListener {
        void onLoginFragmentInteraction(int redId);
    }
}
