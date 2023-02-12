package com.tsarionovtimofey.utils.data.files

import android.content.Context
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.CreateDocument
import androidx.activity.result.contract.ActivityResultContracts.OpenDocument
import com.tsarionovtimofey.utils.domain.ActivityNotStartedException
import com.tsarionovtimofey.utils.domain.AlreadyInProgressException
import com.tsarionovtimofey.utils.domain.CantAccessFileException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import com.tsarionovtimofey.utils.data.ActivityRequired

class AndroidFilesRepository(
    private val appContext: Context,
    private val ioDispatcher: CoroutineDispatcher,
    private val mainDispatcher: CoroutineDispatcher,
) : FilesRepository, ActivityRequired() {

    private var openLauncher: ActivityResultLauncher<Array<String>>? = null
    private var saveLauncher: ActivityResultLauncher<String>? = null
    private var pickVisualMediaLauncher: ActivityResultLauncher<PickVisualMediaRequest>? = null

    private var completableDeferred: CompletableDeferred<AndroidFile>? = null
    private var isStarted = false

    override suspend fun openFile(vararg mimeTypes: String): ReadableFile = withContext(mainDispatcher) {
        assertLauncherState()

        openLauncher?.launch(arrayOf(*mimeTypes))

        CompletableDeferred<AndroidFile>().let {
            completableDeferred = it
            it.await()
        }
    }

    override suspend fun saveFile(suggestedName: String, mimeType: String): WritableFile =
        withContext(mainDispatcher) {
            assertLauncherState()

            saveLauncher?.launch(suggestedName)

            CompletableDeferred<AndroidFile>().let {
                completableDeferred = it
                it.await()
            }
        }

    override suspend fun pickVisualMedia(request: PickVisualMediaRequest): ReadableFile =
        withContext(mainDispatcher) {
            assertLauncherState()

            pickVisualMediaLauncher?.launch(request)

            CompletableDeferred<AndroidFile>().let {
                completableDeferred = it
                it.await()
            }
        }

    // ---

    override fun onActivityCreated(activity: ComponentActivity) {
        openLauncher = activity.registerForActivityResult(OpenDocument()) { uri ->
            handleUri(uri)
        }
        saveLauncher = activity.registerForActivityResult(CreateDocument("text/plain")) { uri ->
            handleUri(uri)
        }
        pickVisualMediaLauncher = activity.registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            handleUri(uri)
        }
    }

    override fun onActivityStarted() {
        isStarted = true
    }

    override fun onActivityStopped() {
        isStarted = false
    }

    override fun onActivityDestroyed(isFinishing: Boolean) {
        openLauncher = null
        saveLauncher = null
        if (isFinishing) {
            completableDeferred?.cancel()
            completableDeferred = null
        }
    }

    private fun handleUri(uri: Uri?) {
        try {
            if (uri == null) {
                completableDeferred?.cancel()
            } else {
                completableDeferred?.complete(AndroidFile(uri, appContext, ioDispatcher))
            }
        } catch (e: Exception) {
            completableDeferred?.completeExceptionally(CantAccessFileException(e))
        }
        completableDeferred = null
    }

    private fun assertLauncherState() {
        if (!isStarted) throw ActivityNotStartedException()
        if (completableDeferred != null) throw AlreadyInProgressException()
    }
}