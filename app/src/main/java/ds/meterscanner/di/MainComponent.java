package ds.meterscanner.di;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

import javax.inject.Singleton;

import dagger.Component;
import ds.meterscanner.App;
import ds.meterscanner.StartupReceiver;
import ds.meterscanner.activity.SettingsActivity;
import ds.meterscanner.adapter.HistoryAdapter;
import ds.meterscanner.data.Prefs;
import ds.meterscanner.databinding.BaseView;
import ds.meterscanner.databinding.BaseViewModel;
import ds.meterscanner.databinding.viewmodel.ChartsViewModel;
import ds.meterscanner.databinding.viewmodel.DetailsViewModel;
import ds.meterscanner.databinding.viewmodel.MainViewModel;
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
    @Version String version();

    void inject(@NotNull App p0);
    void inject(@NotNull StartupReceiver p0);
    void inject(@NotNull SettingsActivity.SettingsFragment p0);
    void inject(@NotNull SnapshotJob p0);
    void inject(@NotNull DebugDrawerController p0);
    void inject(@NotNull BaseViewModel<BaseView> p0);
    void inject(@NotNull MainViewModel p0);
    void inject(@NotNull DetailsViewModel p0);
    void inject(@NotNull ChartsViewModel p0);
    void inject(@NotNull HistoryAdapter historyAdapter);

}