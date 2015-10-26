package com.migapro.jiraclient.ui.adapters;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.migapro.jiraclient.BR;
import com.migapro.jiraclient.R;
import com.migapro.jiraclient.models.Issue;

import java.util.ArrayList;

public class IssueRecyclerAdapter extends RecyclerView.Adapter<IssueRecyclerAdapter.BindingHolder> {

    private ArrayList<Issue> mData;

    public IssueRecyclerAdapter() {
        mData = new ArrayList<>();
    }

    @Override
    public BindingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_issue, parent, false);
        BindingHolder holder = new BindingHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(BindingHolder holder, int position) {
        Issue issue = mData.get(position);
        holder.getBinding().setVariable(BR.issue, issue);
        holder.getBinding().executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public ArrayList<Issue> getData() {
        return mData;
    }

    public void setData(ArrayList<Issue> data) {
        mData = data;
    }

    public static class BindingHolder extends RecyclerView.ViewHolder {

        private ViewDataBinding binding;

        public BindingHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        public ViewDataBinding getBinding() {
            return binding;
        }
    }
}
