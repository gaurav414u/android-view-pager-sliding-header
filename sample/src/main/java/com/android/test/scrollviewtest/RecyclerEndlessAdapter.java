package com.android.test.scrollviewtest;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.List;

public class RecyclerEndlessAdapter extends RecyclerView.Adapter {
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private String mError = null;

    public static interface OnLoadMoreListener {
        void onLoadMore();
    }

    private List<String> items;

    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private boolean mLoadingEnabled = true;
    private OnLoadMoreListener onLoadMoreListener;

    public RecyclerEndlessAdapter(final List<String> items, RecyclerView recyclerView) {
        this.items = items;

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView,
                                       int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    totalItemCount = layoutManager.getItemCount();
                    if (layoutManager instanceof LinearLayoutManager) {
                        lastVisibleItem = ((LinearLayoutManager)layoutManager).findLastVisibleItemPosition();
                    } else if (layoutManager instanceof GridLayoutManager) {
                        lastVisibleItem = ((GridLayoutManager)layoutManager).findLastVisibleItemPosition();
                    } else {
                        throw new RuntimeException("layoutManager must be either LinearLayoutManager or GridLayoutManager");
                    }

                    if (mLoadingEnabled && !loading
                            && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        // End has been reached
                        // Do something
                        if (onLoadMoreListener != null) {
                            mError = null;
                            if (items.size() > 0 && items.get(items.size() - 1) != null) {
                                RecyclerEndlessAdapter.this.items.add(null);
                                notifyItemInserted(RecyclerEndlessAdapter.this.items.size() - 1);
                            }
                            onLoadMoreListener.onLoadMore();
                        }
                        loading = true;
                    }
                }
            });
        }
    }

    public void setLoadingEnabled(boolean loadingEnabled) {
        mLoadingEnabled = loadingEnabled;
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    android.R.layout.simple_list_item_1, parent, false);
            vh = MyViewHolder.newInstance(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.progress_item, parent, false);
            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder) {
            String text = items.get(position);
            ((MyViewHolder) holder).setText(text);

        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
            if (mError == null) {
                ((ProgressViewHolder) holder).errorTv.setVisibility(View.GONE);
                ((ProgressViewHolder) holder).progressBar.setVisibility(View.VISIBLE);
            } else {
                ((ProgressViewHolder) holder).errorTv.setVisibility(View.VISIBLE);
                ((ProgressViewHolder) holder).progressBar.setVisibility(View.GONE);
                ((ProgressViewHolder) holder).errorTv.setText(mError);
            }
        }
    }

    public void setLoaded(List<String> newdata, String error) {
        if (error == null) {
            items.remove(items.size() - 1);
            notifyItemRemoved(items.size());
            this.items.addAll(newdata);
        } else {
            mError = error;
            notifyItemInserted(items.size() - 1);
        }
        loading = false;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }


    //
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        public static MyViewHolder newInstance(View itemView) {
            TextView textView = (TextView) itemView.findViewById(android.R.id.text1);
            return new MyViewHolder(itemView, textView);
        }

        private MyViewHolder(View itemView, final TextView textView) {
            super(itemView);
            this.textView = textView;
            itemView.setOnClickListener(new OnClickListener() {

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

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;
        public TextView errorTv;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar1);
            errorTv = (TextView) v.findViewById(R.id.text_error_message);
        }
    }
}