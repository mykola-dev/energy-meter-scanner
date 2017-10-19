package ds.meterscanner.adapter

import android.support.v7.util.DiffUtil

class DiffCallback<T>(private val oldData: List<T>, private val newData: List<T>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldData.size
    override fun getNewListSize(): Int = newData.size
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = oldData[oldItemPosition] == newData[newItemPosition]
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = oldData[oldItemPosition] == newData[newItemPosition]
}