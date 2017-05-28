package ds.meterscanner.mvvm.viewmodel

import android.view.View

class AlarmItemViewModel {
    var time: String = ""
    var onClick: View.OnClickListener? = null
    var onDeleteClick: View.OnClickListener? = null
    var rescheduled: Boolean = false
}