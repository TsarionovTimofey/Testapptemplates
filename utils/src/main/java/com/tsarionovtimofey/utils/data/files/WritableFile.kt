package com.tsarionovtimofey.utils.data.files

interface WritableFile {
    suspend fun write(data: ByteArray)
}