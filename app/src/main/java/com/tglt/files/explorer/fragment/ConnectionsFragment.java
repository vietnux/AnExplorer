package com.tglt.files.explorer.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.design.internal.NavigationMenu;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.PopupMenu;

import com.tglt.files.explorer.BaseActivity;
import com.tglt.files.explorer.DocumentsActivity;
import com.tglt.files.explorer.DocumentsApplication;
import com.tglt.files.explorer.R;
import com.tglt.files.explorer.adapter.ConnectionsAdapter;
import com.tglt.files.explorer.cloud.CloudConnection;
import com.tglt.files.explorer.common.DialogBuilder;
import com.tglt.files.explorer.common.RecyclerFragment;
import com.tglt.files.explorer.directory.DividerItemDecoration;
//import com.tglt.files.explorer.misc.AnalyticsManager;
import com.tglt.files.explorer.misc.ProviderExecutor;
import com.tglt.files.explorer.misc.RootsCache;
import com.tglt.files.explorer.misc.Utils;
import com.tglt.files.explorer.model.RootInfo;
import com.tglt.files.explorer.network.NetworkConnection;
import com.tglt.files.explorer.provider.CloudStorageProvider;
import com.tglt.files.explorer.provider.ExplorerProvider;
import com.tglt.files.explorer.provider.NetworkStorageProvider;
import com.tglt.files.explorer.setting.SettingsActivity;
import com.tglt.files.explorer.ui.FloatingActionsMenu;
import com.tglt.files.explorer.ui.fabs.FabSpeedDial;

import static android.widget.LinearLayout.VERTICAL;
import static com.tglt.files.explorer.BuildConfig.DROPBOX_CLIENT_ID;
import static com.tglt.files.explorer.BuildConfig.DROPBOX_CLIENT_KEY;
import static com.tglt.files.explorer.BuildConfig.GOOGLE_DRIVE_CLIENT_ID;
import static com.tglt.files.explorer.DocumentsApplication.isSpecialDevice;
import static com.tglt.files.explorer.DocumentsApplication.isWatch;
import static com.tglt.files.explorer.model.DocumentInfo.getCursorInt;
import static com.tglt.files.explorer.network.NetworkConnection.SERVER;
import static com.tglt.files.explorer.provider.CloudStorageProvider.TYPE_BOX;
import static com.tglt.files.explorer.provider.CloudStorageProvider.TYPE_CLOUD;
import static com.tglt.files.explorer.provider.CloudStorageProvider.TYPE_DROPBOX;
import static com.tglt.files.explorer.provider.CloudStorageProvider.TYPE_GDRIVE;
import static com.tglt.files.explorer.provider.CloudStorageProvider.TYPE_ONEDRIVE;

