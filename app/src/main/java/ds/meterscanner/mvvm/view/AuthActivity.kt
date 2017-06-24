package ds.meterscanner.mvvm.view

import android.databinding.ViewDataBinding
import ds.meterscanner.R
import ds.meterscanner.mvvm.AuthView
import ds.meterscanner.mvvm.BaseViewModel
import ds.meterscanner.mvvm.viewmodel.AuthViewModel

class AuthActivity : BaseActivity<ViewDataBinding, AuthViewModel>(), AuthView {

    override fun provideViewModel(): AuthViewModel = BaseViewModel(this)
    override fun getLayoutId(): Int = R.layout.activity_auth

}
