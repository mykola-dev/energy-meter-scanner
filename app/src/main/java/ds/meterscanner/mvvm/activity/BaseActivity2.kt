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
import android.view.View
import com.github.salomonbrys.kodein.LazyKodein
import com.github.salomonbrys.kodein.LazyKodeinAware
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.erased.instance
import ds.bindingtools.runActivity
import ds.meterscanner.BR
import ds.meterscanner.R
import ds.meterscanner.data.Prefs
import ds.meterscanner.data.RefreshEvent
import ds.meterscanner.mvvm.BaseViewModel2
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

@Suppress("LeakingThis")
abstract class BaseActivity2<out B : ViewDataBinding, VM : BaseViewModel2> : AppCompatActivity(), LifecycleRegistryOwner, LazyKodeinAware {
    override val kodein: LazyKodein = LazyKodein { appKodein() }
    private val registry = LifecycleRegistry(this)

    override fun getLifecycle(): LifecycleRegistry {
        return registry
    }

    lateinit var viewModel: VM
    val binding: B by lazy { DataBindingUtil.setContentView<B>(this, getLayoutId()) }
    val bus: EventBus by instance()
    val prefs: Prefs by instance()

    protected open val bindImmediately = false


    init {
        L.v("::: ${javaClass.simpleName} initialized")
    }

    abstract fun provideViewModel(state: Bundle?): VM

    abstract fun getLayoutId(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = provideViewModel(savedInstanceState)
        bind()

        initViewModel()
    }

    @CallSuper
    open protected fun initViewModel() {
        viewModel.showSnackbarCommand.observe(this) {
            showSnackbar(it!!.text)
        }
        viewModel.runAuthScreenCommand.observe(this) {
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

    private fun bind(varId: Int = BR.viewModel) {
        binding.setVariable(varId, viewModel)
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
            s.setAction(actionText, View.OnClickListener {
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


