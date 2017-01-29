package ds.meterscanner.data

import ds.meterscanner.db.model.Snapshot

class RefreshEvent
class HistoryClickEvent(val snapshot: Snapshot)
class AlarmClickEvent(val jobId: Int)
class AlarmDeleteEvent(val jobId: Int)
class ItemSelectEvent(val snapshot: Snapshot, val totalSelected: Int)
class InterruptEvent
