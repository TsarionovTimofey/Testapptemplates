package com.tsarionovtimofey.utils.domain.accounts

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart
import com.tsarionovtimofey.utils.data.accounts.AccountsSource
import com.tsarionovtimofey.utils.domain.AuthException
import com.tsarionovtimofey.utils.domain.InternalException
import com.tsarionovtimofey.utils.domain.Result

class AccountsRepository(
    private val accountsSource: AccountsSource
) {

    private val accountsFlow = MutableStateFlow<Result<Account>>(Result.Pending())

    /**
     * Get the account data of the current logged-in user.
     *
     * The flow may emit exceptions:
     * - [AuthException] if the user is not logged in
     * - [InternalException]
     *
     * @return infinite flow, always success
     */
    fun getAccount(): Flow<Result<Account>> {
        return accountsFlow
            .onStart {
                emit(Result.Pending())
                reloadAccount(silently = true)
            }
    }

    /**
     * Try to reload the account data.
     * The flow returned by [getAccount] is automatically updated.
     *
     * @throws InternalException
     */
    suspend fun reloadAccount(silently: Boolean = false) {
        try {
            if (!silently) accountsFlow.value = Result.Pending()
            accountsFlow.value = Result.Success(accountsSource.getAccount())
        } catch (e: Exception) {
            accountsFlow.value = Result.Error(e)
        }
    }

    /**
     * Try to sign-in via third-party service.
     *
     * The flow returned by [getAccount] is automatically updated.
     *
     * @throws
     */
    suspend fun oauthSignIn() {
        val account = accountsSource.oauthSignIn()
        accountsFlow.value = Result.Success(account)
    }

    suspend fun signOut() {
        accountsSource.signOut()
        accountsFlow.value = Result.Error(AuthException())
    }

}