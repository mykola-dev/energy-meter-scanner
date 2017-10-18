package ds.meterscanner.db.model

import com.google.firebase.database.Exclude
import ds.meterscanner.util.formatTimeDate

data class Snapshot(
    var value: Double = 0.0,
    var outsideTemp: Int? = null,
    var boilerTemp: Int = 0,
    var image: String? = null
) {
    var timestamp: Long = 0
        set(value) {
            field = value
            date = formatTimeDate(value)
        }

    var id: String? = null
    lateinit var date: String

    // used in view
    @get:Exclude var selected = false

    init {
        timestamp = System.currentTimeMillis()
    }
}
