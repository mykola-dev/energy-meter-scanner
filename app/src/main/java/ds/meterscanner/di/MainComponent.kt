/*
package ds.meterscanner.di

import dagger.Component
import ds.meterscanner.App
import ds.meterscanner.StartupReceiver
import ds.meterscanner.activity.SettingsActivity
import ds.meterscanner.data.Prefs
import ds.meterscanner.databinding.BaseView
import ds.meterscanner.databinding.BaseViewModel
import ds.meterscanner.scheduler.SnapshotJob
import ds.meterscanner.ui.DebugDrawerController
import org.greenrobot.eventbus.EventBus
import java.util.*
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(
    AppModule::class,
    NetworkModule::class,
    FirebaseModule::class,
    SchedulerModule::class,
    MiscModule::class

))
interface MainComponent {
    fun calendar(): Calendar
    fun eventBus(): EventBus
    fun prefs(): Prefs

    fun inject(app: App)
    fun inject(startupReceiver: StartupReceiver)
    fun inject(settingsFragment: SettingsActivity.SettingsFragment)
    fun inject(obj: SnapshotJob)
    fun inject(debugDrawerController: DebugDrawerController)
    fun inject(baseViewModel: BaseViewModel<BaseView>)
}*/
