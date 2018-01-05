package com.delaroystudios.taskmakerapp.data;

import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by delaroy on 1/4/18.
 */

public class TaskContract {
    //Database schema information
    public static final String TABLE_TASKS = "tasks";

    //Unique authority string for the content provider
    public static final String CONTENT_AUTHORITY = "com.delaroystudios.taskmakerapp";

    /* Sort order constants */
    //Priority first, Completed last, the rest by date
    public static final String DEFAULT_SORT = String.format("%s ASC, %s DESC, %s ASC",
            TaskColumns.IS_COMPLETE, TaskColumns.IS_PRIORITY, TaskColumns.DUE_DATE);

    //Completed last, then by date, followed by priority
    public static final String DATE_SORT = String.format("%s ASC, %s ASC, %s DESC",
            TaskColumns.IS_COMPLETE, TaskColumns.DUE_DATE, TaskColumns.IS_PRIORITY);

    //Base content Uri for accessing the provider
    public static final Uri CONTENT_URI = new Uri.Builder().scheme("content")
            .authority(CONTENT_AUTHORITY)
            .appendPath(TABLE_TASKS)
            .build();

    /* Helpers to retrieve column values */
    public static String getColumnString(Cursor cursor, String columnName) {
        return cursor.getString( cursor.getColumnIndex(columnName) );
    }

    public static int getColumnInt(Cursor cursor, String columnName) {
        return cursor.getInt( cursor.getColumnIndex(columnName) );
    }

    public static long getColumnLong(Cursor cursor, String columnName) {
        return cursor.getLong( cursor.getColumnIndex(columnName) );
    }



    public static final class TaskColumns implements BaseColumns {

        public static final String DESCRIPTION = "description";

        public static final String IS_COMPLETE = "is_complete";

        public static final String IS_PRIORITY = "is_priority";

        public static final String DUE_DATE = "due_date";
    }


}

