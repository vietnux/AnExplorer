package com.tglt.files.explorer.misc;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.tglt.files.explorer.R;
import com.tglt.files.explorer.common.DialogFragment;
import com.tglt.files.explorer.model.DocumentInfo;
import com.tglt.files.explorer.model.RootInfo;
import com.tglt.files.explorer.setting.SettingsActivity;

public class UtilsFlavour {

    public static void showInfo(Context context, int messageId){

    }

    public static Menu getActionDrawerMenu(Activity activity){
        return null;
    }

    public static View getActionDrawer(Activity activity){
        return null;
    }

    public static void inflateActionMenu(Activity activity,
                                         MenuItem.OnMenuItemClickListener listener,
                                         boolean contextual, RootInfo root, DocumentInfo cwd) {
    }

    public static void showMessage(Activity activity, String message,
                                   int duration, String action, View.OnClickListener listener){
        Snackbar snackbar = Snackbar.make(activity.findViewById(R.id.content_view), message, duration);
        if (null != listener) {
            snackbar.setAction(action, listener)
                    .setActionTextColor(SettingsActivity.getAccentColor());
        }
        snackbar.show();
    }
}