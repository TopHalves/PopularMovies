package com.example.android.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.widget.TextView;

import com.example.android.popularmovies.data.MovieContract;

/**
 * Activity for display a movie's details to the user. This activity is launched with an
 * extra identifying the required movie by its movie id.
 */
public class DetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>{

    public static final String[] MOVIE_DETAIL_PROJECTION = {
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_RUNTIME,
            MovieContract.MovieEntry.COLUMN_AVERAGE_RATING
    };

    public static final int INDEX_MOVIE_ID = 0;
    public static final int INDEX_MOVIE_TITLE = 1;
    public static final int INDEX_MOVIE_POSTER_PATH = 2;
    public static final int INDEX_MOVIE_RELEASE_DATE = 3;
    public static final int INDEX_MOVIE_COLUMN_OVERVIEW = 4;
    public static final int INDEX_MOVIE_COLUMN_RUNTIME = 5;
    public static final int INDEX_MOVIE_AVERAGE_RATING = 6;

    public static final String[] TRAILER_PROJECTION = {
            MovieContract.TrailerEntry._ID,
            MovieContract.TrailerEntry.COLUMN_TRAILER_NAME,
            MovieContract.TrailerEntry.COLUMN_TRAILER_SITE,
            MovieContract.TrailerEntry.COLUMN_TRAILER_KEY
    };

    public static final int INDEX_TRAILER_NAME = 1;
    public static final int INDEX_TRAILER_SITE = 2;
    public static final int INDEX_TRAILER_KEY = 3;

    public static final String[] REVIEW_PROJECTION = {
            MovieContract.ReviewEntry.COLUMN_REVIEW_AUTHOR,
            MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT
    };

    public static final int INDEX_REVIEW_AUTHOR = 0;
    public static final int INDEX_REVIEW_CONTENT = 1;

    public static final int MOVIE_DETAIL_LOADER = 120;
    public static final int MOVIE_REVIEW_LOADER = 121;
    public static final int MOVIE_TRAILER_LOADER = 122;

    private DetailListAdapter mDetailAdapter;

    private Uri mMovieUri;
    private Uri mReviewUri;
    private Uri mTrailerUri;

    /**
     * Activity lifecycle - activity is created. Setup the RecyclerView
     * @param savedInstanceState the previously saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RecyclerView mDetailRecyclerView;

        setContentView(R.layout.activity_detail);

        mDetailRecyclerView = (RecyclerView) findViewById(R.id.detail_rv) ;

        /* Using a staggered layout gives a more pleasing appearance on larger devices
         * We also adjust the number of columns depending on the screen size
         */
        StaggeredGridLayoutManager layoutManager =
                new StaggeredGridLayoutManager(
                        getResources().getInteger(R.integer.detail_list_columns),
                        StaggeredGridLayoutManager.VERTICAL);

        mDetailRecyclerView.setLayoutManager(layoutManager);

        /* Set the specialized adapter for the details */
        mDetailAdapter = new DetailListAdapter(this, this);

        mDetailRecyclerView.setAdapter(mDetailAdapter);

        Intent launchIntent = getIntent();

        /* Get the movie that we are display details about */
        if(launchIntent.hasExtra(MainActivity.MOVIE_ID_EXTRA)){
            long movieId = launchIntent.getLongExtra(MainActivity.MOVIE_ID_EXTRA, 0);

            mMovieUri = MovieContract.MovieEntry.buildMovieUriWithId(movieId);

            mReviewUri = MovieContract.ReviewEntry.buildReviewUriWithId(movieId);

            mTrailerUri = MovieContract.TrailerEntry.buildTrailerUriWithId(movieId);

            /* We define three loaders for:
             *       - the movie details
             *       - the trailers
             *       - the reviews
             * Each cursor is passed to the DetailListAdapter
             */
            getSupportLoaderManager().initLoader(MOVIE_DETAIL_LOADER, null, this);
            getSupportLoaderManager().initLoader(MOVIE_TRAILER_LOADER, null, this);
            getSupportLoaderManager().initLoader(MOVIE_REVIEW_LOADER, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {

        String projection[];
        Uri contentUri;

        switch (loaderId) {
            case MOVIE_DETAIL_LOADER: {
                contentUri = mMovieUri;
                projection = MOVIE_DETAIL_PROJECTION;
                break;
                }
            case MOVIE_REVIEW_LOADER: {
                contentUri = mReviewUri;
                projection = REVIEW_PROJECTION;
                break;
            }
            case MOVIE_TRAILER_LOADER: {
                contentUri = mTrailerUri;
                projection = TRAILER_PROJECTION;
                break;
            }
            default: {
                throw new IllegalArgumentException("Unsupported Loader ID");
            }
        }
        return new CursorLoader(
                this,
                contentUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {

            switch (loader.getId()) {
                case MOVIE_DETAIL_LOADER: {
                    mDetailAdapter.swapMovieDetailsCursor(data);

                    /* The Detail adapter doesn't handle the movie title - set it here*/
                    setDetailMovieTitle(data);
                    break;
                }
                case MOVIE_TRAILER_LOADER: {
                    mDetailAdapter.swapTrailerCursor(data);
                    break;
                }
                case MOVIE_REVIEW_LOADER: {
                    mDetailAdapter.swapReviewsCursor(data);
                    break;
                }
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

    /**
     * Sets the movie title text
     * @param movieCursor the movie cursor
     */
    private void setDetailMovieTitle(Cursor movieCursor) {
        TextView titleTextView = (TextView) findViewById(R.id.film_title_tv);

        titleTextView.setText(movieCursor.getString(INDEX_MOVIE_TITLE));
    }
}
