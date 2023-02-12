package com.tsarionovtimofey.utils.data.accounts.google

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import kotlinx.coroutines.CompletableDeferred
import com.tsarionovtimofey.utils.data.ActivityRequired
import com.tsarionovtimofey.utils.data.accounts.AccountsSource
import com.tsarionovtimofey.utils.domain.AlreadyInProgressException
import com.tsarionovtimofey.utils.domain.CalledNotFromUiException
import com.tsarionovtimofey.utils.domain.InternalException
import com.tsarionovtimofey.utils.domain.accounts.Account
import com.tsarionovtimofey.utils.domain.Result

class GoogleAccountsSource constructor(
    private val applicationContext: Context
) : AccountsSource, ActivityRequired() {

    private var isActivityStarted = false
    private var signInLauncher: ActivityResultLauncher<Unit>? = null
    private var completableDeferred: CompletableDeferred<Account>? = null

    override fun onActivityCreated(activity: ComponentActivity) {
        signInLauncher = activity.registerForActivityResult(GoogleSignInContract()) {
            if (it is Result.Success) {
                completableDeferred?.complete(it.value)
            } else if (it is Result.Error) {
                completableDeferred?.completeExceptionally(it.exception)
            }
            completableDeferred = null
        }
    }

    // ----- ActivityRequired impl

    override fun onActivityStarted() {
        isActivityStarted = true
    }

    override fun onActivityStopped() {
        isActivityStarted = false
    }

    override fun onActivityDestroyed(isFinishing: Boolean) {
        this.signInLauncher = null
    }

    // ----- AccountsSource impl

    override suspend fun oauthSignIn(): Account {
        if (!isActivityStarted) throw CalledNotFromUiException()
        val signInLauncher = this.signInLauncher ?: throw CalledNotFromUiException()
        if (completableDeferred != null) throw AlreadyInProgressException()

        signInLauncher.launch(Unit)

        return CompletableDeferred<Account>().let {
            completableDeferred = it
            it.await()
        }
    }

    override suspend fun getAccount(): Account {
        return getGoogleLastSignedInAccount(applicationContext)
    }

    override suspend fun signOut() {
        try {
            getGoogleSignInClient(applicationContext).signOut().suspend()
        } catch (e: Throwable) {
            throw InternalException(e)
        }
    }

    // --- equals/hash-code for correct working of Activity Result API

    override fun equals(other: Any?): Boolean {
        return other?.javaClass?.name?.equals(javaClass.name) ?: false
    }

    override fun hashCode(): Int {
        return javaClass.name.hashCode()
    }
}