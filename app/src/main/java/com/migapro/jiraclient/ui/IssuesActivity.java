package com.migapro.jiraclient.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.migapro.jiraclient.R;
import com.migapro.jiraclient.Util.Util;
import com.migapro.jiraclient.models.SearchIssue;
import com.migapro.jiraclient.models.SearchIssueResponse;
import com.migapro.jiraclient.models.Worklog;
import com.migapro.jiraclient.services.JiraService;
import com.migapro.jiraclient.services.ServiceGenerator;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class IssuesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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

        retrieveIssues();
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
