package com.example.android.popularmovies.sync;

import android.os.Parcel;
import android.os.Parcelable;

class MovieSyncArgs implements Parcelable {

    static String MOVIE_SYNC_EXTRA_ARGS = "movie_sync_args_extra";

    static final String EXTRA_TARGET_URL = "movie_target_url";
    static final String EXTRA_TARGET_TABLE = "movie_target_table";

    private String tagName;
    private String targetDatabaseUri;
    private String targetRemoteURL;

    MovieSyncArgs(String tagName, String targetDatabaseUri, String targetRemoteURL) {
        this.tagName = tagName;
        this.targetDatabaseUri = targetDatabaseUri;
        this.targetRemoteURL = targetRemoteURL;
    }

    private MovieSyncArgs(Parcel in) {
        tagName = in.readString();
        targetDatabaseUri = in.readString();
        targetRemoteURL = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(tagName);
        dest.writeString(targetDatabaseUri);
        dest.writeString(targetRemoteURL);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MovieSyncArgs> CREATOR = new Creator<MovieSyncArgs>() {
        @Override
        public MovieSyncArgs createFromParcel(Parcel in) {
            return new MovieSyncArgs(in);
        }

        @Override
        public MovieSyncArgs[] newArray(int size) {
            return new MovieSyncArgs[size];
        }
    };

    String getTagName() {
        return tagName;
    }

    String getTargetDatabaseUri() {
        return targetDatabaseUri;
    }

    String getTargetRemoteURL() {
        return targetRemoteURL;
    }
}
