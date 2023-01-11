package org.beachos.backup.contacts;

import android.app.backup.FullBackupDataOutput;

import java.io.File;

interface FullBackupFileHandler {

    void fullBackupFile(File file, FullBackupDataOutput output);

}
