# ViewPagerSlidingHeader
A very light and helpfull android library for implementing ViewPagerSlidingHeader. Explicity focussing on Youtube-Gaming-App like sliding header.



# Usage

## Dependencies
You can get this library from jcenter

### build.gradle
Write the following dependency configuration to your build.gradle

```
repositories {
    jcenter()
}

dependencies {
    // Other dependencies are omitted
    compile 'com.gauravbhola.viewpagerslidingheader:library:VERSION'
}
```

You should replace VERSION to the appropriate version number like 0.0.8.

Then, click "sync" button to get the library using the configuration above.


## Layout

```
//All this stuff will be in reference to SampleApp's SimpleSlidingDrawerWithActionBarSliding
```

After adding the dependency, let's write layout file such as [res/layout/activity_simple_sliding_drawer.xml](https://github.com/gaurav414u/ViewPagerSlidingHeader/blob/master/sample/src/main/res/layout/activity_simple_sliding_drawer.xml).

```
<com.gauravbhola.viewpagerslidingheader.ViewPagerSlidingHeaderRootView
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"     
    android:layout_height="match_parent"
    android:id="@+id/view_root"
    android:background="#000000">

    <!--The initial adjustment of these elements depends upon you.
    But to get the desired results, you should follow whats there 
    in the Sample App. The RootView extends RelativeLayout, so all 
    the relative constraints will work here.-->
    <{YourToolbar/ActionBar}/>
    <{YourViewPager/ViewPagerContainer}/>
    <{YourHeader}/>
    <{YourTabLayout}/>

</com.gauravbhola.viewpagerslidingheader.ViewPagerSlidingHeaderRootView>
```

After configuring your layout, you can write the remaining integration in Acitivity.

## Acitivity code:

Make sure that your activity implements `SlidingHeaderCallbacks` and `SlidingHeaderActivityCallbacks`.

```
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
    //load fragments
    setupViewPager();
    prepareViewPagerSlidingHeader();
}
```

### Sliding Header View preparation
Here `mRootView` is the instance of `com.gauravbhola.viewpagerslidingheader.ViewPagerSlidingHeaderRootView` which you used in your layout file and similarily, mToolbar, mImageView, mSlidingTabLayout, mPagerContainer are the views which are used in the layout file.

```
public void setupViewPager() {
    mPager.setAdapter(mAdapter);
    mSlidingTabLayout.setCustomTabView(R.layout.layout_tab_indicator, android.R.id.text1);
    mSlidingTabLayout.setSelectedIndicatorColors(Color.parseColor("#ffffff"));
    mSlidingTabLayout.setViewPager(mPager);
}

//Call this, after setting up your views
void prepareViewPagerSlidingHeader() {
    mRootView.initHeaderViewPager(mToolbar, mImageView, mSlidingTabLayout, mPagerContainer);
    //parallax effect shows by what ratio should the header view (`mImageView`) move relative to the actual sliding
    //by default the parallax factor is 1
    mRootView.setParallaxFactor(4);
    mRootView.registerHeaderListener(new ViewPagerSlidingHeaderRootView.HeaderSlideListener() {
        @Override
        public void onOpenPercentChanged(int openPercent, float translationY) {
            //can do cool stuff here, like
            //changing the alpha of mImageView
            //transforming any view (Title, User image) in mToolbar as per the `openPercent`
        }
    });
}
```

Since your activity implements `SlidingHeaderCallbacks`. You need to implement following methods like this:
```
@Override
public boolean shouldDrawerMove() {
    return mAdapter.shouldDrawerMove();
}

@Override
public void dispatchFling(MotionEvent ev1, MotionEvent ev2, float velx, float vely) {
    mAdapter.dispatchFling(ev1, ev2, velx, vely);
}
```

Since your activity also implements `SlidingHeaderActivityCallbacks`. You need to implement following method like this:
```
@Override
public ViewPagerSlidingHeaderRootView getRootView() {
    return mRootView;
}
```



### The Fragment Pager Adapter
Now, make sure that the fragments you are using extend the `ViewPagerSlidingHeaderFragment`. MyAdapter should look something like this:
```
public class MyAdapter extends FragmentStatePagerAdapter {
    //Initialize the fragments and override the standard methods for PagerAdapter

    //these 2 functions are important, you need to pass these callbacks to the current active fragment
    public boolean shouldDrawerMove() {
        return ((SlidingHeaderCallbacks)mFragments[mPager.getCurrentItem()]).shouldDrawerMove();
    }

    public void dispatchFling(MotionEvent ev1, MotionEvent ev2, float velx, float vely) {
        ((SlidingHeaderCallbacks)mFragments[mPager.getCurrentItem()]).dispatchFling(ev1, ev2, velx, vely);
    }
}
```


## Fragment code

Please have a look at the [sample/src/main/java/com/android/test/scrollviewtest/ContentFragment.java](https://github.com/gaurav414u/ViewPagerSlidingHeader/blob/master/sample/src/main/java/com/android/test/scrollviewtest/ContentFragment.java)

```
public class ContentFragment extends ViewPagerSlidingHeaderFragment {
    //you should ensure that this view completely fills this fragment
    ScrollView mScrollView; //this can also be an instance of RecyclerView
    //intialisation code
    //get the scrollableView reference in onCreateView

    //and return this view here in this overridden method
    @Override
    public View getScrollableView() {
        return mScrollView;
    }
}
```

Having all of this done, your sliding header will work like a charm.
I know the coupling and the design is not good as of now. I will be working further to improve it in the coming days. It is my first open source android library.

# Developed By

* Gaurav Bhola - [gaurav414u@gmail.com](mailto:gaurav414u@gmail.com)

# Thanks

* Inspired by [ObservableScrollView](https://github.com/ksoichiro/Android-ObservableScrollView) by Soichiro Kashima.



License
=======

    Copyright 2015 Gaurav Bhola

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
