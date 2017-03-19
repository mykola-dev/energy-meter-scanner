package ds.meterscanner.databinding.viewmodel

import android.databinding.ObservableField
import ds.meterscanner.R
import ds.meterscanner.databinding.AuthView
import ds.meterscanner.databinding.BaseViewModel

// todo validation
class AuthViewModel(view: AuthView) : BaseViewModel<AuthView>(view) {

    val login = ObservableField<String>()
    val password = ObservableField<String>()

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
            authenticator.signIn(login.get(), password.get())
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
            authenticator.signUp(login.get(), password.get())
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