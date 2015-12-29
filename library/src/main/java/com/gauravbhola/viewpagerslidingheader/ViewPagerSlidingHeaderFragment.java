package com.gauravbhola.viewpagerslidingheader;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

/**
 * Created by gauravbhola on 23/09/15.
 */
public abstract class ViewPagerSlidingHeaderFragment extends Fragment implements SlidingHeaderCallbacks {
    public static final String TAG = ViewPagerSlidingHeaderFragment.class.getName();
    public static final String PARAM_POSITION = "position";
    public int mPosition;
    public SlidingHeaderActivityCallbacks mCallbacks;
    public FragmentedActivity mActivityCallbacks;
    float prevY = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPosition = getArguments().getInt(PARAM_POSITION, -1);
        if (mActivityCallbacks != null) {
            mActivityCallbacks.updateFragmentInstance(this, mPosition);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        final View scrollable = getScrollableView();
        if (scrollable == null) {
            return;
        }
        if (mCallbacks == null) {
            return;
        }
        scrollable.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_UP:
                        break;
                    case MotionEvent.ACTION_DOWN:
                        prevY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        Log.d(TAG, "onTouchEvent() move");
                        int dy = (int) (event.getY() - prevY);
                        int scroll = 0;
                        if (scrollable instanceof RecyclerView) {
                            scroll = ((RecyclerView)scrollable).computeVerticalScrollOffset();
                        } else if(scrollable instanceof ScrollView) {
                            scroll = ((ScrollView)scrollable).getScrollY();
                        }
                        if (scroll == 0 && dy > 0) {
                            mCallbacks.getRootView().interceptTouchEvent(true);
                            prevY = event.getY();
                            return true;
                        }

                        if (dy > 0 && mCallbacks.getRootView().getActionBarState() == ViewPagerSlidingHeaderRootView.ActionBarState.CLOSED) {
                            mCallbacks.getRootView().interceptTouchEvent(true);
                            prevY = event.getY();
                            return true;
                        }
                        if (dy < 0
                                && mCallbacks.getRootView().getActionBarState() == ViewPagerSlidingHeaderRootView.ActionBarState.OPEN
                                && mCallbacks.getRootView().isActionBarSlidingEnabled()) {
                            mCallbacks.getRootView().interceptTouchEvent(true);
                            prevY = event.getY();
                            return true;
                        }
                        prevY = event.getY();
                        return false;
                }
                return false;
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mActivityCallbacks = (FragmentedActivity) activity;
        } catch (ClassCastException e) {
            //TODO: handle this case properly
        }
        try {
            mCallbacks = (SlidingHeaderActivityCallbacks) activity;
        } catch (ClassCastException e) {
            //TODO: handle this case properly
        }
        if (mCallbacks == null) {
            //try to look for the parent fragment
            try {
                mCallbacks = (SlidingHeaderActivityCallbacks) getParentFragment();
            } catch (ClassCastException e) {
            }
        }
    }

    public abstract View getScrollableView();

    public interface FragmentedActivity {
        public void updateFragmentInstance(Fragment fragment, int position);
        public View getToolBar();
    }

    @Override
    public boolean shouldDrawerMove() {
        View scrollable = getScrollableView();
        if (scrollable == null) {
            return true;
        }
        if (scrollable instanceof ScrollView) {
            ((ScrollView)scrollable).computeScroll();
            if (((ScrollView)scrollable).getScrollY() == 0) {
                return true;
            }
        } else if (scrollable instanceof RecyclerView) {
            int scroll = ((RecyclerView)scrollable).computeVerticalScrollOffset();
            if (Math.abs(scroll) == 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void dispatchFling(MotionEvent ev1, MotionEvent ev2, float velx, float vely) {
        View scrollable = getScrollableView();
        if (scrollable == null) {
            return;
        }
        if (scrollable instanceof ScrollView) {
            ((ScrollView)scrollable).fling(-(int) vely);
        } else if (scrollable instanceof RecyclerView) {
            ((RecyclerView)scrollable).fling(-(int) velx, -(int)vely);
        }
    }
}
