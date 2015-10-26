package com.migapro.jiraclient.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.migapro.jiraclient.R;
import com.migapro.jiraclient.models.Issue;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class IssueRecyclerAdapter extends RecyclerView.Adapter<IssueRecyclerAdapter.ViewHolder> {

    private OnClickListener mOnlickListener;
    private ArrayList<Issue> mData;

    public interface OnClickListener {
        void onClick(String issueId, String timeSpent);
    }

    public IssueRecyclerAdapter() {
        mData = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_issue, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Issue issue = mData.get(position);

        holder.keyTextView.setText(issue.getKey());
        holder.summaryTextView.setText(issue.getFields().getSummary());
        holder.sendWorkLogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnlickListener != null) {
                    mOnlickListener.onClick(issue.getKey(), holder.timeSpent.getText().toString());
                }
            }
        });
        holder.timeSpentSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                holder.timeSpent.setText(progress + "m");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
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

    public void setOnClickListener(OnClickListener listener) {
        mOnlickListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.key) TextView keyTextView;
        @Bind(R.id.send_worklog) Button sendWorkLogButton;
        @Bind(R.id.summary) TextView summaryTextView;
        @Bind(R.id.timeSpent_seekbar) SeekBar timeSpentSeekbar;
        @Bind(R.id.timeSpent) TextView timeSpent;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
