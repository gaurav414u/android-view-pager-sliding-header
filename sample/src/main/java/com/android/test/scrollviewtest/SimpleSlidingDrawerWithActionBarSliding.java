package com.android.test.scrollviewtest;

import com.gauravbhola.viewpagerslidingheader.SlidingHeaderActivityCallbacks;
import com.gauravbhola.viewpagerslidingheader.ViewPagerSlidingHeaderRootView;
import com.gauravbhola.viewpagerslidingheader.SlidingHeaderCallbacks;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

public class SimpleSlidingDrawerWithActionBarSliding extends AppCompatActivity implements SlidingHeaderCallbacks, SlidingHeaderActivityCallbacks {
    Toolbar mToolbar;
    ViewPager mPager;
    View mPagerContainer;
    SlidingTabLayout mSlidingTabLayout;
    View mImageView;
    ViewPagerSlidingHeaderRootView mRootView;
    MyAdapter mAdapter = new MyAdapter(getSupportFragmentManager());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerContainer = findViewById(R.id.container_viewpager);
        mPager.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_dark));
        mRootView = (ViewPagerSlidingHeaderRootView) findViewById(R.id.view_root);
        mRootView.registerCallbacks(this);
        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.slidingtab_layout);
        mImageView = findViewById(R.id.image);

        setupActionBar();
        setupViewPager();
        prepareViewPagerSlidingHeader();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    int getContentView() {
        return R.layout.activity_simple_sliding_drawer;
    }

    void prepareViewPagerSlidingHeader() {
        mRootView.initHeaderViewPager(mToolbar, mImageView, mSlidingTabLayout, mPagerContainer);
        mRootView.setParallaxFactor(4);
        mRootView.registerHeaderListener(new ViewPagerSlidingHeaderRootView.HeaderSlideListener() {
            @Override
            public void onOpenPercentChanged(int openPercent, float translationY) {
                L.d("openPercent = " + openPercent);
                L.d("translation = " + translationY);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void setupViewPager() {
        mPager.setAdapter(mAdapter);
        mSlidingTabLayout.setCustomTabView(R.layout.layout_tab_indicator, android.R.id.text1);
        mSlidingTabLayout.setSelectedIndicatorColors(Color.parseColor("#ffffff"));
        mSlidingTabLayout.setViewPager(mPager);
    }

    public class MyAdapter extends FragmentStatePagerAdapter {
        public String[] mTitles = {"scrollView", "recyclerView", "SC", "RE", "SC", "RE"};
        Fragment[] mFragments = new Fragment[mTitles.length];

        public MyAdapter(FragmentManager fm) {
            super(fm);
            mFragments[0] = ContentFragment.newInstance(0);
            mFragments[1] = RecyclerViewFragment.newInstance();
            mFragments[2] = ContentFragment.newInstance(2);
            mFragments[3] = RecyclerViewFragment.newInstance();
            mFragments[4] = ContentFragment.newInstance(4);
            mFragments[5] = RecyclerViewFragment.newInstance();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }

        @Override
        public Fragment getItem(int i) {
            return mFragments[i];
        }

        public void updateInstance(Fragment fragment, int position) {
            mFragments[position] = fragment;
        }

        public boolean shouldDrawerMove() {
            return ((SlidingHeaderCallbacks)mFragments[mPager.getCurrentItem()]).shouldDrawerMove();
        }

        public void dispatchFling(MotionEvent ev1, MotionEvent ev2, float velx, float vely) {
            ((SlidingHeaderCallbacks)mFragments[mPager.getCurrentItem()]).dispatchFling(ev1, ev2, velx, vely);
        }

        @Override
        public int getCount() {
            return mTitles.length;
        }


    }

    @Override
    public boolean shouldDrawerMove() {
        return mAdapter.shouldDrawerMove();
    }

    @Override
    public void dispatchFling(MotionEvent ev1, MotionEvent ev2, float velx, float vely) {
        mAdapter.dispatchFling(ev1, ev2, velx, vely);
    }

    private void setupActionBar() {
        mToolbar = (Toolbar) findViewById(R.id.action_toolbar);
        if (mToolbar == null) {
            return;
        }
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void updateFragmentInstance(ContentFragment fragment, int position) {
        mAdapter.updateInstance(fragment, position);
    }

    @Override
    public ViewPagerSlidingHeaderRootView getRootView() {
        return mRootView;
    }
}
