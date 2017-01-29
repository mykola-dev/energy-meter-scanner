package ds.meterscanner.data

import android.text.format.DateFormat
import ds.meterscanner.databinding.viewmodel.SnapshotData

class CsvCreator {

    fun createCsvData(snapshotData: List<SnapshotData>, separator: String = ";"): List<String> {
        return snapshotData.map {
            formatDate(it.timestamp) + separator +
                it.value + separator +
                it.getValueWithOffset() + separator +
                it.delta + separator +
                it.temperature + separator +
                it.offset
        }
    }

    private fun formatDate(timestamp: Long): String = DateFormat.format("dd-MM-yyyy", timestamp).toString()
}