package com.migapro.jiraclient.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.migapro.jiraclient.R;
import com.migapro.jiraclient.Util.Util;
import com.migapro.jiraclient.models.Issue;
import com.migapro.jiraclient.models.SearchIssue;
import com.migapro.jiraclient.models.SearchIssueResponse;
import com.migapro.jiraclient.models.Worklog;
import com.migapro.jiraclient.services.JiraService;
import com.migapro.jiraclient.services.ServiceGenerator;
import com.migapro.jiraclient.ui.adapters.IssueRecyclerAdapter;
import com.migapro.jiraclient.ui.dialogs.FindIssueDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class IssuesActivity extends AppCompatActivity implements IssueRecyclerAdapter.OnClickListener,
        FindIssueDialog.OnAddIssueSubmitListener {

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.recyclerview) RecyclerView recyclerView;

    private IssueRecyclerAdapter mAdapter;
    private ArrayList<Issue> mIssues;
    private Set<String> mIssueIds;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issues);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FindIssueDialog findIssueDialog = new FindIssueDialog();
                findIssueDialog.show(getFragmentManager(), "fragment_add_issue");
            }
        });

        mAdapter = new IssueRecyclerAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new IssueRecyclerAdapter();
        mAdapter.setOnClickListener(this);
        recyclerView.setAdapter(mAdapter);

        if (savedInstanceState == null) {
            initIssueIds();
            retrieveIssues(generateJql());
        }
        else {
            Gson gson = new Gson();
            String issuesJson = savedInstanceState.getString("issues");
            ArrayList<Issue> issues = gson.fromJson(issuesJson, new TypeToken<ArrayList<Issue>>() {}.getType());
            if (issues == null || issues.isEmpty()) {
                initIssueIds();
                retrieveIssues(generateJql());
            }
            else {
                mAdapter.setData(issues);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    private void initIssueIds() {
        SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
        mIssueIds = sp.getStringSet("issueIds", new HashSet<String>());
    }

    private String generateJql() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("key in (");
        int i = 0;
        for (String issueId : mIssueIds) {
            stringBuilder.append(issueId);
            i++;
            if (i != mIssueIds.size()) {
                stringBuilder.append(", ");
            }
        }
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    private void retrieveIssues(String jql) {
        showProgressDialog();

        SearchIssue request = new SearchIssue();
        request.setJql(jql);
        request.setStartAt(0);
        request.setMaxResults(20);

        JiraService jiraService = ServiceGenerator.generateService();
        String basicAuth = Util.generateBase64Credentials();

        Call<SearchIssueResponse> responseCall = jiraService.searchIssues(basicAuth, request);
        responseCall.enqueue(new Callback<SearchIssueResponse>() {
            @Override
            public void onResponse(Response<SearchIssueResponse> response, Retrofit retrofit) {
                Log.d("JIRA", String.valueOf(response.isSuccess()));
                if (response.isSuccess()) {
                    mIssues = response.body().getIssues();
                    if (mIssues == null) {
                        mIssues = new ArrayList<Issue>();
                    }
                    mAdapter.setData(mIssues);
                    mAdapter.notifyDataSetChanged();
                }
                dismissProgressDialog();
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("JIRA", "fail");
                dismissProgressDialog();
            }
        });
    }

    @Override
    public void onClick(String issueId, String timeSpent) {
        Worklog request = new Worklog();
        request.setTimeSpent(timeSpent);
        //request.setComment("Executed from Android app");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        Date today = new Date();
        request.setStarted(dateFormat.format(today));

        addWorklog(issueId, request);
    }

    private void addWorklog(String key, Worklog request) {
        showProgressDialog();

        JiraService jiraService = ServiceGenerator.generateService();
        String basicAuth = Util.generateBase64Credentials();

        Call<Worklog> responseCall = jiraService.addWorklog(basicAuth, key, request);
        responseCall.enqueue(new Callback<Worklog>() {
            @Override
            public void onResponse(Response<Worklog> response, Retrofit retrofit) {
                Log.d("JIRA", String.valueOf(response.isSuccess()));
                dismissProgressDialog();
                showWorklogToast(response.isSuccess());
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("JIRA", "fail");
                dismissProgressDialog();
                showWorklogToast(false);
            }
        });
    }

    private void showWorklogToast(boolean isSuccess) {
        Toast.makeText(this,
                isSuccess ? "Successfully logged work" : "Failed to log work",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Gson gson = new Gson();
        String issuesJson = gson.toJson(mAdapter.getData());
        outState.putString("issues", issuesJson);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading");
            mProgressDialog.setCancelable(false);
        }
        mProgressDialog.show();
    }

    private void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void OnAddIssueSubmit(String issueId) {
        SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
        mIssueIds.add(issueId);
        sp.edit().putStringSet("issueIds", mIssueIds).apply();
        retrieveIssues(generateJql());
    }
}
