package com.tsarionovtimofey.utils.data.files

import android.content.Context
import android.net.Uri
import com.tsarionovtimofey.utils.domain.CantAccessFileException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class AndroidFile(
    private val uri: Uri,
    private val context: Context,
    private val ioDispatcher: CoroutineDispatcher,
) : ReadableFile, WritableFile {

    override suspend fun read(): ByteArray = withContext(ioDispatcher) {
        try {
            context.contentResolver.openInputStream(uri)!!.use {
                it.readBytes()
            }
        } catch (e: Exception) {
            throw CantAccessFileException(e)
        }
    }

    override suspend fun write(data: ByteArray) = withContext(ioDispatcher) {
        try {
            context.contentResolver.openOutputStream(uri)!!.use {
                it.write(data)
            }
        } catch (e: Exception) {
            throw CantAccessFileException(e)
        }
    }
}
