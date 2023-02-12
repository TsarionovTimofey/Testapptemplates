package com.tsarionovtimofey.utils.data.accounts

import com.tsarionovtimofey.utils.domain.AuthException
import com.tsarionovtimofey.utils.domain.CalledNotFromUiException
import com.tsarionovtimofey.utils.domain.InternalException
import com.tsarionovtimofey.utils.domain.LoginFailedException
import com.tsarionovtimofey.utils.domain.accounts.Account

interface AccountsSource {

    /**
     * Try to sign-in.
     * @throws CalledNotFromUiException
     * @throws LoginFailedException
     * @throws InternalException
     */
    suspend fun oauthSignIn(): Account

    /**
     * Get the current signed-in account.
     * @throws AuthException if the user is not logged-in
     * @throws InternalException
     */
    suspend fun getAccount(): Account

    /**
     * Sign-out from the app.
     * @throws InternalError
     */
    suspend fun signOut()

}