package com.delaroystudios.taskmakerapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.delaroystudios.taskmakerapp.data.TaskAdapter;
import com.delaroystudios.taskmakerapp.data.TaskContract;

import static com.delaroystudios.taskmakerapp.data.TaskContract.DATE_SORT;
import static com.delaroystudios.taskmakerapp.data.TaskContract.DEFAULT_SORT;

public class MainActivity extends AppCompatActivity implements
        TaskAdapter.OnItemClickListener,
        View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private TaskAdapter mAdapter;
    private static final int TASK_LOADER = 0;
    Uri currentTaskUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        mAdapter = new TaskAdapter(null, this);
        mAdapter.setOnItemClickListener(this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        getLoaderManager().initLoader(TASK_LOADER, null, this);

    }

    @Override
    public void onResume(){
        super.onResume();
        getLoaderManager().restartLoader(TASK_LOADER, null, MainActivity.this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }

    /* Click events in Floating Action Button */
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, NewTaskActivity.class);
        startActivity(intent);
    }

    /* Click events in RecyclerView items */
    @Override
    public void onItemClick(View v, int position) {
        //TODO: Handle list item click event

       /* Intent intent = new Intent(MainActivity.this, TaskDetailActivity.class);

        Uri currentTaskUri = ContentUris.withAppendedId(DatabaseContract.CONTENT_URI, mAdapter.getItemId(position));
        intent.setData(currentTaskUri);
        startActivity(intent);*/

    }



    /* Click events on RecyclerView item checkboxes */
    @Override
    public void onItemToggled(boolean active, int position) {
        Uri currentTaskUri = ContentUris.withAppendedId(TaskContract.CONTENT_URI, mAdapter.getItemId(position));

        ContentValues values = new ContentValues();
        values.put(TaskContract.TaskColumns.IS_COMPLETE, 1);

        int rowsAffected = getContentResolver().update(currentTaskUri, values, null, null);

        if (rowsAffected == 0) {

            Toast.makeText(this, getString(R.string.task_completed_failed),
                    Toast.LENGTH_SHORT).show();
        } else {

            Toast.makeText(this, getString(R.string.task_completed),
                    Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String orderByConstant = "";

        String orderBy = sharedPrefs.getString(
                getString(R.string.pref_sortBy_key),
                getString(R.string.pref_sortBy_default)
        );

        if (orderBy.equals(this.getString(R.string.pref_sortBy_default))) {
            orderByConstant = DEFAULT_SORT;
        } else if (orderBy.equals(this.getString(R.string.pref_sortBy_due))){
            orderByConstant = DATE_SORT;
        } else{

        }

        String[] projection = {
                TaskContract.TaskColumns._ID,
                TaskContract.TaskColumns.DESCRIPTION,
                TaskContract.TaskColumns.IS_COMPLETE,
                TaskContract.TaskColumns.IS_PRIORITY,
                TaskContract.TaskColumns.DUE_DATE
        };

        return new CursorLoader(this,
                TaskContract.CONTENT_URI,
                projection,
                null,
                null,
                orderByConstant);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        mAdapter.swapCursor(cursor);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mAdapter.swapCursor(null);

    }
}

