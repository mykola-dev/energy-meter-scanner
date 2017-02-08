package ds.meterscanner.databinding.viewmodel

import L
import android.databinding.ObservableField
import ds.meterscanner.R
import ds.meterscanner.databinding.BaseViewModel
import ds.meterscanner.databinding.DetailsView
import ds.meterscanner.db.model.Snapshot
import ds.meterscanner.util.formatTimeDate
import io.reactivex.Single.just
import java.util.*
import javax.inject.Inject

class DetailsViewModel(view: DetailsView, var snapshotId: String?) : BaseViewModel<DetailsView>(view) {

    val valueField = ObservableField<String>()
    val dateField = ObservableField<String>()
    val valueErrorField = ObservableField<String>()
    val imageUrl = ObservableField<String>()
    val outsideTemp = ObservableField<String>()
    val boilerTemp = ObservableField<String>()

    private lateinit var snapshot: Snapshot
    @Inject lateinit var  calendar:Calendar

    override fun onCreate() {
        super.onCreate()
        if (snapshotId != null) {
            toolbar.title.set(view.getString(R.string.edit_snapshot))
        } else {
            toolbar.title.set(view.getString(R.string.new_snapshot))
        }
    }


    override fun onAttach() {
        super.onAttach()
        (if (snapshotId != null)
            db.getSnapshotById(snapshotId!!)
        else
            just(Snapshot(boilerTemp = prefs.boilerTemp, outsideTemp = prefs.currentTemperature.toInt())))
            .subscribe { it ->
                if (it.value != 0.0)
                    valueField.set(it.value.toString())
                imageUrl.set(it.image)
                dateField.set(formatTimeDate(it.timestamp))
                outsideTemp.set(if (it.outsideTemp != null) it.outsideTemp.toString() else "")
                boilerTemp.set(it.boilerTemp.toString())
                snapshot = it
            }

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
