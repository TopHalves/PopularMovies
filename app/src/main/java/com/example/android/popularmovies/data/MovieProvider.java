package com.example.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.android.popularmovies.data.MovieContract.*;

/*
 * Defines a ContentProvider for the local movie database. Only the methods required
 * for the operation of the application are defined.
 */
public class MovieProvider extends ContentProvider {

    public static final int CODE_MOVIE = 100;
    public static final int CODE_MOVIE_WITH_MOVIE_ID = 101;
    public static final int CODE_MOVIE_TOP_RATED = 110;
    public static final int CODE_MOVIE_POPULAR = 121;

    public static final int CODE_MOVIE_FAVORITE = 130;
    public static final int CODE_MOVIE_FAVORITE_WITH_MOVIE_ID = 131;

    public static final int CODE_REVIEW = 200;
    public static final int CODE_REVIEW_WITH_MOVIE_ID = 201;

    public static final int CODE_TRAILER = 300;
    public static final int CODE_TRAILER_WITH_MOVIE_ID = 301;

    private static UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mDbHelper;

    /**
     * Make an raw sql query for a LEFT JOIN on the movies table.
     *
     * @param from table to select query results from
     * @param onTable2 table of second ON column that should be equal
     * @param onColumn2 column of the second ON
     * @return the sql query for the join to the movie table
     */
    private static String makeMovieTableJoin(String from, String onTable2, String onColumn2){

        return new String().concat("SELECT ")
                .concat(MovieEntry.TABLE_NAME)
                .concat(".*")
                .concat(" FROM ")
                .concat(from)
                .concat(" LEFT JOIN ")
                .concat(MovieEntry.TABLE_NAME)
                .concat(" ON ")
                .concat(MovieEntry.TABLE_NAME).concat(".").concat(MovieEntry.COLUMN_MOVIE_ID)
                .concat(" = ")
                .concat(onTable2).concat(".").concat(onColumn2);
    }

    /**
     * Get a UriMatcher that matches the various permitted Uris.
     * @return the static Uri matcher for this  ContentProvider
     */
    public static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        /* Path or all movies in table */
        matcher.addURI(authority, MovieContract.PATH_MOVIES, CODE_MOVIE);

        /* Path for all top_rated movies */
        matcher.addURI(authority, MovieContract.PATH_MOVIES + "/" + MovieContract.PATH_TOP_RATED,
                CODE_MOVIE_TOP_RATED);

        /* Path or all popular movies */
        matcher.addURI(authority, MovieContract.PATH_MOVIES + "/" + MovieContract.PATH_POPULAR,
                CODE_MOVIE_POPULAR);

        /* Path for all favorite movies */
        matcher.addURI(authority, MovieContract.PATH_MOVIES + "/" + MovieContract.PATH_FAVORITE,
                CODE_MOVIE_FAVORITE);

        /* Path for favorite by movie id */
        matcher.addURI(authority, MovieContract.PATH_MOVIES + "/" + MovieContract.PATH_FAVORITE + "/#",
                CODE_MOVIE_FAVORITE_WITH_MOVIE_ID);

        /* Path for movie by movie id */
        matcher.addURI(authority, MovieContract.PATH_MOVIES + "/#", CODE_MOVIE_WITH_MOVIE_ID);

        /* Path for all reviews - used for bulk insert */
        matcher.addURI(authority, MovieContract.PATH_REVIEWS, CODE_REVIEW);

        /* Path for review by movie id */
        matcher.addURI(authority, MovieContract.PATH_REVIEWS + "/#", CODE_REVIEW_WITH_MOVIE_ID);

        /* Path for all trailers - used for bulk insert */
        matcher.addURI(authority, MovieContract.PATH_TRAILERS, CODE_TRAILER);

