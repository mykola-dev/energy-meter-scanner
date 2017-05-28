@file:Suppress("EXPERIMENTAL_FEATURE_WARNING")

package ds.meterscanner.mvvm.viewmodel

import android.app.Application
import android.databinding.ObservableField
import ds.meterscanner.R
import ds.meterscanner.mvvm.BaseViewModel2
import ds.meterscanner.mvvm.invoke

// todo validation
class AuthViewModel(app: Application) : BaseViewModel2(app) {

    val login = ObservableField<String>()
    val password = ObservableField<String>()

    init {
        toolbar.title = getString(R.string.log_in)
    }

    fun onSignIn() = async {
        toggleProgress(true)
        try {
            authenticator.signIn(login.get(), password.get())
            finishCommand()
        } catch (e: Exception) {
            showSnackbarCommand(getString(R.string.sign_in_error))
            e.printStackTrace()
        } finally {
            toggleProgress(false)
        }

    }

    fun onSignUp() = async {
        toggleProgress(true)
        try {
            authenticator.signUp(login.get()!!, password.get()!!)
            showSnackbarCommand(getString(R.string.user_created))
            onSignIn()
        } catch (e: Exception) {
            showSnackbarCommand(getString(R.string.sign_up_error))
            e.printStackTrace()
        } finally {
            toggleProgress(false)
        }

    }


}