public class ConnectionsFragment extends RecyclerFragment
        implements View.OnClickListener, FabSpeedDial.MenuListener, ConnectionsAdapter.OnItemClickListener {

    public static final String TAG = "ConnectionsFragment";

    private ConnectionsAdapter mAdapter;
    private LoaderManager.LoaderCallbacks<Cursor> mCallbacks;

    private final int mLoaderId = 42;
    private FloatingActionsMenu mActionMenu;
    private RootInfo mConnectionsRoot;
    private int mLastShowAccentColor;

    public static void show(FragmentManager fm) {
        final ConnectionsFragment fragment = new ConnectionsFragment();
        final FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.container_directory, fragment, TAG);
        ft.commitAllowingStateLoss();
    }

    public static ConnectionsFragment get(FragmentManager fm) {
        return (ConnectionsFragment) fm.findFragmentByTag(TAG);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(isSpecialDevice());
        mConnectionsRoot = DocumentsApplication.getRootsCache(getActivity()).getConnectionsRoot();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return  inflater.inflate(R.layout.fragment_connections,container,false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        final Resources res = getActivity().getResources();

        mActionMenu = (FloatingActionsMenu) view.findViewById(R.id.fabs);
        mActionMenu.setMenuListener(this);
        mActionMenu.setVisibility(!isSpecialDevice() ? View.VISIBLE : View.GONE);
        mActionMenu.attachToListView(getListView());

        // Indent our list divider to align with text
        final boolean insetLeft = res.getBoolean(R.bool.list_divider_inset_left);
        final int insetSize = res.getDimensionPixelSize(R.dimen.list_divider_inset);
        DividerItemDecoration decoration = new DividerItemDecoration(getActivity(), VERTICAL);
        if (insetLeft) {
            decoration.setInset(insetSize, 0);
        } else {
            decoration.setInset(0, insetSize);
        }
        if(!isWatch()) {
            getListView().addItemDecoration(decoration);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        int accentColor = SettingsActivity.getAccentColor();
        if ((mLastShowAccentColor != 0 && mLastShowAccentColor == accentColor))
            return;
        int defaultColor = SettingsActivity.getPrimaryColor(getActivity());
        mActionMenu.setBackgroundTintList(SettingsActivity.getAccentColor());
        mActionMenu.setSecondaryBackgroundTintList(Utils.getActionButtonColor(defaultColor));
    }


    @Override
    public void onItemClick(ConnectionsAdapter.ViewHolder item, View view, int position) {
        final Cursor cursor = mAdapter.getItem(position);
        if (cursor != null) {
            openConnectionRoot(cursor);
        }
    }

    @Override
    public void onItemLongClick(ConnectionsAdapter.ViewHolder item, View view, int position) {
        if(isSpecialDevice()) {
            showPopupMenu(view, position);
        }
    }

    @Override
    public void onItemViewClick(ConnectionsAdapter.ViewHolder item, View view, int position) {
        showPopupMenu(view, position);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final Context context = getActivity();

        mAdapter = new ConnectionsAdapter(context, null);
        mAdapter.setOnItemClickListener(this);
        mCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {

            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                Uri contentsUri = ExplorerProvider.buildConnection();

                String selection = null;
                String[] selectionArgs = null;
                if(!Utils.hasWiFi(getActivity())){
                    selection = ExplorerProvider.ConnectionColumns.TYPE + "!=? " ;
                    selectionArgs = new String[]{SERVER};
                }

                return new CursorLoader(context, contentsUri, null, selection, selectionArgs, null);
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor result) {
                if (!isAdded())
                    return;

                mAdapter.swapCursor(result);
                if (isResumed()) {
                    setListShown(true);
                } else {
                    setListShownNoAnimation(true);
                }
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                mAdapter.swapCursor(null);
            }
        };
        setListAdapter(mAdapter);
        setListShown(false);
        // Kick off loader at least once
        getLoaderManager().restartLoader(mLoaderId, null, mCallbacks);

    }

    public void reload(){
        getLoaderManager().restartLoader(mLoaderId, null, mCallbacks);
        RootsCache.updateRoots(getActivity(), NetworkStorageProvider.AUTHORITY);
        RootsCache.updateRoots(getActivity(), CloudStorageProvider.AUTHORITY);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.connections_options, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        menuItemAction(item);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()){
            case R.id.fab:
                addConnection();
                break;
        }
    }

    private void showPopupMenu(View view, final int position) {
        PopupMenu popup = new PopupMenu(getActivity(), view);

        popup.getMenuInflater().inflate(R.menu.popup_connections, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                return onPopupMenuItemClick(menuItem, position);
            }
        });
        popup.show();
    }

    public boolean onPopupMenuItemClick(MenuItem item, int position) {
        final Cursor cursor = mAdapter.getItem(position);
        int connection_id = getCursorInt(cursor, BaseColumns._ID);
        NetworkConnection networkConnection = NetworkConnection.fromConnectionsCursor(cursor);
        final int id = item.getItemId();
        switch (id) {
            case R.id.menu_edit:
                if(!networkConnection.type.startsWith(TYPE_CLOUD)) {
                    editConnection(connection_id);
                } else {
                    Utils.showSnackBar(getActivity(), "Cloud storage connection can't be edited");
                }
                return true;
            case R.id.menu_delete:
                if(!networkConnection.type.equals(SERVER)) {
                    deleteConnection(connection_id);
                } else {
                    Utils.showSnackBar(getActivity(), "Default server connection can't be deleted");
                }
                return true;
            default:
                return false;
        }
    }

    private void addConnection() {
        CreateConnectionFragment.show(((DocumentsActivity)getActivity()).getSupportFragmentManager());
//        AnalyticsManager.logEvent("connection_add");
    }

    private void editConnection(int connection_id) {
        CreateConnectionFragment.show(((DocumentsActivity)getActivity()).getSupportFragmentManager(), connection_id);
//        AnalyticsManager.logEvent("connection_edit");
    }

    private void deleteConnection(final int connection_id) {
        DialogBuilder builder = new DialogBuilder(getActivity());
        builder.setMessage("Delete connection?")
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int did) {
                        boolean success = NetworkConnection.deleteConnection(getActivity(), connection_id);
                        if(success){
                            reload();
                        }
                    }
                    }).setNegativeButton(android.R.string.cancel,  null);
        builder.showDialog();
