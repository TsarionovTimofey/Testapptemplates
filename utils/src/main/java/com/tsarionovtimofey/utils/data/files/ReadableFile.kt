package com.tsarionovtimofey.utils.data.files

interface ReadableFile {
    suspend fun read(): ByteArray
}