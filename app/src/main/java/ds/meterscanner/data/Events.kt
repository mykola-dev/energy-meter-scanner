package ds.meterscanner.data

import ds.meterscanner.db.model.Snapshot

class RefreshEvent
class HistoryClickEvent(val snapshot: Snapshot)
class ItemSelectEvent(val snapshot: Snapshot, val totalSelected: Int)
class InterruptEvent
