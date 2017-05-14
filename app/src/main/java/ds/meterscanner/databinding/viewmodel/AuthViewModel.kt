package ds.meterscanner.databinding.viewmodel

import ds.bindingtools.binding
import ds.meterscanner.R
import ds.meterscanner.databinding.AuthView
import ds.meterscanner.databinding.BaseViewModel
import kotlinx.coroutines.experimental.delay

// todo validation
class AuthViewModel(view: AuthView) : BaseViewModel<AuthView>(view) {

    var login: CharSequence by binding("")
    val password: CharSequence by binding("")

    override fun onCreate() {
        super.onCreate()
        toolbar.title.set(view.getString(R.string.log_in))
    }

    override fun onAttach() {
        super.onAttach()
    }

    fun onSignIn() = async {
        toggleProgress(true)
        try {
            delay(500)
            authenticator.signIn(login.toString(), password.toString())
            view.finish()
        } catch (e: Exception) {
            view.showSnackbar(view.getString(R.string.sign_in_error))
            e.printStackTrace()
        } finally {
            toggleProgress(false)
        }

    }

    fun onSignUp() = async {
        toggleProgress(true)
        try {
            authenticator.signUp(login.toString(), password.toString())
            view.showSnackbar(view.getString(R.string.user_created))
            onSignIn()
        } catch (e: Exception) {
            view.showSnackbar(view.getString(R.string.sign_up_error))
            e.printStackTrace()
        } finally {
            toggleProgress(false)
        }

    }


}