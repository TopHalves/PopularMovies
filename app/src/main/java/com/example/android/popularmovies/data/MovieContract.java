package com.example.android.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Class defines the tables and their respective column names for the movie db
 */
public class MovieContract {

    /*
     * Defines the content authority for the content provider - unique for this application
     */
    static final String CONTENT_AUTHORITY = "com.example.android.popularmovies";

    /*
     * Defines the base URI for interacting with the content provider
     */
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /*
     * We define five main paths which are associated with the three tables defined
     *
     * PATH_MOVIES - provides access to the movies table
     * PATH_TRAILERS - provides access to the trailers table
     * PATH_REVIEWS - provides access to the reviews table
     * PATH_FAVORITES - provides access to the favorites table
     * PATH_TOP_RATED - provides access to the table of top rated movies
     *
     */
    static final String PATH_MOVIES = "movies";
    static final String PATH_POPULAR = "popular";
    static final String PATH_TOP_RATED = "top_rated";
    static final String PATH_FAVORITE = "favorites";
    static final String PATH_TRAILERS = "trailers";
    static final String PATH_REVIEWS = "reviews";

    /* We require this column name is the same in every table */
    public static final String GLOBAL_COLUMN_MOVIE_ID = "movie_id";

    /**
     * Class that defines the table for movies
     */
    public static final class MovieEntry implements BaseColumns{

        /* The base URI for the movies table */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIES)
                .build();

        /* The table name as used in the database itself */
        static final String TABLE_NAME = "movies";

        /* The movies db id for the movie - unique */
        public static final String COLUMN_MOVIE_ID = GLOBAL_COLUMN_MOVIE_ID;

        /* The movie title */
        public static final String COLUMN_TITLE = "title";

        /* The movie poster image Url */
        public static final String COLUMN_POSTER_PATH = "poster_path";

        /* The long text summary of the movie */
        public static final String COLUMN_OVERVIEW = "overview";

        /* The release data of the move - stored as milliseconds since epoch */
        public static final String COLUMN_RELEASE_DATE = "release_date";

        /* The overall rating average out of ten */
        public static final String COLUMN_AVERAGE_RATING = "average_rating";

        /* The movie runtime in minutes */
        public static final String COLUMN_RUNTIME = "runtime";

        /**
         * Builds a Uri to for use in queries that involve a single movie identified by the movie id.
         * We use the same id as the moviedb for consistency.
         * *
         * @param id Movie id of the individual movie
         * @return Uri to query an individual movie in the movies table
         */
        public static Uri buildMovieUriWithId(long id) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(id))
                    .build();
        }
    }

    public static final class TopRatedEntry implements BaseColumns{
        /* The base URI for the top rated table */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIES)
                .appendPath(PATH_TOP_RATED)
                .build();

        /* The table name as used in the database itself */
        static final String TABLE_NAME = "top_rated";

        /* The movies db id for the movie - unique */
        static final String COLUMN_MOVIE_ID = GLOBAL_COLUMN_MOVIE_ID;
    }

    public static final class PopularEntry implements BaseColumns{
        /* The base URI for the top rated table */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIES)
                .appendPath(PATH_POPULAR)
                .build();

        /* The table name as used in the database itself */
        static final String TABLE_NAME = "popular";

        /* The movies db id for the movie - unique */
        static final String COLUMN_MOVIE_ID = GLOBAL_COLUMN_MOVIE_ID;
    }

    public static final class FavoriteEntry implements BaseColumns{
        /* The base URI for the favorite table */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIES)
                .appendPath(PATH_FAVORITE)
                .build();

        /* The table name as used in the database itself */
        static final String TABLE_NAME = "favorite";

        /* The movies db id for the movie - unique */
        public static final String COLUMN_MOVIE_ID = GLOBAL_COLUMN_MOVIE_ID;

        /**
         * Builds a Uri to for use in queries that involve a single favorite movie
         * identified by the movie id. We use the same id as the moviedb for consistency.
         * *
         * @param id Movie id of the individual movie
         * @return Uri to query an individual favorite movie in the favorites table
         */
        public static Uri buildFavoriteUriWithId(long id) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(id))
                    .build();
        }
    }

    /**
     * Class that defines the table for trailers
     */
    public static final class TrailerEntry implements BaseColumns{

        /* The base URI for the trailer table */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_TRAILERS)
                .build();

        /* The table name as used in the database itself */
        static final String TABLE_NAME = "trailers";

        /* The trailer moviesdb trailer id */
        public static final String COLUMN_TRAILER_ID = "trailer_id";

        /* The movies db id that this trailer if for - MAY NOT BE UNIQUE in this table */
        public static final String COLUMN_MOVIE_ID = GLOBAL_COLUMN_MOVIE_ID;

        /* The trailer key */
        public static final String COLUMN_TRAILER_KEY = "key";

        /* The name of the trailer */
        public static final String COLUMN_TRAILER_NAME = "name";

        /* The site that hosts the trailer */
        public static final String COLUMN_TRAILER_SITE = "site";

        /**
         * Builds a Uri to for use in queries that involve all trailers that match a movie id
         * We use the same id as the moviedb for consistency.
         * *
         * @param id Movie id of the individual movie
         * @return Uri to query all trailers that match the movie id
         */
        public static Uri buildTrailerUriWithId(long id) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(id))
                    .build();
        }
    }

    /**
     * Class that defines the table for reviews
     */
    public static final class ReviewEntry implements BaseColumns{

        /* The base URI for the review table */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_REVIEWS)
                .build();

        /* The table name as used in the database itself */
        static final String TABLE_NAME = "reviews";

        /* The movie db id */
        public static final String COLUMN_REVIEW_ID = "review_id";

        /* The movies db id that this trailer if for - MAY NOT BE UNIQUE in this table */
        public static final String COLUMN_MOVIE_ID = GLOBAL_COLUMN_MOVIE_ID;

        /* The review author */
        public static final String COLUMN_REVIEW_AUTHOR = "author";

        /* The review text */
        public static final String COLUMN_REVIEW_CONTENT = "review";

        /* The review url */
        public static final String COLUMN_REVIEW_URL = "url";

        /**
         * Builds a Uri to for use in queries that involve all reviews that match a movie id
         * We use the same id as the moviedb for consistency.
         * *
         * @param id Movie id of the individual movie
         * @return Uri to query all reviews that match the movie id
         */
        public static Uri buildReviewUriWithId(long id) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(id))
                    .build();
        }
    }
}
