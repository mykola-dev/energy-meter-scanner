package ds.meterscanner.mvvm.viewmodel

class AlarmItemViewModel {
    lateinit var onClickDelegate: (jobId: Int) -> Unit
    lateinit var onDeleteClickDelegate: (jobId: Int) -> Unit
    var jobId: Int = -1

    var time: String = ""
    var rescheduled: Boolean = false
    fun onClick() = onClickDelegate(jobId)
    fun onDeleteClick() = onDeleteClickDelegate(jobId)
}