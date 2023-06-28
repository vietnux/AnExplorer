package com.tglt.files.explorer.common;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;

public abstract class SettingsCommonActivity extends AppCompatPreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
    }
}
