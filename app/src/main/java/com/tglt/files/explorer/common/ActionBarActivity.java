package com.tglt.files.explorer.common;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

//import com.tglt.files.explorer.misc.AnalyticsManager;
import com.tglt.files.explorer.misc.Utils;

/**
 * Created by HaKr on 18-Oct-14.
 */
public abstract class ActionBarActivity extends BaseCommonActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Utils.changeThemeStyle(getDelegate());
        super.onCreate(savedInstanceState);
    }

    @Override
    public ActionBar getSupportActionBar() {
        return super.getSupportActionBar();
    }

    @Override
    public void recreate() {
        Utils.changeThemeStyle(getDelegate());
        super.recreate();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        AnalyticsManager.setCurrentScreen(this, getTag());
    }

    public abstract String getTag();
}
