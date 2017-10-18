package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.popularmovies.data.MovieContract.*;

/*
 * Database helper for the local movies database
 */
class MovieDbHelper extends SQLiteOpenHelper {

    /*
     * The on device file name for the database
     */
    private static final String DATABASE_FILE_NAME = "movie.db";

    /* Current version of the database */
    private static final int DATABASE_VERSION = 1;

    MovieDbHelper(Context context) {
        super(context, DATABASE_FILE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Method that generates the movie table.
     *
     * @param database the database to create the movie table in
     */
    private void createMovieTable(SQLiteDatabase database){
        final String SQL_CREATE_MOVIE_TABLE =

                "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +

                /* unique id from the moviedb for the key */
                MovieEntry.COLUMN_MOVIE_ID          + " INTEGER PRIMARY KEY ON CONFLICT REPLACE, " +

                MovieEntry.COLUMN_TITLE             + " STRING NOT NULL, " +
                MovieEntry.COLUMN_POSTER_PATH       + " STRING NOT NULL, " +
                MovieEntry.COLUMN_OVERVIEW          + " STRING NOT NULL, " +
                MovieEntry.COLUMN_AVERAGE_RATING    + " REAL NOT NULL, " +
                MovieEntry.COLUMN_RELEASE_DATE      + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_RUNTIME           + " INTEGER NOT NULL " +
                ");";

        database.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    /**
     * Method that generates the reviews table.
     *
     * @param database the database to create the movie table in
     */
    private void createReviewTable(SQLiteDatabase database) {
        final String SQL_CREATE_REVIEW_TABLE =

                "CREATE TABLE " + ReviewEntry.TABLE_NAME + " (" +
                ReviewEntry._ID                     + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                /* unique review id string from the moviedb for the key */
                ReviewEntry.COLUMN_REVIEW_ID        + " STRING, " +
                ReviewEntry.COLUMN_MOVIE_ID         + " INTEGER NOT NULL, " +
                ReviewEntry.COLUMN_REVIEW_CONTENT   + " STRING NOT NULL, " +
                ReviewEntry.COLUMN_REVIEW_AUTHOR    + " STRING NOT NULL, " +
                ReviewEntry.COLUMN_REVIEW_URL       + " STRING NOT NULL, " +
                " UNIQUE (" + ReviewEntry.COLUMN_REVIEW_ID + ") ON CONFLICT REPLACE" +
                ");";

        database.execSQL(SQL_CREATE_REVIEW_TABLE);
    }

    /**
     * Method that generates the trailers table.
     *
     * @param database the database to create the movie table in
     */
    private void createTrailersTable(SQLiteDatabase database) {
        final String SQL_CREATE_TRAILER_TABLE =

                "CREATE TABLE " + TrailerEntry.TABLE_NAME + " (" +

                TrailerEntry._ID                     + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                /* unique trailer id string  from the moviedb for the key */
                TrailerEntry.COLUMN_TRAILER_ID       + " STRING, " +
                TrailerEntry.COLUMN_MOVIE_ID         + " INTEGER NOT NULL, " +
                TrailerEntry.COLUMN_TRAILER_KEY      + " STRING NOT NULL, " +
                TrailerEntry.COLUMN_TRAILER_SITE     + " STRING NOT NULL, " +
                TrailerEntry.COLUMN_TRAILER_NAME     + " STRING NOT NULL, " +
                " UNIQUE (" + TrailerEntry.COLUMN_TRAILER_ID + ") ON CONFLICT REPLACE" +
                ");";

        database.execSQL(SQL_CREATE_TRAILER_TABLE);
    }

    /**
     * Method that generates the top rated movies table.
     *
     * @param database the database to create the top rated movies table in
     */
    private void createTopRatedTable(SQLiteDatabase database) {
        final String SQL_CREATE_TOP_RATED_TABLE =

                "CREATE TABLE " + TopRatedEntry.TABLE_NAME + " (" +
                        TopRatedEntry._ID                + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        TopRatedEntry.COLUMN_MOVIE_ID    + " INTEGER NOT NULL " +
                        ");";

        database.execSQL(SQL_CREATE_TOP_RATED_TABLE);
    }

    /**
     * Method that generates the popular movies table
     *
     * @param database the database to create the popular movies table in
     */
    private void createPopularTable(SQLiteDatabase database) {
        final String SQL_CREATE_POPULAR_TABLE =

                "CREATE TABLE " + PopularEntry.TABLE_NAME   + " (" +
                        PopularEntry._ID                    + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        PopularEntry.COLUMN_MOVIE_ID        + " INTEGER NOT NULL " +
                        ");";

        database.execSQL(SQL_CREATE_POPULAR_TABLE);
    }

    /**
     * Method that generates the favorite movies table
     *
     * @param database the database to create the favorite movies table in
     */
    private void createFavoriteTable(SQLiteDatabase database) {
        final String SQL_CREATE_FAVORITE_TABLE =

                "CREATE TABLE " + FavoriteEntry.TABLE_NAME  + " (" +
                        FavoriteEntry._ID                   + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        FavoriteEntry.COLUMN_MOVIE_ID       + " INTEGER NOT NULL " +
                        ");";

        database.execSQL(SQL_CREATE_FAVORITE_TABLE);
    }

    /**
     * Build the database - called if it doesn't exist already.
     * @param database the database being created
     */
    @Override
    public void onCreate(SQLiteDatabase database) {
        createMovieTable(database);
        createReviewTable(database);
        createTrailersTable(database);
        createPopularTable(database);
        createTopRatedTable(database);
        createFavoriteTable(database);
    }

    /**
     * Upgrade the database. The approach to upgrade the database here is to destroy everything and
     * start over.
     *
     * @param datebase the database being upgraded
     * @param oldVersion the old version number
     * @param newVersion the new version number
     */
    @Override
    public void onUpgrade(SQLiteDatabase datebase, int oldVersion, int newVersion) {
        datebase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        datebase.execSQL("DROP TABLE IF EXISTS " + ReviewEntry.TABLE_NAME);
        datebase.execSQL("DROP TABLE IF EXISTS " + TrailerEntry.TABLE_NAME);
        datebase.execSQL("DROP TABLE IF EXISTS " + FavoriteEntry.TABLE_NAME);
        datebase.execSQL("DROP TABLE IF EXISTS " + PopularEntry.TABLE_NAME);
        datebase.execSQL("DROP TABLE IF EXISTS " + TopRatedEntry.TABLE_NAME);

        onCreate(datebase);
    }
}
