package ds.meterscanner.mvvm

import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView

open class BindingHolder<B : ViewDataBinding, VM>(var binding: B, var viewModel: VM) : RecyclerView.ViewHolder(binding.root)
