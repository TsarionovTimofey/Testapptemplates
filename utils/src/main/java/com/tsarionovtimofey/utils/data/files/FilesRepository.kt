package com.tsarionovtimofey.utils.data.files

import androidx.activity.result.PickVisualMediaRequest
import com.tsarionovtimofey.utils.domain.ActivityNotStartedException
import com.tsarionovtimofey.utils.domain.AlreadyInProgressException
import com.tsarionovtimofey.utils.domain.CantAccessFileException


interface FilesRepository {

    /**
     * @throws CantAccessFileException
     * @throws ActivityNotStartedException
     * @throws AlreadyInProgressException
     */
    suspend fun openFile(vararg mimeTypes: String): ReadableFile

    /**
     * @throws CantAccessFileException
     * @throws ActivityNotStartedException
     * @throws AlreadyInProgressException
     */
    suspend fun saveFile(suggestedName: String, mimeType: String): WritableFile

    suspend fun pickVisualMedia(request: PickVisualMediaRequest): ReadableFile
}