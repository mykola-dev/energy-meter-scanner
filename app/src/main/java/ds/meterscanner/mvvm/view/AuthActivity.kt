package ds.meterscanner.mvvm.view

import ds.databinding.bind
import ds.databinding.to
import ds.meterscanner.R
import ds.meterscanner.mvvm.AuthView
import ds.meterscanner.mvvm.BindableActivity
import ds.meterscanner.mvvm.viewModelOf
import ds.meterscanner.mvvm.viewmodel.AuthViewModel
import kotlinx.android.synthetic.main.activity_auth.*

class AuthActivity : BindableActivity<AuthViewModel>(), AuthView {

    override fun provideViewModel(): AuthViewModel = viewModelOf()
    override fun getLayoutId(): Int = R.layout.activity_auth

    override fun bindView() {
        super.bindView()
        viewModel.bind {
            to(this::login, loginField::setText, loginField::getText)
            to(::password, passwordField::setText, passwordField::getText)
            to(::showProgress, { signinButton.isEnabled = !it })
            to(::showProgress, { signupButton.isEnabled = !it })
            signinButton.setOnClickListener { this.onSignIn() }
            signupButton.setOnClickListener { this.onSignUp() }
        }
    }

}
