package com.example.android.popularmovies.sync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

/*
 * Utility methods for initializing the periodic synchronization of the local database and
 * determining if the database requires to be immediately synchronized due to lack of data.
 * A mechanism for launching separate periodic synchronizations is provided to avoid hitting
 * the remote movie db service limit of 40 transactions in 10 seconds.
 */
public class MovieSyncUtils {

    /* Sync once per day - the update period for the popular and top rated on the movie db service */
    private static final int SYNC_INTERVAL_DAYS = 1;

    private static final int SYNC_INTERVAL_HOURS = (int) TimeUnit.DAYS.toHours(SYNC_INTERVAL_DAYS );
    private static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS);

    /* Provide some flixibility on the update time */
    private static final int SYNC_FLEXIBLE_SECONDS = SYNC_INTERVAL_SECONDS / 2;

    /* true if the jobs have already been initialized */
    private static boolean sInitialized;

    /* The arguments required for the popular movies synchronization */
    private static MovieSyncArgs POPULAR_UPDATE_ARGS = new MovieSyncArgs(
            "popular",
            MovieContract.PopularEntry.CONTENT_URI.toString(),
            NetworkUtils.POPULAR_URL.toString());

    /* The arguments required for the top rated movies synchronization */
    private static MovieSyncArgs TOP_RATED_UPDATE_ARGS = new MovieSyncArgs(
            "top_rated",
            MovieContract.TopRatedEntry.CONTENT_URI.toString(),
            NetworkUtils.TOP_RATED_URL.toString());

    /* The synchronization targets - other should be added here */
    private static MovieSyncArgs[] SYNC_TARGETS = {POPULAR_UPDATE_ARGS, TOP_RATED_UPDATE_ARGS};

    /**
     * Determines the arguments that should be used based on the remote database Url being requested
     * @param uri the remote Url to check
     * @return the sync aruments to be used for the Url
     */
   private static MovieSyncArgs syncArgsForUri(String uri){
        for (MovieSyncArgs args: SYNC_TARGETS) {
            if (args.getTargetDatabaseUri().equals(uri)) {
                return args;
            }
        }
        return null;
    }

    /**
     * Schedule a firebase job for the specified synchronization arguments.
     * @param context the current context
     * @param syncArgs the arguments that the job will use
     */
    private static void scheduleFirebaseJobDispatcherSync(@NonNull final Context context,
                                                  @NonNull final MovieSyncArgs syncArgs){

        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        Bundle jobExtraArgs = new Bundle();

        jobExtraArgs.putString(MovieSyncArgs.EXTRA_TARGET_URL, syncArgs.getTargetRemoteURL());
        jobExtraArgs.putString(MovieSyncArgs.EXTRA_TARGET_TABLE, syncArgs.getTargetDatabaseUri());

        Job syncMoviesJob = dispatcher.newJobBuilder()

                /* Pass the sync args as extras. Unfortunately the interface does not allow
                 * a parcelable to be passed in here, so we must use simple string extras instead
                 */
                .setExtras(jobExtraArgs)
                .setService(MovieFirebaseJobService.class)
                /* Use the tag from the sync args to identify the job */
                .setTag(syncArgs.getTagName())
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setTrigger(Trigger.executionWindow(
                        SYNC_INTERVAL_SECONDS,
                        SYNC_INTERVAL_SECONDS + SYNC_FLEXIBLE_SECONDS))
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setReplaceCurrent(true)
                .build();

        dispatcher.schedule(syncMoviesJob);
    }

    /**
     * If not already initialized create a sync job for each remote target.
     *
     * @param context the current Context
     */
    synchronized public static void initialize(@NonNull final Context context) {

        if (sInitialized) return;

        sInitialized = true;

        for (MovieSyncArgs args: SYNC_TARGETS)
        {
            MovieSyncUtils.scheduleFirebaseJobDispatcherSync(context, args);
        }
    }

    /**
     * Check if the requested target has an local data from the provider. If not sync
     * immediately.
     *
     * @param context the current context
     * @param target the local target Uri for the movie provider
     */
    synchronized public static void checkDataAvailableForTarget(@NonNull final Context context,
                                                                Uri target) {

        MovieSyncArgs args = syncArgsForUri(target.toString());

        if (args == null){
            return;
        }

        String[] projectionColumns = {MovieContract.MovieEntry.COLUMN_MOVIE_ID};

        /* Check to see if the provider has anything for our target query */
        Cursor cursor = context.getContentResolver().query(
                target,
                projectionColumns,
                null,
                null,
                null);

        if (null == cursor || cursor.getCount() == 0) {
            startSyncNow(context, args);
        }

        /* Close the Cursor to avoid memory leaks */
        if (cursor != null) cursor.close();
    }

    /**
     * Start an immediate asynchronous synchronization by starting an the IntentService.
     * @param context The current context
     */
    private static void startSyncNow(@NonNull final Context context, MovieSyncArgs syncArgs) {
        Intent intentToSyncImmediately = new Intent(context, MovieSyncIntentService.class);

        intentToSyncImmediately.putExtra(MovieSyncArgs.MOVIE_SYNC_EXTRA_ARGS, syncArgs);

        context.startService(intentToSyncImmediately);
    }
}
