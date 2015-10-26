package com.migapro.jiraclient.ui.dialogs;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.migapro.jiraclient.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FindIssueDialog extends DialogFragment {

    @Bind(R.id.issue_id_edditText) EditText issueIdEditText;
    @Bind(R.id.find_button) Button addIssueButton;

    private OnAddIssueSubmitListener mListener;

    public interface OnAddIssueSubmitListener {
        void OnAddIssueSubmit(String issueId);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find_issue, container);
        ButterKnife.bind(this, view);

        addIssueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String issueId = issueIdEditText.getText().toString();
                if (!issueId.isEmpty()) {
                    if (mListener != null) {
                        mListener.OnAddIssueSubmit(issueId);
                    }
                    dismiss();
                }
            }
        });

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListener = (OnAddIssueSubmitListener) activity;
    }
}
