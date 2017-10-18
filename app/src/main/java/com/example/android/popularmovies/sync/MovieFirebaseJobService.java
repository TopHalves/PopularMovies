package com.example.android.popularmovies.sync;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

/*
 * Defines the Firebase job that is run to periodically sync our local database
 */
public class MovieFirebaseJobService extends JobService {

    /**
     * Called when the scheduler starts this job.
     * @param job the job that started
     * @return true if the job is ongoing
     */
    @Override
    public boolean onStartJob(final JobParameters job) {

        final Bundle jobBundle = job.getExtras();

        /* We don't want to the sync on the main thread as it uses the network */
        AsyncTask<Void, Void, Void> SyncMovieDataTask = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... voids) {

                /* Unfortunately the job extras can only contain basic types i.e. not parcelables */
                String targetUrl = jobBundle.getString(MovieSyncArgs.EXTRA_TARGET_URL);
                String targetTable = jobBundle.getString(MovieSyncArgs.EXTRA_TARGET_TABLE);

                if (targetUrl == null || targetTable == null) return null;

                Context context = getApplicationContext();

                MovieSyncTask.syncMovieData(context, targetUrl, targetTable);

                /* inform the scheduler that the job is now finished */
                jobFinished(job, false);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                jobFinished(job, false);
            }
        };

        /* Start the actual task */
        SyncMovieDataTask.execute();

        return true;
    }

    /**
     * Called when the job is stopped due to the execution conditions no longer being met.
     * @param job the job that was stopped
     * @return true if the job should not be repeated
     */
    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }
}
