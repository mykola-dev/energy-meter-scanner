package ds.meterscanner.di

import dagger.Component
import ds.meterscanner.App
import ds.meterscanner.StartupReceiver
import ds.meterscanner.activity.BaseActivity
import ds.meterscanner.activity.SettingsActivity
import ds.meterscanner.scheduler.SnapshotJob
import ds.meterscanner.ui.DebugDrawerController
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
    fun getCalendar(): Calendar

    fun inject(app: App)
    fun inject(obj: BaseActivity<*, *>)
    fun inject(startupReceiver: StartupReceiver)
    fun inject(settingsFragment: SettingsActivity.SettingsFragment)
    fun inject(obj: SnapshotJob)
    fun inject(debugDrawerController: DebugDrawerController)
}