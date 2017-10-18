package com.example.android.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.sync.MovieSyncUtils;
import com.example.android.popularmovies.utilities.MoviePrefUtils;

/* The main activity - displays a list of movies */
public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        SharedPreferences.OnSharedPreferenceChangeListener,
        MovieListAdapter.MovieListAdapterOnClickHandler {

    /* The extra string id for movie_id - used to pass the id to the detail activity */
    public static final String MOVIE_ID_EXTRA = "movie_id_extra";

    public static final String[] MAIN_MOVIE_PROJECTION = {
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH
    };

    public static final int INDEX_MOVIE_ID = 0;
    public static final int INDEX_MOVIE_TITLE = 1;
    public static final int INDEX_MOVIE_POSTER_PATH = 2;

    /* Loader id for the background network task */
    private static final int MOVIE_LOADER_ID = 78;

    /* String ID for the URL argument used in the Loader */
    private static final String URI_TO_RETRIEVE_EXTRA = "url_to_retrieve_extra";

    /* Name of the parameters from Layout manger to save/restore */
    private static final String LAYOUT_POSITION = "scroll_position";

    /* stores the last layout position */
    private static int sPosition = RecyclerView.NO_POSITION;

    private RecyclerView mRecyclerView;

    /* Loading indicator */
    private ProgressBar mLoadingIndicator;

    /* Layout with the display to show if there is an error */
    private ConstraintLayout mProblemLayout;

    /* Layout with the display to show if favorites are empty */
    private ConstraintLayout mEmptyFavorites;

    //private GridView mMainGridView;
    private MovieListAdapter mMovieListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.main_movie_rv);

        mProblemLayout = (ConstraintLayout) findViewById(R.id.layout_problem_display);
        mEmptyFavorites = (ConstraintLayout) findViewById(R.id.layout_no_favorites_display);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        /* Use the appropriate number of columns for the orientation in layout manager */
        GridLayoutManager layoutManager =
                new GridLayoutManager(this, getResources().getInteger(R.integer.movie_list_columns));

        mRecyclerView.setLayoutManager(layoutManager);

        mMovieListAdapter = new MovieListAdapter(this, this);

        mRecyclerView.setAdapter(mMovieListAdapter);

        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);

        MovieSyncUtils.initialize(this);

        loadMovieData();
    }

    /* If the application is going away - store the current layout position */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        GridLayoutManager layoutManager = (GridLayoutManager)mRecyclerView.getLayoutManager();
        int position = layoutManager.findFirstVisibleItemPosition();

        outState.putInt(LAYOUT_POSITION, position);
    }

    /* restore the layout position - stored here so that it can be moved after the load */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
         if(savedInstanceState != null)
         {
             sPosition = savedInstanceState.getInt(LAYOUT_POSITION);
         }
    }

    @Override
    protected void onPause() {
        super.onPause();
        GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
        sPosition = layoutManager.findFirstVisibleItemPosition();
    }

    /**
     * Load the movie data into the RecylerView via the movie loader
     */
    private void loadMovieData() {
        Bundle dBQueryBundle = new Bundle();

        /* Get a Uri we can use for the current sort option */
        Uri queryUri = MoviePrefUtils.queryUriForCurrentSortOption(this);

        /* Make sure we have something to display - if not start the sync now */
        MovieSyncUtils.checkDataAvailableForTarget(this, queryUri);

        // Put the query URL into the bundle to pass to the loader
        dBQueryBundle.putString(URI_TO_RETRIEVE_EXTRA, queryUri.toString());

        LoaderManager loaderManager = getSupportLoaderManager();

        // Hide the error layout
        mProblemLayout.setVisibility(View.GONE);
        mEmptyFavorites.setVisibility(View.GONE);

        mLoadingIndicator.setVisibility(View.VISIBLE);

        loaderManager.restartLoader(MOVIE_LOADER_ID, dBQueryBundle, this);
    }

    /**
     * Called when the options menu is created. Sets to our custom menu.
     *
     * @param menu The menu being created
     * @return true if the menu creation was handled - false otherwise
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);

        return true;
    }

    /**
     * Called when a user selects an item from the options menu.
     *
     * @param item the item selected
     * @return true if the selection was handled - false otherwise
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Called when the sort order preference is changed. We must cancel any loader here and
     * reload the data for the new setting.
     *
     * @param sharedPreferences shared preferences that have changed
     * @param key               the string key of the changed preference
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (key.equals(getString(R.string.pref_sort_key))) {
            sPosition = RecyclerView.NO_POSITION;
            loadMovieData();
        }
    }

    /**
     * Called when the activity is destroyed.
     */
    @Override
    protected void onDestroy() {
        // Unregister the preference change listener
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {

        Uri movieQueryUri;

        switch (loaderId) {

            case MOVIE_LOADER_ID: {

                if (args.containsKey(URI_TO_RETRIEVE_EXTRA)) {
                    movieQueryUri = Uri.parse(args.getString(URI_TO_RETRIEVE_EXTRA));
                } else {
                    throw new IllegalArgumentException("No Uri provided to Loader");
                }
                return new CursorLoader(this,
                        movieQueryUri,
                        MAIN_MOVIE_PROJECTION,
                        null,
                        null,
                        null);
            }

            default: {
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
            }
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        mLoadingIndicator.setVisibility(View.GONE);

        if (data == null || data.getCount() == 0) {

            // Nothing valid to display at this point
            mMovieListAdapter.swapCursor(null);

            if (MoviePrefUtils.currentSortOrder(
                    getBaseContext()).equals(getBaseContext().getString(R.string.pref_sort_favorite))) {
                mEmptyFavorites.setVisibility(View.VISIBLE);
            } else {
                // Show the loading error text - most likely a network problem if here
                mProblemLayout.setVisibility(View.VISIBLE);
            }

            return;
        }

        mMovieListAdapter.swapCursor(data);

        if (sPosition == RecyclerView.NO_POSITION) sPosition = 0;

        mRecyclerView.setVisibility(View.VISIBLE);
        mRecyclerView.scrollToPosition(sPosition);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieListAdapter.swapCursor(null);
    }

    /**
     * Launch the details activity when the user clicks a movie.
     * @param movieId the id of the movie
     */
    @Override
    public void onClick(long movieId) {

        Intent detailIntent = new Intent(this, DetailActivity.class);

        // Pass the movie info as extra info in the detail activity intent
        detailIntent.putExtra(MOVIE_ID_EXTRA, movieId);

        // Start the detail activity
        startActivity(detailIntent);
    }
}