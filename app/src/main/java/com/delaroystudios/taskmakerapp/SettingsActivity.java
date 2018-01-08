package com.delaroystudios.taskmakerapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by delaroy on 1/8/18.
 */

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
