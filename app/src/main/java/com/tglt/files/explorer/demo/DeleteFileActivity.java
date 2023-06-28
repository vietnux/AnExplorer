/*
 * Copyright 2013 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tglt.files.explorer.demo;

import androidx.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.tglt.files.explorer.R;
/**
 * An activity to illustrate how to delete a file.
 */
public class DeleteFileActivity extends BaseDemoActivity {
    private static final String TAG = "DeleteFileActivity";

    @Override
    protected void onDriveClientReady() {
        pickTextFile()
                .addOnSuccessListener(this,
                        driveId -> deleteFile(driveId.asDriveFile()))
                .addOnFailureListener(this, e -> {
                    Log.e(TAG, "No file selected", e);
                    showMessage(getString(R.string.file_not_selected));
                    finish();
                });
    }
    private void deleteFile(DriveFile file) {
        // [START drive_android_delete_file]
        getDriveResourceClient()
                .delete(file)
                .addOnSuccessListener(this,
                        aVoid -> {
                            showMessage(getString(R.string.file_deleted));
                            finish();
                        })
                .addOnFailureListener(this, e -> {
                    Log.e(TAG, "Unable to delete file", e);
                    showMessage(getString(R.string.delete_failed));
                    finish();
                });
        // [END drive_android_delete_file]
    }
}
