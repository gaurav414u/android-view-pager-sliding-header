package com.android.test.scrollviewtest;

import com.gauravbhola.viewpagerslidingheader.ScrollUtils;
import com.gauravbhola.viewpagerslidingheader.ViewPagerSlidingHeaderRootView;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;

/**
 * Created by gauravbhola on 21/02/16.
 */
public class SlidingDrawerWithTransparentActionBarActivity extends SimpleSlidingDrawerWithActionBarSliding {
    ImageView mHeaderView;
    int mColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHeaderView = (ImageView)findViewById(R.id.imageview_actual);
        mColor = getResources().getColor(R.color.primary);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_simple_sliding_drawer_transparent_actionbar;
    }

    @Override
    protected void prepareViewPagerSlidingHeader() {
        mToolbar.setBackgroundColor(Color.TRANSPARENT);
        mRootView.initHeaderViewPager(null, mImageView, mSlidingTabLayout, mPagerContainer);
        mRootView.setParallaxFactor(4);
        mRootView.registerHeaderListener(new ViewPagerSlidingHeaderRootView.HeaderSlideListener() {
            @Override
            public void onOpenPercentChanged(final int openPercent, final float translationY) {
                L.d("openPercent = " + openPercent);
                L.d("translation = " + translationY);
                mHeaderView.post(new Runnable() {
                    @Override
                    public void run() {
                        //diving by 4 to set the parallax
                        mHeaderView.setTranslationY(translationY / 4);
                        mHeaderView.setColorFilter(ScrollUtils.getColorWithAlpha(1-((float)openPercent)/100, mColor));
                    }
                });
            }
        });
    }
}
