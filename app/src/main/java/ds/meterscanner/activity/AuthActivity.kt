package ds.meterscanner.activity

import android.databinding.ViewDataBinding
import android.os.Bundle
import ds.meterscanner.R
import ds.meterscanner.databinding.AuthView
import ds.meterscanner.databinding.viewmodel.AuthViewModel

class AuthActivity : BaseActivity<ViewDataBinding, AuthViewModel>(), AuthView {

    override fun instantiateViewModel(state: Bundle?) = AuthViewModel(this)
    override fun getLayoutId(): Int = R.layout.activity_auth

    override fun runAuthScreen() {
        // no-op
    }

}
