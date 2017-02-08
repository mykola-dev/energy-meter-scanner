package ds.meterscanner.di;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;

import javax.inject.Singleton;

import dagger.Component;
import ds.meterscanner.App;
import ds.meterscanner.StartupReceiver;
import ds.meterscanner.activity.SettingsActivity;
import ds.meterscanner.data.Prefs;
import ds.meterscanner.databinding.BaseView;
import ds.meterscanner.databinding.BaseViewModel;
import ds.meterscanner.scheduler.SnapshotJob;
import ds.meterscanner.ui.DebugDrawerController;

@Component(modules = {
        AppModule.class,
        NetworkModule.class,
        FirebaseModule.class,
        SchedulerModule.class,
        MiscModule.class
})
@Singleton()
public interface MainComponent {

    Calendar calendar();
    EventBus eventBus();
    Prefs prefs();

    void inject(App p0);
    void inject(StartupReceiver p0);
    void inject(SettingsActivity.SettingsFragment p0);
    void inject(SnapshotJob p0);
    void inject(DebugDrawerController p0);
    void inject(BaseViewModel<BaseView> p0);
}