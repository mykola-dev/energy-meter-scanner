package ds.meterscanner.scheduler

import com.evernote.android.job.Job
import com.evernote.android.job.JobCreator

class SnapshotJobCreator : JobCreator {

    override fun create(tag: String): Job? {
        return Class.forName(tag).newInstance() as Job?
    }
}