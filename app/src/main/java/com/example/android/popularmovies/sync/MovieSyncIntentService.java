package com.example.android.popularmovies.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/*
 * Defines an Intent service for immediate asynchronous synchronization of the local movies
 * database.
 */
public class MovieSyncIntentService extends IntentService {

    /* Tag for this service */
    private static final String MOVIE_SERVICE_NAME = "MovieSyncIntentService";

    public MovieSyncIntentService() {
        super(MOVIE_SERVICE_NAME);
    }

    /**
     * Handle the requested intent.
     * @param intent The incoming intent
     */
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if (intent != null){
            /* Get the synchronization arumnets from the intent extras */
            MovieSyncArgs syncArgs = intent.getParcelableExtra(MovieSyncArgs.MOVIE_SYNC_EXTRA_ARGS);

            MovieSyncTask.syncMovieData(this, syncArgs);
        }
    }
}
