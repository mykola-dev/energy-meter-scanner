package ds.meterscanner.mvvm.viewmodel

import L
import android.databinding.ObservableField
import com.github.salomonbrys.kodein.erased.instance
import ds.meterscanner.R
import ds.meterscanner.mvvm.BaseViewModel
import ds.meterscanner.mvvm.DetailsView
import ds.meterscanner.db.model.Snapshot
import ds.meterscanner.util.formatTimeDate
import java.util.*

class DetailsViewModel(view: DetailsView, var snapshotId: String?) : BaseViewModel<DetailsView>(view) {

    val valueField = ObservableField<String>()
    val dateField = ObservableField<String>()
    val valueErrorField = ObservableField<String>()
    val imageUrl = ObservableField<String>()
    val outsideTemp = ObservableField<String>()
    val boilerTemp = ObservableField<String>()

    private lateinit var snapshot: Snapshot
    private val calendar: Calendar = instance()

    override fun onCreate() {
        super.onCreate()
        if (snapshotId != null) {
            toolbar.title = view.getString(R.string.edit_snapshot)
        } else {
            toolbar.title = view.getString(R.string.new_snapshot)
        }
    }


    override fun onAttach() {
        super.onAttach()
        fetchSnapshot()


    }

    private fun fetchSnapshot() = async {
        snapshot = if (snapshotId != null)
            db.getSnapshotById(snapshotId!!)
        else
            Snapshot(boilerTemp = prefs.boilerTemp, outsideTemp = prefs.currentTemperature.toInt())

        if (snapshot.value != 0.0)
            valueField.set(snapshot.value.toString())
        imageUrl.set(snapshot.image)
        dateField.set(formatTimeDate(snapshot.timestamp))
        outsideTemp.set(if (snapshot.outsideTemp != null) snapshot.outsideTemp.toString() else "")
        boilerTemp.set(snapshot.boilerTemp.toString())
    }

    fun onValueChanged(text: CharSequence) {
        L.v("$text")
        valueErrorField.set(null)
    }

    fun onDatePick() {
        view.pickDate(truncDate()) { date ->
            snapshot.timestamp = date.time
            dateField.set(formatTimeDate(date.time))
        }
    }

    fun onSave() {
        if (validateValue()) {
            if (boilerTemp.get().isNotEmpty())
                snapshot.boilerTemp = boilerTemp.get().toInt()

            if (outsideTemp.get().isNotEmpty())
                snapshot.outsideTemp = outsideTemp.get().toInt()
            else
                snapshot.outsideTemp = null

            db.saveSnapshot(snapshot)

            view.finish()
        }
    }

    private fun validateValue(): Boolean {
        try {
            snapshot.value = valueField.get().toDouble()
            return true
        } catch(e: Exception) {
            valueErrorField.set(view.getString(R.string.invalid_data))
            return false
        }
    }

    private fun truncDate(): Date {
        calendar.time = Date(snapshot.timestamp)
        calendar.set(Calendar.HOUR_OF_DAY, 12)
        calendar.set(Calendar.MINUTE, 0)
        return calendar.time
    }

}
