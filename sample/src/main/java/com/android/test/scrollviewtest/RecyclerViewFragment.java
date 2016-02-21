package com.android.test.scrollviewtest;

import com.gauravbhola.viewpagerslidingheader.ViewPagerSlidingHeaderFragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

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
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListView = (RecyclerView) view.findViewById(R.id.recyclerview);
        final ArrayList<String> list = new ArrayList<>();
        int i = 100;
        do {
            list.add("item " + (i));
            i--;
        } while (i > 0);

        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mListView.setLayoutManager(mLayoutManager);
        mListView.setAdapter(new MyAdapter(getActivity().getApplicationContext(), list));
    }

    public static class MyAdapter extends RecyclerView.Adapter {
        private Context mContext;
        private List<String> mItems;

        public MyAdapter(Context context, List<String> items) {
            mContext = context;
            mItems = items;
        }

        public static class MyViewHolder extends RecyclerView.ViewHolder {
            private final TextView textView;

            public static MyViewHolder newInstance(View itemView) {
                TextView textView = (TextView) itemView.findViewById(android.R.id.text1);
                return new MyViewHolder(itemView, textView);
            }

            private MyViewHolder(View itemView, final TextView textView) {
                super(itemView);
                this.textView = textView;
                itemView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Toast.makeText(v.getContext(),
                                "OnClick :" + textView.getText(),
                                Toast.LENGTH_SHORT).show();

                    }
                });
            }

            public void setText(CharSequence text) {
                textView.setText(text);
            }
        }
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    android.R.layout.simple_list_item_1, parent, false);
            MyViewHolder vh = MyViewHolder.newInstance(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            String text = mItems.get(position);
            ((MyViewHolder) holder).setText(text);

        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }
    }

}
