package com.delaroystudios.taskmakerapp.data;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by delaroy on 1/5/18.
 */

public class TaskProvider extends ContentProvider {
    private static final String TAG = TaskProvider.class.getSimpleName();

    private static final int CLEANUP_JOB_ID = 43;

    private static final int TASKS = 100;
    private static final int TASKS_WITH_ID = 101;

    private DbHelper mDbHelper;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        // content://com.google.developer.taskmaker/tasks
        sUriMatcher.addURI(TaskContract.CONTENT_AUTHORITY,
                TaskContract.TABLE_TASKS,
                TASKS);

        // content://com.google.developer.taskmaker/tasks/id
        sUriMatcher.addURI(TaskContract.CONTENT_AUTHORITY,
                TaskContract.TABLE_TASKS + "/#",
                TASKS_WITH_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new DbHelper(getContext());

        return true;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null; /* Not used */
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor = null;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case TASKS:
                cursor = database.query(TaskContract.TABLE_TASKS, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case TASKS_WITH_ID:

                selection = TaskContract.TaskColumns._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                cursor = database.query(TaskContract.TABLE_TASKS, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TASKS:
                return insertTask(uri, values);

            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertTask(Uri uri, ContentValues values) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(TaskContract.TABLE_TASKS, null, values);

        if (id == -1) {
            Log.e(TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }


    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TASKS_WITH_ID:
                selection = TaskContract.TaskColumns._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateTask(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateTask(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(TaskContract.TABLE_TASKS, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        switch (sUriMatcher.match(uri)) {
            case TASKS:
                //Rows aren't counted with null selection
                selection = (selection == null) ? "1" : selection;
                break;
            case TASKS_WITH_ID:
                long id = ContentUris.parseId(uri);
                selection = String.format("%s = ?", TaskContract.TaskColumns._ID);
                selectionArgs = new String[]{String.valueOf(id)};
                break;
            default:
                throw new IllegalArgumentException("Illegal delete URI");
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int count = db.delete(TaskContract.TABLE_TASKS, selection, selectionArgs);

        if (count > 0) {
            //Notify observers of the change
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return count;
    }

}

