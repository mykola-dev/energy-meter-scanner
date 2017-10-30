package ds.meterscanner.mvvm

import L
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import com.github.salomonbrys.kodein.*
import com.github.salomonbrys.kodein.android.appKodein
import ds.bindingtools.startActivity
import ds.bindingtools.withBindable
import ds.meterscanner.R
import ds.meterscanner.mvvm.view.AuthActivity
import kotlinx.android.synthetic.main.activity_auth.*

@Suppress("LeakingThis")
abstract class BindableActivity<out VM : BindableViewModel> : AppCompatActivity(), BindableView, KodeinInjected {
    override val injector: KodeinInjector = KodeinInjector()

    override val viewModel: VM by lazy { provideViewModel() }

    /**
     * Default ViewModel factory
     */
    protected inline fun <reified T : BindableViewModel> defaultViewModelOf(): T =
        viewModelOf { T::class.java.getConstructor(Kodein::class.java).newInstance(kodein().value) }

    protected open val isDisplayUpButton = true

    init {
        L.v("::: ${javaClass.simpleName} initialized")
    }

    abstract fun provideViewModel(): VM

    abstract fun getLayoutId(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inject(appKodein())
        setContentView(getLayoutId())
        bindView()
        initViewModel()

        println(kodein().value.container.bindings.description)
        L.v("tag=${instance<String>("tag")}")
    }

    @CallSuper
    protected open fun bindView() = withBindable(viewModel) {
        bind(::showProgress, { progressView?.isRefreshing = it }, { progressView?.isRefreshing ?: false })
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

    private fun setupToolbar() {
        val toolbar: Toolbar? = findViewById(R.id.toolbar)
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
