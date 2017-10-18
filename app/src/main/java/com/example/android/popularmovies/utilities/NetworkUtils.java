package com.example.android.popularmovies.utilities;

import android.net.Uri;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * These utility methods are used to form queries and communicate with the movies api service
 */

public final class NetworkUtils {

    /* The URL of the move db api root.*/
    private static final String MOVIE_DB_BASE_URL = "https://api.themoviedb.org";

    /* The URL of the image root */
    private static final String MOVIE_IMAGE_BASE = "http://image.tmdb.org";

    /* The path to images at movies db */
    private static final String MOVIE_DB_IMAGE_PATH = "t/p/w185";

    /* This is the api key from MovieDB. Must be replaced with a valid key to function.*/
    private static final String MOVIE_DB_KEY = REPLACE_WITH_VALID_API_KEY_TO_BUILD;

    /* The API version we are using. see https://developers.themoviedb.org/3/ for details.*/
    private static final String API_VERSION = "3";

    /* The following constants are the query parameters of the api */

    /* The api key query parameter - required for everything useful */
    private static final String API_QUERY_KEY = "api_key";

    /* The movie interface path */
    private static final String API_PATH_MOVIE = "movie";

    /* The top rated films path */
    private static final String API_PATH_TOP_RATED = "top_rated";

    /* The popular films path */
    private static final String API_PATH_POPULAR = "popular";

    /* The append extra query - used to request additional info for each movie */
    private static final String API_QUERY_APPEND_EXTRA = "append_to_response";

    /* The append extra parameter for reviews */
    private static final String API_PARAMETER_REVIEW = "reviews";

    /* The append extra parameter for videos */
    private static final String API_PARAMETER_VIDEOS = "videos";

    public static final URL POPULAR_URL = buildPopularMovieUrl();
    public static final URL TOP_RATED_URL = buildTopRatedMovieUrl();

    /**
     * Builds a URL to query the movies API based on the required search type.
     *
     * @param SearchType The type of movie search to build.
     * @return A url for a movies db query.
     */
    private static URL buildBasicMovieUrl(final String SearchType) {
        Uri movieQueryUri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                .appendPath(API_VERSION)
                .appendPath(API_PATH_MOVIE)
                .appendPath(SearchType)
                .appendQueryParameter(API_QUERY_KEY, MOVIE_DB_KEY)
                .build();

        try {
            return new URL(movieQueryUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Builds a URL to query the movies API for the list of popular movies.
     *
     * @return A url to query popular movies from movies db.
     */
    private static URL buildPopularMovieUrl() { return buildBasicMovieUrl(API_PATH_POPULAR); }

    /**
     * Builds a URL to query the movies API for the list of top rated movies.
     *
     * @return A url to query top rated movies from movies db.
     */
    private static URL buildTopRatedMovieUrl() {
        return buildBasicMovieUrl(API_PATH_TOP_RATED);
    }

    /**
     * Build a query Url for a specific movie at the movie db
     * @param moveid the id of the movie
     * @return the fully formed Url of the target movie
     */
    static URL buildSpecificMovieUrl(long moveid) {

        Uri movieQueryUri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                .appendPath(API_VERSION)
                .appendPath(API_PATH_MOVIE)
                .appendPath(String.valueOf(moveid))
                .appendQueryParameter(API_QUERY_APPEND_EXTRA,
                API_PARAMETER_REVIEW + "," + API_PARAMETER_VIDEOS)
                .appendQueryParameter(API_QUERY_KEY, MOVIE_DB_KEY)
                .build();

        try {
            return new URL(movieQueryUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get the response from a URL as a single String.
     *
     * @param urlToFetch The URL to fetch a HTTP response from.
     * @return The contents of the HTTP response or null if no response.
     * @throws IOException Related to network and stream reading.
     */
    public static String getResponseFromHttpUrl(URL urlToFetch) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) urlToFetch.openConnection();
        try {
            InputStream inputStream = urlConnection.getInputStream();

            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            String response = null;
            if (hasInput) {
                response = scanner.next();
            }
            scanner.close();
            return response;
        } finally {
            urlConnection.disconnect();
        }
    }

    /**
     * Converts a relative movie db image URL to an absolute one.
     * For example: http://image.tmdb.org/t/p/w185/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg
     *
     * @param relativePath The relative image path to convert.
     * @return The absolute path of the image.
     */
    public static URL convertRelativeImagePathToAbsolute(@NonNull final String relativePath){

        Uri imageUri = Uri.parse(MOVIE_IMAGE_BASE).buildUpon()
                .appendEncodedPath(MOVIE_DB_IMAGE_PATH)
                .appendEncodedPath(relativePath)
                .build();

        try {
            return new URL(imageUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
