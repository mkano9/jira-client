package com.migapro.jiraclient.ui;

import android.app.ProgressDialog;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class IssuesActivity extends AppCompatActivity implements IssueRecyclerAdapter.OnClickListener {

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.recyclerview) RecyclerView recyclerView;

    private IssueRecyclerAdapter mAdapter;
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

            }
        });

        mAdapter = new IssueRecyclerAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new IssueRecyclerAdapter();
        mAdapter.setOnClickListener(this);
        recyclerView.setAdapter(mAdapter);

        if (savedInstanceState == null) {
            retrieveIssues();
        }
        else {
            Gson gson = new Gson();
            String issuesJson = savedInstanceState.getString("issues");
            ArrayList<Issue> issues = gson.fromJson(issuesJson, new TypeToken<ArrayList<Issue>>() {}.getType());
            if (issues == null || issues.isEmpty()) {
                retrieveIssues();
            }
            else {
                mAdapter.setData(issues);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    private void retrieveIssues() {
        SearchIssue request = new SearchIssue();
        request.setJql("key in (key-1, key-2)");
        request.setStartAt(0);
        request.setMaxResults(10);

        JiraService jiraService = ServiceGenerator.generateService();
        String basicAuth = Util.generateBase64Credentials();

        Call<SearchIssueResponse> responseCall = jiraService.searchIssues(basicAuth, request);
        responseCall.enqueue(new Callback<SearchIssueResponse>() {
            @Override
            public void onResponse(Response<SearchIssueResponse> response, Retrofit retrofit) {
                Log.d("JIRA", String.valueOf(response.isSuccess()));
                if (response.isSuccess()) {
                    mAdapter.setData(response.body().getIssues());
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
        request.setComment("Executed from Android app");
        //request.setStarted("2015-10-25T09:40:50.877-0500");
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
}