        /* Path for trailer by movie id */
        matcher.addURI(authority, MovieContract.PATH_TRAILERS + "/#", CODE_TRAILER_WITH_MOVIE_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    /**
     * Query the provider - only the query types for the application are implemented.
     *
     * @param uri the query Uri
     * @param projection the query projection
     * @param selection the query selection
     * @param selectionArgs the query selection args
     * @param sortOrder the required sort order of the results
     * @return a cursor of the result or null on failure
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        Cursor cursor;

        switch (sUriMatcher.match(uri)){
            case CODE_MOVIE: {
                cursor = mDbHelper.getReadableDatabase().query(
                        MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case CODE_MOVIE_WITH_MOVIE_ID: {

                String movieId = uri.getLastPathSegment();
                String[] selectionArguments = new String[]{movieId};

                cursor = mDbHelper.getReadableDatabase().query(
                        MovieEntry.TABLE_NAME,
                        projection,
                        MovieEntry.COLUMN_MOVIE_ID + " = ? ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);

                break;
            }
            case CODE_MOVIE_FAVORITE: {
                final String query = makeMovieTableJoin(
                        FavoriteEntry.TABLE_NAME,
                        FavoriteEntry.TABLE_NAME,
                        FavoriteEntry.COLUMN_MOVIE_ID);

                cursor = mDbHelper.getReadableDatabase().rawQuery(query, null);

                break;
            }
            case CODE_MOVIE_POPULAR: {
                final String query = makeMovieTableJoin(
                        PopularEntry.TABLE_NAME,
                        PopularEntry.TABLE_NAME,
                        PopularEntry.COLUMN_MOVIE_ID);

                cursor = mDbHelper.getReadableDatabase().rawQuery(query, null);

                break;
            }
            case CODE_MOVIE_TOP_RATED: {
                final String query = makeMovieTableJoin(
                        TopRatedEntry.TABLE_NAME,
                        TopRatedEntry.TABLE_NAME,
                        TopRatedEntry.COLUMN_MOVIE_ID);

                cursor = mDbHelper.getReadableDatabase().rawQuery(query, null);

                break;
            }
            case CODE_REVIEW_WITH_MOVIE_ID: {
                String movieId = uri.getLastPathSegment();

                String[] selectionArguments = new String[]{movieId};

                cursor = mDbHelper.getReadableDatabase().query(
                        ReviewEntry.TABLE_NAME,
                        projection,
                        ReviewEntry.COLUMN_MOVIE_ID + " = ? ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case CODE_TRAILER_WITH_MOVIE_ID: {
                String movieId = uri.getLastPathSegment();

                String[] selectionArguments = new String[]{movieId};

                cursor = mDbHelper.getReadableDatabase().query(
                        TrailerEntry.TABLE_NAME,
                        projection,
                        TrailerEntry.COLUMN_MOVIE_ID + " = ? ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case CODE_MOVIE_FAVORITE_WITH_MOVIE_ID: {
                String movieId = uri.getLastPathSegment();

                String[] selectionArguments = new String[]{movieId};

                cursor = mDbHelper.getReadableDatabase().query(
                        FavoriteEntry.TABLE_NAME,
                        projection,
                        TrailerEntry.COLUMN_MOVIE_ID + " = ? ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown Uri: " + uri.toString());
            }
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    /**
     * Bulk insert into the provider. This isn't implemented for favorites as these are never bulk
     * inserted
     *
     * @param uri the uri to insert to
     * @param values the array of values to insert
     * @return the number of insertions performed
     */
    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {

        final SQLiteDatabase writableDatabase = mDbHelper.getWritableDatabase();
        String bulkInsertTable;

        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIE: {
                bulkInsertTable = MovieEntry.TABLE_NAME;
                break;
            }
            case CODE_TRAILER: {
                bulkInsertTable = TrailerEntry.TABLE_NAME;
                break;
            }
            case CODE_REVIEW: {
                bulkInsertTable = ReviewEntry.TABLE_NAME;
                break;
            }
            case CODE_MOVIE_POPULAR: {
                bulkInsertTable = PopularEntry.TABLE_NAME;
                break;
            }
            case CODE_MOVIE_TOP_RATED: {
                bulkInsertTable = TopRatedEntry.TABLE_NAME;
                break;
            }

            default: {
                return super.bulkInsert(uri, values);
            }
        }

        writableDatabase.beginTransaction();
        int insertRowCount = 0;
        try {
            for (ContentValues value : values){
                long id = writableDatabase.insert(bulkInsertTable, null, value);
                if (id != -1){
                    insertRowCount++;
                }
            }
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
        }

        if (insertRowCount > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return insertRowCount;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    /**
     * Provide insertion of values into the favorites table only.
     *
     * @param uri the uri to insert to
     * @param values the value to insert
     * @return the uri that was inserted to
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase database = mDbHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIE_FAVORITE: {
                long id = database.insert(FavoriteEntry.TABLE_NAME, null, values);
                if (id != -1) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return uri;
            }
            default: {
                throw new UnsupportedOperationException("Unsupported Insert: " + uri.toString());
            }
        }
    }

    /**
     * Delete rows at the requested Uri.
     *
     * @param uri the Uri to delete from
     * @param selection the selection query of items to delete
     * @param selectionArgs the arguments of the defined selection
     * @return the number of items deleted
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        final SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int deleteCount = 0;

        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIE_POPULAR: {
                deleteCount = database.delete(
                    PopularEntry.TABLE_NAME,
                    selection,
                    selectionArgs);
                break;
            }
            case CODE_MOVIE_TOP_RATED: {
                deleteCount = database.delete(
                        TopRatedEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            }
            case CODE_MOVIE_FAVORITE_WITH_MOVIE_ID: {
                String movieId = uri.getLastPathSegment();

                String[] selectionArguments = new String[]{movieId};

                deleteCount = mDbHelper.getReadableDatabase().delete(
                        FavoriteEntry.TABLE_NAME,
                        TrailerEntry.COLUMN_MOVIE_ID + " = ? ",
                        selectionArguments);
                break;
            }
        }

        return deleteCount;
    }

    /**
     * There is no use case for updates so this is overridden as required but not implemented.
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        return 0;
    }


}
