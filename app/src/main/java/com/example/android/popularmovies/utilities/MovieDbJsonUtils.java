package com.example.android.popularmovies.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.data.MovieContract.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

/**
 * Various helper methods for processing the movies db Json data.
 */
public final class MovieDbJsonUtils {

    private static final String TAG = MovieDbJsonUtils.class.getCanonicalName();

    /* For api details see: https://developers.themoviedb.org/3/movies/get-popular-movies */

    /* the results array object - array of movies */
    private static final String MDB_RESULTS = "results";

    /* string - relative URL path if the poster art */
    private static final String MDB_POSTER_PATH ="poster_path";

    /* string - overview text of movie */
    private static final String MDB_OVERVIEW = "overview";

    /* string - release date */
    private static final String MDB_RELEASE_DATE = "release_date";

    /* integer - id (within movie db) */
    private static final String MDB_ID = "id";

    /* string - original title */
    private static final String MDB_ORIGINAL_TITLE = "original_title";

    private static final String MDB_VOTE_AVERAGE = "vote_average";

    /* Error conditions for JSON queries */

    /* integer - status code */
    private static final String MDB_STATUS_CODE = "status_code";

    /* integer - runtime */
    private static final String MDB_RUNTIME = "runtime";

    private static final String MDB_REVIEWS = "reviews";

    private static final String MDB_REVIEW_ID = "id";

    private static final String MDB_REVIEW_AUTHOR = "author";

    private static final String MDB_REVIEW_URL = "url";

    private static final String MDB_REVIEW_CONTENT = "content";

    private static final String MDB_TRAILERS = "videos";

    private static final String MDB_TRAILER_ID = "id";

    private static final String MDB_TRAILER_KEY = "key";

    private static final String MDB_TRAILER_NAME = "name";

    private static final String MDB_TRAILER_SITE = "site";

    /**
     * Returns an ArrayList of ContentValues for all the movies specified in an ArrayList of
     * movie ids.
     * @param context the current context
     * @param movieStringArray an ArrayList of movie id strings
     * @return the list of ContentValues for each movie
     */
    public static ArrayList<ContentValues> movieContentArrayFromStringArray(Context context,
            ArrayList<String> movieStringArray){

        ArrayList<ContentValues> contentValues = new ArrayList<>();

        for (int i = 0; i < movieStringArray.size(); i++) {

            try {
                ContentValues movieContentValues = MovieDbJsonUtils.getMovieContentValuesFromJson(
                        context, movieStringArray.get(i));
                contentValues.add(movieContentValues);
            } catch (Exception e) {
                Log.w(TAG, "Failed to get movie data" + e.toString());
            }
        }

        return contentValues;
    }

    /**
     * Returns an ArrayList of review ContentValues from an ArrayList of strings contain JSON review
     * data.
     * @param context the current context
     * @param reviewStringArray the ArrayList containing review data as JSON
     * @return the ArrayList of review ContentValues
     */
    public static ArrayList<ContentValues> reviewContentArrayFromStringArray(Context context,
                                                                            ArrayList<String> reviewStringArray){

        ArrayList<ContentValues> contentValues = new ArrayList<>();

        for (int i = 0; i < reviewStringArray.size(); i++) {
            try {
                JSONObject movieJsonObject = validateJSONString(reviewStringArray.get(i));

                String movieId = movieJsonObject.getString(MDB_ID);

                JSONObject reviewJsonObject = movieJsonObject.getJSONObject(MDB_REVIEWS);
                JSONArray reviewJsonArray = reviewJsonObject.getJSONArray(MDB_RESULTS);

                if (reviewJsonArray.length() > 0 ) {

                    for (int j = 0; j < reviewJsonArray.length(); j++){
                        JSONObject reviewJson = reviewJsonArray.getJSONObject(j);

                        ContentValues reviewContent = reviewContentValuesFromJson(reviewJson);

                        reviewContent.put(ReviewEntry.COLUMN_MOVIE_ID, movieId);
                        contentValues.add(reviewContent);
                    }
                }
            } catch (Exception e) {
                Log.w(TAG, "Failed to read reviews for movie: " + e.toString());
            }
        }

        return contentValues;
    }

