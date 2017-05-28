package ds.meterscanner.mvvm.activity

import android.arch.lifecycle.ViewModelProviders
import android.databinding.ViewDataBinding
import android.os.Bundle
import ds.meterscanner.R
import ds.meterscanner.mvvm.viewmodel.AuthViewModel

class AuthActivity : BaseActivity2<ViewDataBinding, AuthViewModel>() {

    override fun provideViewModel(state: Bundle?): AuthViewModel = ViewModelProviders.of(this).get(AuthViewModel::class.java)
    override fun getLayoutId(): Int = R.layout.activity_auth


}
