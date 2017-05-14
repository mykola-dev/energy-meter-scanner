package ds.meterscanner.activity

import L
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import com.github.salomonbrys.kodein.LazyKodein
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.erased.instance
import ds.bindingtools.runActivity
import ds.meterscanner.R
import ds.meterscanner.data.Prefs
import ds.meterscanner.data.RefreshEvent
import ds.meterscanner.databinding.BaseView
import ds.meterscanner.databinding.BaseViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

abstract class AnkoActivity<VM : BaseViewModel<*>> : AppCompatActivity(), BaseView {
    final override val kodein: LazyKodein = LazyKodein { appKodein() }

    override lateinit var viewModel: VM
    val bus: EventBus by instance()
    val prefs:Prefs by instance()

    init {
        L.v("::: ${javaClass.simpleName} initialized")
    }

    abstract fun instantiateViewModel(state: Bundle?): VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = instantiateViewModel(savedInstanceState)
        createLayout()
        viewModel.onCreate()
    }

    override fun getColour(id: Int): Int = ContextCompat.getColor(this, id)

    override fun onStart() {
        super.onStart()
        viewModel.onAttach()
        bus.register(this)
    }


    override fun onStop() {
        super.onStop()
        viewModel.onDetach()
        bus.unregister(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        setupToolbar()
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        viewModel.onPrepareMenu(menu)
        return super.onPrepareOptionsMenu(menu)
    }

    abstract protected fun createLayout()

    private fun setupToolbar() {
        val toolbar = findViewById(R.id.toolbar) as Toolbar?
        if (toolbar != null) {
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(isDisplayUpButton())
            onToolbarCreated(toolbar)
        }
    }

    protected fun isDisplayUpButton() = true

    protected fun onToolbarCreated(toolbar: Toolbar) {
    }

    override fun showSnackbar(
        text: String,
        callback: (() -> Unit)?,
        duration: Int,
        @StringRes actionText: Int,
        actionCallback: (() -> Unit)?
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

    override fun runAuthScreen() {
        runActivity<AuthActivity>()
    }

    @Subscribe
    fun onRefresh(e: RefreshEvent) {
    }

}