    /**
     * Returns an ArrayList of trailer ContentValues from an ArrayList of strings containing JSON
     * trailer data.
     *
     * @param context the current context
     * @param trailerStringArray ArrayList of json trailer data
     * @return ArrayList of ContentValues for each trailer
     */
    public static ArrayList<ContentValues> trailerContentArrayFromStringArray(Context context,
                                                                              ArrayList<String> trailerStringArray){

        ArrayList<ContentValues> contentValues = new ArrayList<>();

        for (int i = 0; i < trailerStringArray.size(); i++) {
            try {
                JSONObject movieJsonObject = validateJSONString(trailerStringArray.get(i));

                String movieId = movieJsonObject.getString(MDB_ID);

                JSONObject trailerJsonObject = movieJsonObject.getJSONObject(MDB_TRAILERS);
                JSONArray trailerJsonArray = trailerJsonObject.getJSONArray(MDB_RESULTS);

                if (trailerJsonArray.length() > 0 ) {

                    for (int j = 0; j < trailerJsonArray.length(); j++){
                        JSONObject reviewJson = trailerJsonArray.getJSONObject(j);

                        ContentValues trailerContent = trailerContentValuesFromJson(reviewJson);

                        trailerContent.put(TrailerEntry.COLUMN_MOVIE_ID, movieId);
                        contentValues.add(trailerContent);
                    }
                }
            } catch (Exception e) {
                Log.w(TAG, "Failed to read trailers for movie: " + e.toString());
            }
        }

        return contentValues;
    }

    /**
     * Converts the raw movie db string JSON data into an ArrayList of movie JSON strings
     * retrieved from the remote movie db.
     *
     * @param movieJsonData the JSON data
     * @return An ArrayList of Strings containing each movie's JSON data
     * @throws JSONException on Json parse error
     */
    public static ArrayList<String> arrayDataForMovieList(String movieJsonData)
            throws JSONException {

        JSONObject movieJson = validateJSONString(movieJsonData);

        JSONArray resultMovies = movieJson.getJSONArray(MDB_RESULTS);

        ArrayList<String> movieJsonTextArray = new ArrayList<>();

        for (int i = 0; i < resultMovies.length(); i++) {

            long id = resultMovies.getJSONObject(i).getLong(MDB_ID);

            URL mUrl = NetworkUtils.buildSpecificMovieUrl(id);

            try {
                String movieJsonText = NetworkUtils.getResponseFromHttpUrl(mUrl);
                movieJsonTextArray.add(movieJsonText);
            } catch (IOException e) {
                Log.w(TAG, "Failed to get movie data for: " + mUrl.toString());
            }

        }

        return movieJsonTextArray;
    }

    public static ArrayList<ContentValues> movieIdListFromJson(String movieJsonData)
            throws JSONException {

        JSONObject movieJson = validateJSONString(movieJsonData);

        JSONArray resultMovies = movieJson.getJSONArray(MDB_RESULTS);

        int resultCount = resultMovies.length();

        if (resultCount == 0){
            return null;
        }

        ArrayList<ContentValues> contentValueList = new ArrayList<>();

        for (int i = 0; i < resultCount; i++){
            long id = resultMovies.getJSONObject(i).getLong(MDB_ID);
            ContentValues contentValues = new ContentValues();
            contentValues.put(MovieContract.GLOBAL_COLUMN_MOVIE_ID, id);
            contentValueList.add(contentValues);
        }

        return contentValueList;
    }

    /**
     * Check that the server reports successfully handling a query and return the Json as a
     * {@link JSONObject}
     * @param JsonString a raw Json string to validate
     * @return A {@link JSONObject} representation of the string
     */
    private static JSONObject validateJSONString(String JsonString) {

        JSONObject movieJson = null;

        try {
            movieJson = new JSONObject(JsonString);

            if (movieJson.has(MDB_STATUS_CODE)) {
                int errorCode = movieJson.getInt(MDB_STATUS_CODE);

                switch (errorCode) {
                    case HttpURLConnection.HTTP_OK:
                        Log.v(TAG, "HTTP_OK");
                        break;
                    case HttpURLConnection.HTTP_NOT_FOUND:
                        Log.e(TAG, "HTTP_NOT_FOUND");
                        return null;
                    default:
                        Log.e(TAG, "No Status Code Returned");
                        // Probably a network issue
                        return null;
                }
            }

        } catch (JSONException e){
            Log.w(TAG, "Failed to process Json data: " + e.toString());
        }

        return movieJson;
    }

