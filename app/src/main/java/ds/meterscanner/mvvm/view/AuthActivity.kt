package ds.meterscanner.mvvm.view

import android.databinding.ViewDataBinding
import ds.meterscanner.R
import ds.meterscanner.mvvm.AuthView
import ds.meterscanner.mvvm.BaseViewModel3
import ds.meterscanner.mvvm.viewmodel.AuthViewModel

class AuthActivity : BaseActivity3<ViewDataBinding, AuthViewModel>(), AuthView {

    override fun provideViewModel(): AuthViewModel = BaseViewModel3(this)
    override fun getLayoutId(): Int = R.layout.activity_auth

}
