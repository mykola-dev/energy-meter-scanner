package ds.meterscanner.databinding.viewmodel

import android.databinding.ObservableField
import ds.meterscanner.R
import ds.meterscanner.databinding.AuthView
import ds.meterscanner.databinding.BaseViewModel
import ds.meterscanner.rx.toggleProgress

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

    fun onSignIn() {
        authenticator.signInRx(login.get(),password.get())
            .toggleProgress(this)
            .bindTo(ViewModelEvent.DETACH)
            .subscribe({
                view.finish()
            }, {
                view.showSnackbar(view.getString(R.string.sign_in_error))
                it.printStackTrace()
            })

    }

    fun onSignUp() {
        authenticator.signUpRx(login.get(),password.get())
            .toggleProgress(this)
            .bindTo(ViewModelEvent.DETACH)
            .subscribe({
                view.showSnackbar(view.getString(R.string.user_created))
                onSignIn()
            }, {
                view.showSnackbar(view.getString(R.string.sign_up_error))
                it.printStackTrace()
            })
    }


}