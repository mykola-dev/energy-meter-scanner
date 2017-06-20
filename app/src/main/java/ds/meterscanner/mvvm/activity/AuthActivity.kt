package ds.meterscanner.mvvm.activity

import android.arch.lifecycle.ViewModelProviders
import android.databinding.ViewDataBinding
import ds.meterscanner.R
import ds.meterscanner.mvvm.AuthView
import ds.meterscanner.mvvm.viewmodel.AuthViewModel

class AuthActivity : BaseActivity3<ViewDataBinding, AuthViewModel>(), AuthView {

    override fun provideViewModel(): AuthViewModel = ViewModelProviders.of(this).get(AuthViewModel::class.java)
    override fun getLayoutId(): Int = R.layout.activity_auth

}
