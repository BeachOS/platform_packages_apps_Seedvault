package org.beachos.backup.storage.ui.backup

import android.content.Context
import android.net.Uri
import org.beachos.backup.storage.api.BackupContentType
import org.beachos.backup.storage.api.MediaType

public data class BackupContentItem(
    val uri: Uri,
    val contentType: BackupContentType,
    val enabled: Boolean,
) {
    public fun getName(context: Context): String = when (contentType) {
        is BackupContentType.Custom -> BackupContentType.Custom.getName(uri)
        is MediaType -> context.getString(contentType.nameRes)
    }
}
