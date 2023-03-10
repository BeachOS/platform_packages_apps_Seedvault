package com.stevesoltys.seedvault.ui.files

import android.app.Application
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.beachos.backup.storage.api.StorageBackup
import org.beachos.backup.storage.ui.backup.BackupContentViewModel

class FileSelectionViewModel(
    app: Application,
    override val storageBackup: StorageBackup,
) : BackupContentViewModel(app) {

    init {
        viewModelScope.launch { loadContent() }
    }

}
