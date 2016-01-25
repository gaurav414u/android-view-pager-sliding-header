package com.gauravbhola.viewpagerslidingheader;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;

import java.util.ArrayList;

/**
 * Created by gauravbhola on 07/12/15.
 */
public class ViewPagerSlidingHeaderRootView extends RelativeLayout {
    private SlidingHeaderCallbacks mCallbacks;
    private HeaderSlideListener mHeaderListener;
    private float prevY = 0;
    private float mTranslationYUpperBoundActionBar;
    private float mTranslationYLowerBoundActionBar;
    private float mTranslationYUpperBound;
    private float mTranslationYLowerBound;
    private float mParallaxFactor = 1;
    int mPrevYintercepted;
    int mPrevXintercepted;

    float mTouchDy;
    boolean mChildrenEventsCanceled = false;
    boolean mDownMotionEventPended = true;

    boolean mScrollMode = false;
    private int mTouchSlop;

    private View mSlidingTabLayout;
    private View mToolbar;
    private View mHeaderView;
    private View mPager;

    public static enum DrawerState {OPEN, CLOSED, CLOSING, OPENING};
    public static enum ActionBarState {OPEN, CLOSED, CLOSING, OPENING};

    private DrawerState mDrawerState;
    private ActionBarState mActionBarState;

    public float getParallaxFactor() {
        return mParallaxFactor;
    }

    public void setParallaxFactor(float parallaxFactor) {
        mParallaxFactor = parallaxFactor;
    }

    public static abstract class HeaderSlideListener {
        //goes from 100 to 0 when the header closes or is in midway
        private int openPercent;
        private View mSlidingTabLayout;

        private int getOpenPercent() {
            return openPercent;
        }

        public void setOpenPercent(int openPercent) {
            this.openPercent = openPercent;
            onOpenPercentChanged(openPercent, (mSlidingTabLayout == null) ? 0 : mSlidingTabLayout.getTranslationY());
        }

        public abstract void onOpenPercentChanged(int openPercent, float slidingTabTranslation);
    }

    public void registerCallbacks(SlidingHeaderCallbacks callbacks) {
        mCallbacks = callbacks;
    }

    public void registerHeaderListener(HeaderSlideListener listener) {
        mHeaderListener = listener;
        mHeaderListener.mSlidingTabLayout = mSlidingTabLayout;
    }

    public ViewPagerSlidingHeaderRootView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    private int getActionBarHeight() {
        if (mToolbar != null) {
            return mToolbar.getHeight();
        }
        return 0;
    }

    public boolean isActionBarSlidingEnabled() {
        return mToolbar != null;
    }

    int mInterruptInterception = 0;

