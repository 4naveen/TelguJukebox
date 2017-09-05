package com.lorvent.project.telgujukebox.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;


public final class FavouriteContract {


    public static final String CONTENT_AUTHORITY = "com.lorvent.project.telgujukebox.data.FavoriteProvider";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    public static final class FavoriteEntry implements BaseColumns {
        public static final String TABLE_NAME = "favorite";

        // Table columns
        public static final String COLUMN_VIDEO_ID = "video_id";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_LIKE_COUNT = "like";
        public static final String COLUMN_VIEW_COUNT = "view";
        public static final String COLUMN_MOVIE_NAME = "title";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

        public static Uri buildFavouriteUri(long movie_id) {
            return ContentUris.withAppendedId(CONTENT_URI,movie_id);

        }

    }

}
