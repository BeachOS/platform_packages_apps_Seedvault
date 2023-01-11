package com.stevesoltys.seedvault.storage

import android.content.Intent
import com.stevesoltys.seedvault.transport.requestBackup
import org.beachos.backup.storage.api.BackupObserver
import org.beachos.backup.storage.api.RestoreObserver
import org.beachos.backup.storage.api.StorageBackup
import org.beachos.backup.storage.backup.BackupJobService
import org.beachos.backup.storage.backup.BackupService
import org.beachos.backup.storage.backup.NotificationBackupObserver
import org.beachos.backup.storage.restore.NotificationRestoreObserver
import org.beachos.backup.storage.restore.RestoreService
import org.koin.android.ext.android.inject

/*
test and debug with

  adb shell dumpsys jobscheduler |
  grep -A 23 -B 4 "Service: com.stevesoltys.seedvault/.storage.StorageBackupJobService"

force running with:

  adb shell cmd jobscheduler run -f com.stevesoltys.seedvault 0

 */
internal class StorageBackupJobService : BackupJobService(StorageBackupService::class.java)

internal class StorageBackupService : BackupService() {

    companion object {
        internal const val EXTRA_START_APP_BACKUP = "startAppBackup"
    }

    override val storageBackup: StorageBackup by inject()

    // use lazy delegate because context isn't available during construction time
    override val backupObserver: BackupObserver by lazy {
        NotificationBackupObserver(applicationContext)
    }

    override fun onBackupFinished(intent: Intent, success: Boolean) {
        if (intent.getBooleanExtra(EXTRA_START_APP_BACKUP, false)) {
            requestBackup(applicationContext)
        }
    }
}

internal class StorageRestoreService : RestoreService() {
    override val storageBackup: StorageBackup by inject()

    // use lazy delegate because context isn't available during construction time
    override val restoreObserver: RestoreObserver by lazy {
        NotificationRestoreObserver(applicationContext)
    }
}
