package com.haxxedtech.lwp.matrix;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class HTLiveMatrixActivity extends PreferenceActivity {
    /** Called when the activity is first created. */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}