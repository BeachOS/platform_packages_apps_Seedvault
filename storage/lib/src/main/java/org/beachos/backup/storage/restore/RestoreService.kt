package org.beachos.backup.storage.restore

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.beachos.backup.storage.api.RestoreObserver
import org.beachos.backup.storage.api.StorageBackup
import org.beachos.backup.storage.api.StoredSnapshot
import org.beachos.backup.storage.restore.RestoreService.Companion.EXTRA_TIMESTAMP_START
import org.beachos.backup.storage.restore.RestoreService.Companion.EXTRA_USER_ID
import org.beachos.backup.storage.ui.NOTIFICATION_ID_RESTORE
import org.beachos.backup.storage.ui.Notifications

/**
 * Start to trigger restore as a foreground service. Ensure that you provide the snapshot
 * to be restored with [Intent.putExtra]:
 *   * the user ID of the snapshot as a [String] in [EXTRA_USER_ID]
 *   * the snapshot's timestamp as a [Long] in [EXTRA_TIMESTAMP_START].
 *     See [BackupSnapshot.getTimeStart].
 */
public abstract class RestoreService : Service() {

    public companion object {
        private const val TAG = "RestoreService"
        public const val EXTRA_USER_ID: String = "userId"
        public const val EXTRA_TIMESTAMP_START: String = "timestamp"
    }

    private val n by lazy { Notifications(applicationContext) }
    protected abstract val storageBackup: StorageBackup
    protected abstract val restoreObserver: RestoreObserver?

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand $intent $flags $startId")
        val userId = intent?.getStringExtra(EXTRA_USER_ID) ?: error("No user ID in intent: $intent")
        val timestamp = intent.getLongExtra(EXTRA_TIMESTAMP_START, -1)
        if (timestamp < 0) error("No timestamp in intent: $intent")
        val storedSnapshot = StoredSnapshot(userId, timestamp)

        startForeground(NOTIFICATION_ID_RESTORE, n.getRestoreNotification())
        GlobalScope.launch {
            // TODO offer a way to try again if failed, or do an automatic retry here
            storageBackup.restoreBackupSnapshot(storedSnapshot, null, restoreObserver)
            stopSelf(startId)
        }
        return START_STICKY_COMPATIBILITY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        super.onDestroy()
    }

}
