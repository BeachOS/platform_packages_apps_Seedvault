package org.beachos.backup.storage.plugin

import com.google.protobuf.InvalidProtocolBufferException
import org.beachos.backup.storage.api.StoragePlugin
import org.beachos.backup.storage.api.StoredSnapshot
import org.beachos.backup.storage.backup.BackupSnapshot
import org.beachos.backup.storage.crypto.StreamCrypto
import org.beachos.backup.storage.restore.readVersion
import java.io.IOException
import java.security.GeneralSecurityException

@Suppress("BlockingMethodInNonBlockingContext")
internal class SnapshotRetriever(
    private val storagePlugin: StoragePlugin,
    private val streamCrypto: StreamCrypto = StreamCrypto,
) {

    @Throws(
        IOException::class,
        GeneralSecurityException::class,
        InvalidProtocolBufferException::class,
    )
    suspend fun getSnapshot(streamKey: ByteArray, storedSnapshot: StoredSnapshot): BackupSnapshot {
        return storagePlugin.getBackupSnapshotInputStream(storedSnapshot).use { inputStream ->
            val version = inputStream.readVersion()
            val timestamp = storedSnapshot.timestamp
            val ad = streamCrypto.getAssociatedDataForSnapshot(timestamp, version.toByte())
            streamCrypto.newDecryptingStream(streamKey, inputStream, ad).use { decryptedStream ->
                BackupSnapshot.parseFrom(decryptedStream)
            }
        }
    }

}
