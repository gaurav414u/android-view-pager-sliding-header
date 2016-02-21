package com.android.test.scrollviewtest;

import com.gauravbhola.viewpagerslidingheader.ViewPagerSlidingHeaderRootView;

/**
 * Created by gauravbhola on 21/02/16.
 */
public class SlidingDrawerWithoutActionBarSliding extends SimpleSlidingDrawerWithActionBarSliding {
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