//        AnalyticsManager.logEvent("connection_delete");
    }

    public void openConnectionRoot(Cursor cursor) {
        NetworkConnection connection = NetworkConnection.fromConnectionsCursor(cursor);
        DocumentsActivity activity = ((DocumentsActivity)getActivity());
        if (connection.type.startsWith(TYPE_CLOUD)){
            activity.onRootPicked(activity.getRoots().getRootInfo(CloudConnection.fromCursor(getActivity(), cursor)), mConnectionsRoot);
        } else {
            activity.onRootPicked(activity.getRoots().getRootInfo(connection), mConnectionsRoot);
        }
    }

    public void openConnectionRoot(NetworkConnection connection) {
        DocumentsActivity activity = ((DocumentsActivity)getActivity());
        activity.onRootPicked(activity.getRoots().getRootInfo(connection), mConnectionsRoot);
    }

    public void openConnectionRoot(CloudConnection connection) {
        DocumentsActivity activity = ((DocumentsActivity)getActivity());
        activity.onRootPicked(activity.getRoots().getRootInfo(connection), mConnectionsRoot);
    }

    @Override
    public boolean onPrepareMenu(NavigationMenu navigationMenu) {
        return true;
    }

    public boolean onMenuItemSelected(MenuItem menuItem) {
        menuItemAction(menuItem);
        mActionMenu.closeMenu();
        return false;
    }

    @Override
    public void onMenuClosed() {

    }

    public void addCloudConnection(String cloudType){
        final BaseActivity activity = (BaseActivity) getActivity();
        CloudConnection cloudStorage = CloudConnection.createCloudConnections(getActivity(), cloudType);
        new CloudConnection.CreateConnectionTask(activity, cloudStorage).executeOnExecutor(
                ProviderExecutor.forAuthority(CloudStorageProvider.AUTHORITY+cloudType));
//        AnalyticsManager.logEvent("add_cloud");
        Log.e("Cloud ss", cloudType +"  "+DROPBOX_CLIENT_KEY +" "+ cloudStorage );
//        Log.e("Cloud ss", cloudType +"  " + cloudStorage );
    }

    public void menuItemAction(MenuItem menuItem) {
//        final BaseActivity activity = (BaseActivity) getActivity();
//        if(!DocumentsApplication.isPurchased()){
//            DocumentsApplication.openPurchaseActivity(activity);
//            return;
//        }
        switch (menuItem.getItemId()){
            case R.id.cloud_gridve:
                addCloudConnection(TYPE_GDRIVE);
                break;

            case R.id.cloud_dropbox:
                addCloudConnection(TYPE_DROPBOX);
                break;

            case R.id.cloud_onedrive:
                addCloudConnection(TYPE_ONEDRIVE);
                break;

            case R.id.cloud_box:
                addCloudConnection(TYPE_BOX);
                break;

            case R.id.network_ftp:
                addConnection();
//                AnalyticsManager.logEvent("add_ftp");
                break;
        }
    }
}