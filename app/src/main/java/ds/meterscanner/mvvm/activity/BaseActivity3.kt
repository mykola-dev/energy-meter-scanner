package ds.meterscanner.mvvm.activity

import L
import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.LifecycleRegistryOwner
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import com.github.salomonbrys.kodein.erased.instance
import ds.bindingtools.runActivity
import ds.meterscanner.BR
import ds.meterscanner.R
import ds.meterscanner.data.RefreshEvent
import ds.meterscanner.mvvm.BaseViewModel3
import ds.meterscanner.mvvm.View3
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

@Suppress("LeakingThis")
abstract class BaseActivity3<out B : ViewDataBinding, out VM : BaseViewModel3> : AppCompatActivity(), LifecycleRegistryOwner, View3 {
    private val registry = LifecycleRegistry(this)

    val bus: EventBus = instance()

    override fun getLifecycle(): LifecycleRegistry = registry

    override val viewModel: VM by lazy { provideViewModel() }
    val binding: B by lazy { DataBindingUtil.setContentView<B>(this, getLayoutId()) }

    protected open val bindImmediately = false

    init {
        L.v("::: ${javaClass.simpleName} initialized")
    }

    abstract fun provideViewModel(): VM

    abstract fun getLayoutId(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind()

        //lifecycle.addObserver(EventBusObserver(kodein))

        initViewModel()
    }

    @CallSuper
    open protected fun initViewModel() {
        viewModel.showSnackbarCommand.observe(this) {
            showSnackbar(it.text)
        }
        viewModel.runAuthScreenCommand?.observe(this) {
            runActivity<AuthActivity>()
        }
        viewModel.finishCommand.observe(this) {
            finish()
        }

    }

    override fun onStart() {
        super.onStart()
        bus.register(this)
    }

    override fun onStop() {
        super.onStop()
        bus.unregister(this)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        setupToolbar()
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        viewModel.onPrepareMenu(menu)
        return super.onPrepareOptionsMenu(menu)
    }

    private fun bind() {
        binding.setVariable(BR.viewModel, viewModel)
        binding.setVariable(BR.view, this)

        if (bindImmediately)
            binding.executePendingBindings()
    }

    private fun setupToolbar() {
        val toolbar = findViewById(R.id.toolbar) as Toolbar?
        if (toolbar != null) {
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(isDisplayUpButton())
            onToolbarCreated(toolbar)
        }
    }

    protected fun showSnackbar(
        text: String,
        callback: (() -> Unit)? = null,
        duration: Int = Snackbar.LENGTH_LONG,
        @StringRes actionText: Int = 0,
        actionCallback: (() -> Unit)? = null
    ) {
        val content = findViewById(R.id.coordinator) ?: findViewById(android.R.id.content)
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

    protected fun isDisplayUpButton() = true

    protected fun onToolbarCreated(toolbar: Toolbar) {

    }

    @Subscribe
    fun onRefresh(e: RefreshEvent) {
    }

}