    public void initHeaderViewPager(View actionBarView, View headerView, View tabView, View pager) {
        mDrawerState = DrawerState.OPEN;
        mActionBarState = ActionBarState.OPEN;
        mSlidingTabLayout = tabView;
        mToolbar = actionBarView;
        mHeaderView = headerView;
        mPager = pager;
        if (mHeaderListener != null) {
            mHeaderListener.mSlidingTabLayout = mSlidingTabLayout;
        }


        mHeaderView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mTranslationYLowerBound = -mHeaderView.getHeight();
                mTranslationYUpperBound = 0;

                mTranslationYLowerBoundActionBar = -mHeaderView.getHeight() - getActionBarHeight();
                mTranslationYUpperBoundActionBar = mTranslationYLowerBound;
            }
        });

        ViewTreeObserver viewTreeObserver = mPager.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                LayoutParams layoutParams = new LayoutParams(
                        LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT);

                int height = 0;
                if (mToolbar == null) {
                    height = mPager.getHeight() + mHeaderView.getHeight();
                } else {
                    height = getHeight() - mSlidingTabLayout.getHeight();
                }

                int viewPagerWidth = mPager.getWidth();
                //float viewPagerHeight = (float) (viewPagerWidth * FEATURED_IMAGE_RATIO);

                layoutParams.width = viewPagerWidth;
                layoutParams.height = height;
                layoutParams.addRule(ALIGN_PARENT_BOTTOM);
                //layoutParams.addRule(RelativeLayout.BELOW, mSlidingTabLayout.getId());

                mPager.setLayoutParams(layoutParams);

                mPager.getViewTreeObserver()
                        .removeGlobalOnLayoutListener(this);
                //mPager.measure(viewPagerWidth, height);
                mPager.setTranslationY(getPagerDeviation());
                mPager.requestLayout();
            }
        });
    }

    public ActionBarState getActionBarState() {
        return mActionBarState;
    }

    public void parentShouldStartIntercepting() {
    }

    public void interceptTouchEvent(boolean intercept) {
        mIntercepting = intercept;
        requestDisallowInterceptTouchEvent(!intercept);
    }

    private boolean mIntercepting;
    private MotionEvent mPendingDownMotionEvent;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mCallbacks == null) {
            return false;
        }

        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mPrevYintercepted = (int) ev.getY();
                mPrevXintercepted = (int) ev.getX();
                mPendingDownMotionEvent = MotionEvent.obtainNoHistory(ev);
                mDownMotionEventPended = true;
                mChildrenEventsCanceled = false;
                return false;

            case MotionEvent.ACTION_MOVE:
                int dy = (int) ev.getY() - mPrevYintercepted;
                int dx = (int) ev.getX() - mPrevXintercepted;

                if (Math.abs(dx) > mTouchSlop) {
                    int i = 2;
                    return super.onInterceptTouchEvent(ev);
                }

                if (Math.abs(dy) < 15) {
                    return super.onInterceptTouchEvent(ev);
                }

                mIntercepting = shouldInterceptTouchEvents(dy);
                prevY = ev.getY();
                mPrevYintercepted = (int) ev.getY();
                return mIntercepting;

            case MotionEvent.ACTION_UP:
                return false;
        }
        return super.onInterceptTouchEvent(ev);
    }

    MyGestureListener mMyGestureListener = new MyGestureListener(getContext());

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mMyGestureListener.onTouch(this, event);
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener implements OnTouchListener {
        public GestureDetector gDetector;
        Context context;

        public MyGestureListener() {
            super();
        }

        public MyGestureListener(Context context) {
            this(context, null);
        }

        public MyGestureListener(Context context, GestureDetector gDetector) {
            if (gDetector == null)
                gDetector = new GestureDetector(context, this);

            this.context = context;
            this.gDetector = gDetector;
        }


        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            // if we dont have a lower bound on dy, the list flings unnecessarily
            if (Math.abs(mTouchDy) < 5) {
                velocityY = velocityY/5;
            } else if (Math.abs(mTouchDy) < 7) {
                velocityY = velocityY/3;
            } else if (Math.abs(mTouchDy) < 10) {
                velocityY = velocityY/2;
            }
            dispatchFling(e1, e2, velocityX, velocityY);
            return super.onFling(e1, e2, velocityX, velocityY);
        }


        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getActionMasked() == MotionEvent.ACTION_MOVE) {
                float y = event.getY();
                mTouchDy = y - prevY;
                prevY = y;

                mIntercepting = shouldInterceptTouchEvents((int)mTouchDy);
                if (mInterruptInterception > 0) {
                    //if there is a need to interrupt, then do it now
                    mInterruptInterception--;
                    return false;
                }
                if (mIntercepting) {
                    moveContents(mTouchDy);
                    if (!mChildrenEventsCanceled) {
                        mChildrenEventsCanceled = true;
                        //cancel the children events
                        duplicateTouchEventForChildren(obtainMotionEvent(event, MotionEvent.ACTION_CANCEL));
                    }
                    mDownMotionEventPended = true;
                    return true;
                } else {
                    //send a down motion event to the children first, then start duplicating the events as is
                    if (mDownMotionEventPended) {
                        mDownMotionEventPended = false;
                        MotionEvent mev = MotionEvent.obtainNoHistory(mPendingDownMotionEvent);
                        mev.setLocation(event.getX(), event.getY());
                        duplicateTouchEventForChildren(event, mev);
                    } else {
                        duplicateTouchEventForChildren(event);
                    }
                    mChildrenEventsCanceled = false;
                    return gDetector.onTouchEvent(event);
                }
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                settleContents();
                return gDetector.onTouchEvent(event);
                //return false;
            }
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                prevY = event.getY();
                return true;
            }
            return gDetector.onTouchEvent(event);
        }
    }

    private MotionEvent obtainMotionEvent(MotionEvent base, int action) {
        MotionEvent ev = MotionEvent.obtainNoHistory(base);
        ev.setAction(action);
        return ev;
    }

    /**
     * Duplicate touch events to child ViewPager views.
     * We want to dispatch a down motion event and the move events to
     * child views, but calling dispatchTouchEvent() causes StackOverflowError.
     * Therefore we do it manually.
     *
     * @param ev            motion event to be passed to children(viewpager)
     * @param pendingEvents pending events like ACTION_DOWN. This will be passed to the children before ev
     */
    private void duplicateTouchEventForChildren(MotionEvent ev, MotionEvent... pendingEvents) {
        if (ev == null) {
            return;
        }
        for (int i = getChildCount() - 1; 0 <= i; i--) {
            View childView = getChildAt(i);
            //only duplicating the events if the child is a ViewPager
            if (childView != null && childView == mPager) {
                MotionEvent event = MotionEvent.obtainNoHistory(ev);
                //doesnt matter whether this touch lies in the child rect or not
                /*
                Rect childRect = new Rect();
                childView.getHitRect(childRect);
                if (!childRect.contains((int) event.getX(), (int) event.getY())) {
                    continue;
                }*/
                float offsetX = -childView.getLeft();
                float offsetY = -childView.getTop();
                boolean consumed = false;
                if (pendingEvents != null) {
                    for (MotionEvent pe : pendingEvents) {
                        if (pe != null) {
                            MotionEvent peAdjusted = MotionEvent.obtainNoHistory(pe);
                            peAdjusted.offsetLocation(offsetX, offsetY);
                            try {
                                consumed |= childView.dispatchTouchEvent(peAdjusted);
                            } catch (IllegalArgumentException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                event.offsetLocation(offsetX, offsetY);
                try {
                    consumed |= childView.dispatchTouchEvent(event);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
                if (consumed) {
                    break;
                }
            }
        }
    }


    public boolean moveContents(float dy) {
        //awesomeness :) ;)
        if (mDrawerState == DrawerState.OPEN
                || mDrawerState == DrawerState.OPENING
                || mDrawerState == DrawerState.CLOSING) {
            return moveHeader(dy);
        }
        if (mDrawerState == DrawerState.CLOSED && mActionBarState == ActionBarState.OPEN && dy > 0) {
            if (mCallbacks.shouldDrawerMove()) {
                return moveHeader(dy);
            } else {
                requestDisallowInterceptTouchEvent(true);
            }
        }
        return moveActionBar(dy);
    }

    public int getPagerDeviation() {
        if (mToolbar == null) {
            return mHeaderView.getHeight();
        } else {
            return getActionBarHeight() + mHeaderView.getHeight();
        }
    }

    public boolean moveHeader(float dy) {
        if (dy > 0) {
            mDrawerState = DrawerState.OPENING;
        } else if (dy < 0) {
            mDrawerState = DrawerState.CLOSING;
        }
        float translationY = (mSlidingTabLayout.getTranslationY() + dy);
        translationY = ScrollUtils.getFloat(translationY, mTranslationYLowerBound, mTranslationYUpperBound);

        mSlidingTabLayout.setTranslationY(translationY);
        if (mHeaderListener != null) {
            mHeaderListener.setOpenPercent((int) (100 - 100 * (translationY / mTranslationYLowerBound)));
        }
        mHeaderView.setTranslationY(translationY / mParallaxFactor);
        mHeaderView.requestLayout();
        mPager.setTranslationY(getPagerDeviation() + translationY);

        if (translationY == mTranslationYLowerBound) {
            mDrawerState = DrawerState.CLOSED;
            if (isActionBarSlidingEnabled()) {
                mInterruptInterception = 5;
            }
            return false;
        }
        if (translationY == mTranslationYUpperBound) {
            mDrawerState = DrawerState.OPEN;
            return false;
        }
        return true;
    }

    public boolean moveActionBar(float dy) {
        if (mToolbar == null) {
            return false;
        }
        if (dy > 0) {
            mActionBarState = ActionBarState.OPENING;
        } else if (dy < 0) {
            mActionBarState = ActionBarState.CLOSING;
        }

        float translationY = (mSlidingTabLayout.getTranslationY() + dy);
        translationY = ScrollUtils.getFloat(translationY, mTranslationYLowerBoundActionBar, mTranslationYUpperBoundActionBar);
        mSlidingTabLayout.setTranslationY(translationY);
        mHeaderView.setTranslationY(translationY / mParallaxFactor);

        if (mToolbar != null) {
            float translationYActionbar = ScrollUtils.getFloat(mToolbar.getTranslationY() + dy, -getActionBarHeight(), 0);
            mToolbar.setTranslationY(translationYActionbar);
        }

        mPager.setTranslationY(getPagerDeviation() + translationY);

        if (translationY == mTranslationYLowerBoundActionBar) {
            mActionBarState = ActionBarState.CLOSED;
            return false;
        }
        if (translationY == mTranslationYUpperBoundActionBar) {
            mActionBarState = ActionBarState.OPEN;
            return false;
        }
        return true;
    }


    public void settleContents() {
        if (mDrawerState == DrawerState.OPENING
                || mDrawerState == DrawerState.CLOSING) {
            closeDrawer();
            return;
        }
        if (mActionBarState == ActionBarState.OPENING ||
                mActionBarState == ActionBarState.CLOSING) {
            closeActionBar();
            return;
        }
    }

    public boolean shouldInterceptTouchEvents(int dy) {
        if (dy <= 0) {
            //sliding up

            if (mDrawerState == DrawerState.OPEN || mDrawerState == DrawerState.CLOSING || mDrawerState == DrawerState.OPENING) {
                //drawer is open or is in midway
                return true;
            }
            //drawer closed

            if (mActionBarState == ActionBarState.OPEN || mActionBarState == ActionBarState.OPENING || mActionBarState == ActionBarState.CLOSING) {
                //action bar is open or is in midway

                if (mToolbar != null) {
                    //action bar sliding is supported
                    return true;
                } else {
                    return false;
                }
            }
            //actionbar closed

            return false;
        }
        if (dy > 0) {
            //sliding down

            if (mActionBarState == ActionBarState.CLOSED || mActionBarState == ActionBarState.OPENING || mActionBarState == ActionBarState.CLOSING) {
                //action bar closed or in midway
                return true;
            }
            //action bar open

            if (mDrawerState == DrawerState.CLOSED) {
                //drawer closed
                return mCallbacks.shouldDrawerMove();
            }

            if (mDrawerState == DrawerState.CLOSING || mDrawerState == DrawerState.OPENING) {
                //drawer midway
                return true;
            }

            //drawer open
            return false;
        }
        return false;
    }

    private void closeDrawer() {
        long animationDuration = 200;
        float translationY = 0;
        int pagerNewHeight = mPager.getHeight();
        DrawerState drawerState = DrawerState.OPEN;
        float alpha = 0f;

        if (mDrawerState == DrawerState.CLOSING) {
            translationY = mTranslationYLowerBound;
            drawerState = DrawerState.CLOSED;
            alpha = 0f;
        }

        if (mDrawerState == DrawerState.OPENING) {
            translationY = mTranslationYUpperBound;
            drawerState = DrawerState.OPEN;
            alpha = 1f;
        }

        final DrawerState finalDrawerState = drawerState;

        ObjectAnimator animator = ObjectAnimator.ofFloat(mHeaderView,
                "translationY",
                mHeaderView.getTranslationY(), translationY / mParallaxFactor);

        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mSlidingTabLayout,
                "translationY",
                mSlidingTabLayout.getTranslationY(), translationY);

        ObjectAnimator animator3 = ObjectAnimator.ofFloat(mPager,
                "translationY",
                mPager.getTranslationY(), getPagerDeviation() + translationY);


        ArrayList<ObjectAnimator> arrayListObjectAnimators = new ArrayList<ObjectAnimator>(); //ArrayList of ObjectAnimators
        arrayListObjectAnimators.add(0, animator);
        arrayListObjectAnimators.add(1, animator2);
        arrayListObjectAnimators.add(2, animator3);

        if (mHeaderListener != null) {
            ObjectAnimator animator5 = ObjectAnimator.ofInt(mHeaderListener,
                    "openPercent",
                    mHeaderListener.getOpenPercent(), (int)(alpha)*100);
            arrayListObjectAnimators.add(3, animator5);
        }

        ObjectAnimator[] objectAnimators = arrayListObjectAnimators.toArray(new ObjectAnimator[arrayListObjectAnimators.size()]);
        AnimatorSet animSetXY = new AnimatorSet();
        animSetXY.setInterpolator(new DecelerateInterpolator());
        animSetXY.playTogether(objectAnimators);
        animSetXY.setDuration(animationDuration);//1sec
        animSetXY.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mDrawerState = finalDrawerState;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animSetXY.start();
    }

    private void closeActionBar() {
        long animationDuration = 200;
        float translationY = 0;
        float translationYActionBar = 0;
        int pagerNewHeight = mPager.getHeight();
        ActionBarState drawerState = ActionBarState.OPEN;

        if (mActionBarState == ActionBarState.CLOSING) {
            translationY = mTranslationYLowerBoundActionBar;
            translationYActionBar = -getActionBarHeight();
            drawerState = ActionBarState.CLOSED;
        }

        if (mActionBarState == ActionBarState.OPENING) {
            translationY = mTranslationYUpperBoundActionBar;
            translationYActionBar = 0f;
            drawerState = ActionBarState.OPEN;
        }

        final ActionBarState finalDrawerState = drawerState;

        ObjectAnimator animator = ObjectAnimator.ofFloat(mHeaderView,
                "translationY",
                mHeaderView.getTranslationY(), translationY / mParallaxFactor);

        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mSlidingTabLayout,
                "translationY",
                mSlidingTabLayout.getTranslationY(), translationY);

        ObjectAnimator animator3 = ObjectAnimator.ofFloat(mPager,
                "translationY",
                mPager.getTranslationY(), getPagerDeviation() + translationY);


        ArrayList<ObjectAnimator> arrayListObjectAnimators = new ArrayList<ObjectAnimator>(); //ArrayList of ObjectAnimators
        arrayListObjectAnimators.add(0, animator);
        arrayListObjectAnimators.add(1, animator2);
        arrayListObjectAnimators.add(2, animator3);

        if (mToolbar != null) {
            ObjectAnimator animator4 = ObjectAnimator.ofFloat(mToolbar,
                    "translationY",
                    mToolbar.getTranslationY(), translationYActionBar);
            arrayListObjectAnimators.add(3, animator4);
        }

        ObjectAnimator[] objectAnimators = arrayListObjectAnimators.toArray(new ObjectAnimator[arrayListObjectAnimators.size()]);
        AnimatorSet animSetXY = new AnimatorSet();
        animSetXY.setInterpolator(new DecelerateInterpolator());
        animSetXY.playTogether(objectAnimators);
        animSetXY.setDuration(animationDuration);//1sec
        animSetXY.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mActionBarState = finalDrawerState;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animSetXY.start();
    }

    public void dispatchFling(MotionEvent ev1, MotionEvent ev2, float velx, float vely) {
        if (mDrawerState != DrawerState.CLOSING
                && mActionBarState != ActionBarState.CLOSING
                && mActionBarState != ActionBarState.OPENING) {
            mCallbacks.dispatchFling(ev1, ev2, velx, vely);
        }
    }

}
