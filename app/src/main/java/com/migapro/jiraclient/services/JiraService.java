package com.migapro.jiraclient.services;

import com.migapro.jiraclient.models.SearchIssue;
import com.migapro.jiraclient.models.SearchIssueResponse;
import com.migapro.jiraclient.models.Worklog;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Path;

public interface JiraService {

    @Headers({"Content-Type : application/json"})
    @POST("/rest/api/2/search/")
    Call<SearchIssueResponse> searchIssues(@Header("Authorization") String authorization,
                             @Body SearchIssue searchRequest);

    @Headers({"Content-Type : application/json"})
    @POST("/rest/api/2/issue/{issueId}/worklog/")
    Call<Worklog> addWorklog(@Header("Authorization") String authorization,
                                        @Path("issueId") String issueId,
                                        @Body Worklog addWorklogRequest);
}
