package com.android.test.scrollviewtest;

import com.gauravbhola.viewpagerslidingheader.ViewPagerSlidingHeaderFragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by gauravbhola on 18/12/15.
 */
public class RecyclerViewFragment extends ViewPagerSlidingHeaderFragment {
    public static final String TAG = RecyclerViewFragment.class.getName();
    RecyclerView mListView;

    public static RecyclerViewFragment newInstance() {
        Bundle args = new Bundle();
        RecyclerViewFragment fragment = new RecyclerViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View getScrollableView() {
        return mListView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
    }

    LinearLayoutManager mLayoutManager;
    Handler handler = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_recyclerview, null);
        mListView = (RecyclerView) v.findViewById(R.id.recyclerview);
        //mListView.setScrollViewCallbacks(this);

        final ArrayList<String> list = new ArrayList<>();
        int i = 100;
        do {
            list.add("item " + (i));
            i--;
        } while (i > 0);

        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mListView.setLayoutManager(mLayoutManager);
        final RecyclerEndlessAdapter mAdapter = new RecyclerEndlessAdapter(list, mListView);

        mAdapter.setOnLoadMoreListener(new RecyclerEndlessAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                //add null , so the adapter will check view_type and show progress bar at bottom
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //prepare dummy data
                        int start = list.size();
                        int end = start + 100;

                        ArrayList<String> newData = new ArrayList<String>();
                        for (int i = start + 1; i <= end; i++) {
                            newData.add(i + "");
                        }
                        Random r = new Random();
                        boolean showError = r.nextBoolean();
                        if (showError) {
                            mAdapter.setLoaded(null, "some error");
                        } else {
                            mAdapter.setLoaded(newData, null);
                        }
                        //or you can add all at once but do not forget to call mAdapter.notifyDataSetChanged();
                    }
                }, 2000);

            }
        });

        mListView.setAdapter(mAdapter);
        return v;
    }
}
