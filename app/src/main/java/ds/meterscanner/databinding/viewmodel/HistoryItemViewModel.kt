package ds.meterscanner.databinding.viewmodel

import android.view.View

class HistoryItemViewModel {
    var date: String = ""
    var value: String = ""
    var onClick: View.OnClickListener? = null
    var onLongClick: View.OnLongClickListener? = null
    var selectMode = false
    var checked = false
    var imageUrl: String? = null
}