package ds.meterscanner.mvvm.view

import ds.bindingtools.bind
import ds.bindingtools.withBindable
import ds.meterscanner.R
import ds.meterscanner.mvvm.AuthView
import ds.meterscanner.mvvm.BindableActivity
import ds.meterscanner.mvvm.viewModelOf
import ds.meterscanner.mvvm.viewmodel.AuthViewModel
import kotlinx.android.synthetic.main.activity_auth.*
import kotlinx.android.synthetic.main.toolbar.*

class AuthActivity : BindableActivity<AuthViewModel>(), AuthView {

    override fun provideViewModel(): AuthViewModel = viewModelOf()
    override fun getLayoutId(): Int = R.layout.activity_auth

    override fun bindView() {
        super.bindView()
        toolbar.title = getString(R.string.log_in)
        signinButton.setOnClickListener { viewModel.onSignIn() }
        signupButton.setOnClickListener { viewModel.onSignUp() }

        withBindable(viewModel) {
            bind(::login, loginField)
            bind(::password, passwordField)
            bind(loginError::error, loginLayout::setError)
            bind(passwordError::error, passwordLayout::setError)
            bind(::showProgress, {
                signinButton.isEnabled = !it
                signupButton.isEnabled = !it
            })
        }
    }
}
