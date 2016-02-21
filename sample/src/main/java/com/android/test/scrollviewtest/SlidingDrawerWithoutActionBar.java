package com.android.test.scrollviewtest;

import com.gauravbhola.viewpagerslidingheader.ViewPagerSlidingHeaderRootView;

/**
 * Created by gauravbhola on 21/02/16.
 */
public class SlidingDrawerWithoutActionBar extends SimpleSlidingDrawerWithActionBarSliding {

    @Override
    protected int getContentView() {
        return R.layout.activity_simple_sliding_drawer_without_actionbar;
    }

    @Override
    protected void prepareViewPagerSlidingHeader() {
        mRootView.initHeaderViewPager(null, mImageView, mSlidingTabLayout, mPagerContainer);
        mRootView.setParallaxFactor(4);
        mRootView.registerHeaderListener(new ViewPagerSlidingHeaderRootView.HeaderSlideListener() {
            @Override
            public void onOpenPercentChanged(int openPercent, float translationY) {
                L.d("openPercent = " + openPercent);
                L.d("translation = " + translationY);
            }
        });
    }
}
