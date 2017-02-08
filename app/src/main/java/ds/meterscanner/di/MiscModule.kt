package ds.meterscanner.di

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import dagger.Module
import dagger.Provides
import org.greenrobot.eventbus.EventBus
import java.util.*
import javax.inject.Named
import javax.inject.Singleton

@Module
class MiscModule {
    @Provides @Singleton fun eventBus(): EventBus = EventBus
        .builder()
        //.addIndex(EventBusIndex::class.java)
        .build()

    @Provides @Singleton fun glide(ctx: Context): RequestManager = Glide.with(ctx)

    @Provides fun calendar(): Calendar {
        val cal = Calendar.getInstance()
        cal.firstDayOfWeek = Calendar.MONDAY
        return cal
    }

    @Provides @Singleton @Named("version") fun version(ctx: Context): String {
        val pInfo = ctx.packageManager.getPackageInfo(ctx.packageName, 0)
        return pInfo.versionName
    }

}