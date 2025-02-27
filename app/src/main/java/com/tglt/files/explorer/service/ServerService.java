package com.tglt.files.explorer.service;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import com.tglt.files.explorer.misc.ConnectionUtils;

import static com.tglt.files.explorer.misc.ConnectionUtils.ACTION_START_FTPSERVER;
import static com.tglt.files.explorer.misc.ConnectionUtils.ACTION_STOP_FTPSERVER;

@TargetApi(Build.VERSION_CODES.N)
public class ServerService extends TileService {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        if (ConnectionUtils.isServerRunning(getApplicationContext())) {
            updateTileState(Tile.STATE_ACTIVE);
        } else {
            updateTileState(Tile.STATE_INACTIVE);
        }
    }

    private void updateTileState(int state) {
        Tile tile = getQsTile();
        if (tile != null) {
            tile.setState(state);
            Icon icon = tile.getIcon();
            switch (state) {
                case Tile.STATE_ACTIVE:
                    icon.setTint(Color.WHITE);
                    break;
                case Tile.STATE_INACTIVE:
                case Tile.STATE_UNAVAILABLE:
                default:
                    icon.setTint(Color.GRAY);
                    break;
            }
            tile.updateTile();
        }
    }

    @Override
    public void onClick() {
        super.onClick();

        Tile tile = getQsTile();
        switch (tile.getState()) {
            case Tile.STATE_INACTIVE:
                startServer();
                updateTileState(Tile.STATE_ACTIVE);
                break;
            case Tile.STATE_ACTIVE:
                stopServer();
            default:
                updateTileState(Tile.STATE_INACTIVE);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void startServer() {
        Intent intent = new Intent(ACTION_START_FTPSERVER);
        Bundle args = new Bundle();
        intent.putExtras(args);
        sendBroadcast(intent);
    }

    private void stopServer() {
        Intent intent = new Intent(ACTION_STOP_FTPSERVER);
        Bundle args = new Bundle();
        intent.putExtras(args);
        sendBroadcast(intent);
    }
}
