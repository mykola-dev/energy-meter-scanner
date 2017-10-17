package ds.meterscanner.mvvm.viewmodel

import android.databinding.ObservableField
import android.util.Patterns
import ds.meterscanner.R
import ds.meterscanner.mvvm.BaseViewModel
import ds.meterscanner.mvvm.invoke

class AuthViewModel : BaseViewModel() {

    val login = ObservableField<String>("")
    val password = ObservableField<String>("")
    val loginError = ValidatorField(login) {
        when {
            !Patterns.EMAIL_ADDRESS.matcher(it).matches() -> getString(R.string.wrong_email)
            else -> ""
        }
    }
    val passwordError = ValidatorField(password) {
        when {
            it.isEmpty() -> getString(R.string.shouldnt_be_empty)
            else -> ""
        }
    }

    override val runAuthScreenCommand = null

    init {
        toolbar.title = getString(R.string.log_in)
    }

    fun onSignIn() = async {
        loginError.validate() && passwordError.validate() || return@async
        authenticator.signIn(login.get(), password.get())
        finishCommand()
    }

    fun onSignUp() = async {
        loginError.validate() && passwordError.validate() || return@async
        authenticator.signUp(login.get()!!, password.get()!!)
        showSnackbarCommand(getString(R.string.user_created))
        onSignIn()
    }
}