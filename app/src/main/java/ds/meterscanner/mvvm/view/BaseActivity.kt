package ds.meterscanner.mvvm.view

import L
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import ds.bindingtools.startActivity
import ds.meterscanner.BR
import ds.meterscanner.R
import ds.meterscanner.mvvm.BaseView
import ds.meterscanner.mvvm.BaseViewModel
import ds.meterscanner.mvvm.observe

@Suppress("LeakingThis")
abstract class BaseActivity<out B : ViewDataBinding, out VM : BaseViewModel> : AppCompatActivity(), BaseView {

    override val viewModel: VM by lazy { provideViewModel() }
    val binding: B by lazy { DataBindingUtil.setContentView<B>(this, getLayoutId()) }

    protected open val bindImmediately = false
    protected val isDisplayUpButton = true

    init {
        L.v("::: ${javaClass.simpleName} initialized")
    }

    abstract fun provideViewModel(): VM

    abstract fun getLayoutId(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindView()
        initViewModel()
    }

    @CallSuper
    open protected fun initViewModel() {
        viewModel.showSnackbarCommand.observe(this) {
            showSnackbar(it.text)
        }
        viewModel.runAuthScreenCommand?.observe(this) {
            startActivity<AuthActivity>()
        }
        viewModel.finishCommand.observe(this) {
            finish()
        }

    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        setupToolbar()
    }

    private fun bindView() {
        binding.setVariable(BR.viewModel, viewModel)
        //binding.setVariable(BR.view, this)

        if (bindImmediately)
            binding.executePendingBindings()
    }

    private fun setupToolbar() {
        val toolbar:Toolbar? = findViewById(R.id.toolbar)
        if (toolbar != null) {
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(isDisplayUpButton)
        }
    }

    protected fun showSnackbar(
        text: String,
        callback: (() -> Unit)? = null,
        duration: Int = Snackbar.LENGTH_LONG,
        @StringRes actionText: Int = 0,
        actionCallback: (() -> Unit)? = null
    ) {
        val content: View = findViewById(R.id.coordinator) ?: findViewById(android.R.id.content)
        val s = Snackbar.make(content, text, duration)
        if (callback != null) {
            val snackCallback = object : Snackbar.Callback() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    callback()
                }
            }
            s.addCallback(snackCallback)
        }
        if (actionCallback != null)
            s.setAction(actionText, {
                actionCallback()
            })
        s.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

}
