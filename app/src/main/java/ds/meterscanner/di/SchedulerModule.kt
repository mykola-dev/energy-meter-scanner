package ds.meterscanner.di

import android.content.Context
import com.evernote.android.job.JobManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class SchedulerModule {

    @Provides
    @Singleton
    fun job(ctx: Context) = JobManager.create(ctx)
}