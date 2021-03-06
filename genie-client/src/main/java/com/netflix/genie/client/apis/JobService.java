/*
 *
 *  Copyright 2016 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package com.netflix.genie.client.apis;

import com.fasterxml.jackson.databind.JsonNode;
import com.netflix.genie.common.dto.Application;
import com.netflix.genie.common.dto.Cluster;
import com.netflix.genie.common.dto.Command;
import com.netflix.genie.common.dto.Job;
import com.netflix.genie.common.dto.JobExecution;
import com.netflix.genie.common.dto.JobRequest;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;

import java.util.List;
import java.util.Map;

/**
 * An interface that provides all methods needed for the Genie job client implementation.
 *
 * @author amsharma
 * @since 3.0.0
 */
public interface JobService {

    /**
     * Path to Jobs.
     */
    String JOBS_URL_SUFFIX = "/api/v3/jobs";

    /**
     * Method to submit a job to Genie.
     *
     * @param request The request object containing all the
     * @return A callable object.
     */
    @POST(JOBS_URL_SUFFIX)
    Call<Void> submitJob(@Body final JobRequest request);

    /**
     * Submit a job with attachments.
     *
     * @param request A JobRequest object containing all the details needed to run the job.
     * @param attachments A list of all the attachment files to be sent to the server.
     *
     * @return A callable object.
     */
    @Multipart
    @POST(JOBS_URL_SUFFIX)
    Call<Void> submitJobWithAttachments(
        @Part("request") JobRequest request,
        @Part List<MultipartBody.Part> attachments);

    /**
     * Method to get all jobs from Genie.
     *
     * @param options A map of query parameters to be used to filter the jobs.
     * @return A callable object.
     */
    @GET(JOBS_URL_SUFFIX)
    Call<JsonNode> getJobs(@QueryMap final Map<String, String> options);

    /**
     * Method to fetch a single job from Genie.
     *
     * @param jobId The id of the job to get.
     * @return A callable object.
     */
    @GET(JOBS_URL_SUFFIX + "/{id}")
    Call<Job> getJob(@Path("id") final String jobId);

    /**
     * Method to fetch the stdout of a job from Genie.
     *
     * @param jobId The id of the job whose stdout is desired.
     * @return A callable object.
     */
    @Streaming
    @GET(JOBS_URL_SUFFIX + "/{id}/output/stdout")
    Call<ResponseBody> getJobStdout(@Path("id") final String jobId);

    /**
     * Method to fetch the stderr of a job from Genie.
     *
     * @param jobId The id of the job whose stderr is desired.
     * @return A callable object.
     */
    @Streaming
    @GET(JOBS_URL_SUFFIX + "/{id}/output/stderr")
    Call<ResponseBody> getJobStderr(@Path("id") final String jobId);

    /**
     * Method to get Job status.
     *
     * @param jobId The id of the job whose status is desired.
     * @return A callable object.
     */
    @GET(JOBS_URL_SUFFIX + "/{id}/status")
    Call<JsonNode> getJobStatus(@Path("id") final String jobId);

    /**
     * Method to get the cluster information on which a job is run.
     *
     * @param jobId The id of the job.
     * @return A callable object.
     */
    @GET(JOBS_URL_SUFFIX + "/{id}/cluster")
    Call<Cluster> getJobCluster(@Path("id") final String jobId);

    /**
     * Method to get the command information on which a job is run.
     *
     * @param jobId The id of the job.
     * @return A callable object.
     */
    @GET(JOBS_URL_SUFFIX + "/{id}/command")
    Call<Command> getJobCommand(@Path("id") final String jobId);

    /**
     * Method to get the JobRequest for a job.
     *
     * @param jobId The id of the job.
     * @return A callable object.
     */
    @GET(JOBS_URL_SUFFIX + "/{id}/request")
    Call<JobRequest> getJobRequest(@Path("id") final String jobId);

    /**
     * Method to get the execution information for a job.
     *
     * @param jobId The id of the job.
     * @return A callable object.
     */
    @GET(JOBS_URL_SUFFIX + "/{id}/execution")
    Call<JobExecution> getJobExecution(@Path("id") final String jobId);

    /**
     * Method to get the Applications for a job.
     *
     * @param jobId The id of the job.
     * @return A callable object.
     */
    @GET(JOBS_URL_SUFFIX + "/{id}/applications")
    Call<List<Application>> getJobApplications(@Path("id") final String jobId);

    /**
     * Method to send a job kill request to Genie.
     *
     * @param jobId The id of the job.
     * @return A callable object.
     */
    @DELETE(JOBS_URL_SUFFIX + "/{id}")
    Call<Void> killJob(@Path("id") final String jobId);
}
