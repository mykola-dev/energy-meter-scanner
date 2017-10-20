package ds.meterscanner.mvvm.viewmodel

import ds.databinding.binding
import ds.meterscanner.R
import ds.meterscanner.mvvm.BindableViewModel
import ds.meterscanner.mvvm.invoke

class AuthViewModel : BindableViewModel() {

    // todo validation
    val login: String by binding("")
    val password: String by binding("")
    /*val loginError = ValidatorField(login) {
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
    }*/

    override val runAuthScreenCommand = null

    fun onSignIn() = async {
        //loginError.validate() && passwordError.validate() || return@async
        authenticator.signIn(login, password)
        finishCommand()
    }

    fun onSignUp() = async {
        //loginError.validate() && passwordError.validate() || return@async
        authenticator.signUp(login, password)
        showSnackbarCommand(getString(R.string.user_created))
        onSignIn()
    }
}