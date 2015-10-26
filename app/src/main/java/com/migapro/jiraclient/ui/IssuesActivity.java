package com.migapro.jiraclient.ui;

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

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class IssuesActivity extends AppCompatActivity {

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.recyclerview) RecyclerView recyclerView;

    private IssueRecyclerAdapter mAdapter;

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
                Worklog request = new Worklog();
                request.setTimeSpent("5m");
                request.setComment("Executed from Android app");
                request.setStarted("2015-10-25T09:40:50.877-0500");

                addWorklog("key-1", request);
            }
        });

        mAdapter = new IssueRecyclerAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new IssueRecyclerAdapter();
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
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("JIRA", "fail");
            }
        });
    }

    private void addWorklog(String key, Worklog request) {
        JiraService jiraService = ServiceGenerator.generateService();
        String basicAuth = Util.generateBase64Credentials();

        Call<Worklog> responseCall = jiraService.addWorklog(basicAuth, key, request);
        responseCall.enqueue(new Callback<Worklog>() {
            @Override
            public void onResponse(Response<Worklog> response, Retrofit retrofit) {
                Log.d("JIRA", String.valueOf(response.isSuccess()));
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("JIRA", "fail");
            }
        });
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
}