    /**
     * Converts a movie JSON response into the appropriate ContentValues
     * @param context the current context
     * @param movieJsonText the raw string of JSON data
     * @return the Content values for the movie
     * @throws JSONException on Json parse error
     */
    private static ContentValues getMovieContentValuesFromJson(Context context, String movieJsonText)
            throws JSONException {

        JSONObject movieJson = validateJSONString(movieJsonText);

        String title        = movieJson.getString(MDB_ORIGINAL_TITLE);
        String posterPath   = movieJson.getString(MDB_POSTER_PATH);
        String overview     = movieJson.getString(MDB_OVERVIEW);
        String id           = movieJson.getString(MDB_ID);
        String releaseDate  = movieJson.getString(MDB_RELEASE_DATE);
        double voteAverage  = movieJson.getDouble(MDB_VOTE_AVERAGE);
        long runtime        = movieJson.getLong(MDB_RUNTIME);

        ContentValues movieValues = new ContentValues();

        Date movieDate = MovieDateUtils.dateFromDBTextDate(context, releaseDate);

        movieValues.put(MovieEntry.COLUMN_TITLE, title);
        movieValues.put(MovieEntry.COLUMN_POSTER_PATH, posterPath);
        movieValues.put(MovieEntry.COLUMN_RELEASE_DATE,
                MovieDateUtils.millisecondTimeForDate(movieDate));
        movieValues.put(MovieEntry.COLUMN_AVERAGE_RATING, voteAverage);
        movieValues.put(MovieEntry.COLUMN_OVERVIEW, overview);
        movieValues.put(MovieEntry.COLUMN_MOVIE_ID, id);
        movieValues.put(MovieEntry.COLUMN_RUNTIME, runtime);

        return movieValues;
    }

    /**
     * Converts a raw String of review JSON data into its appropriate content values
     * @param reviewJson the raw String of JSON data.
     * @return the ContentValues of the review
     * @throws JSONException on Json parese error
     */
    private static ContentValues reviewContentValuesFromJson(JSONObject reviewJson)
            throws JSONException {

        ContentValues reviewValues = new ContentValues();

        String id = reviewJson.getString(MDB_REVIEW_ID);
        String reviewContent = reviewJson.getString(MDB_REVIEW_CONTENT);
        String reviewAuthor = reviewJson.getString(MDB_REVIEW_AUTHOR);
        String url = reviewJson.getString(MDB_REVIEW_URL);

        reviewValues.put(ReviewEntry.COLUMN_REVIEW_ID, id);
        reviewValues.put(ReviewEntry.COLUMN_REVIEW_CONTENT, reviewContent);
        reviewValues.put(ReviewEntry.COLUMN_REVIEW_AUTHOR, reviewAuthor);
        reviewValues.put(ReviewEntry.COLUMN_REVIEW_URL, url);

        return reviewValues;
    }

    /**
     * Converts a raw String of JSON data representing a trailer into the appropriate ContentValue
     * @param trailerJson the raw String of JSON data
     * @return the ContentValues for the trailer
     * @throws JSONException on json parese error
     */
    private static ContentValues trailerContentValuesFromJson(JSONObject trailerJson)
            throws JSONException {

        ContentValues trailerContent = new ContentValues();

        String id = trailerJson.getString(MDB_TRAILER_ID);
        String key = trailerJson.getString(MDB_TRAILER_KEY);
        String site = trailerJson.getString(MDB_TRAILER_SITE);
        String name = trailerJson.getString(MDB_TRAILER_NAME);

        trailerContent.put(TrailerEntry.COLUMN_TRAILER_ID, id);
        trailerContent.put(TrailerEntry.COLUMN_TRAILER_KEY, key);
        trailerContent.put(TrailerEntry.COLUMN_TRAILER_SITE, site);
        trailerContent.put(TrailerEntry.COLUMN_TRAILER_NAME, name);

        return trailerContent;
    }
}
