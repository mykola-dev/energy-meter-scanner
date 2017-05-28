package ds.meterscanner.mvvm

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import ds.meterscanner.adapter.DiffCallback
import java.lang.reflect.ParameterizedType

// simple viewmodel adapter
abstract class ViewModelAdapter<VM, D>(
    private var data: List<D> = listOf(),
    private val viewModelId: Int = ds.meterscanner.BR.viewModel
) : RecyclerView.Adapter<BindingHolder<ViewDataBinding, VM>>() {

    lateinit protected var context: Context

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        context = recyclerView.context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingHolder<ViewDataBinding, VM> {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(inflater, layoutId, parent, false)
        return BindingHolder(binding, instantiateViewModel())
    }

    override fun onBindViewHolder(holder: BindingHolder<ViewDataBinding, VM>, position: Int) {
        val item = getItem(position)
        onFillViewModel(holder, holder.viewModel, item, position)
        holder.binding.setVariable(viewModelId, holder.viewModel)
        holder.binding.executePendingBindings()
    }

    fun getItem(position: Int): D {
        return data[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun setData(data: List<D>) {
        val diffResult = DiffUtil.calculateDiff(DiffCallback(this.data, data))
        this.data = data
        diffResult.dispatchUpdatesTo(this)
    }

    fun getData() = data

    protected abstract val layoutId: Int

    protected abstract fun onFillViewModel(holder: BindingHolder<ViewDataBinding, VM>, viewModel: VM, item: D, position: Int)

    open protected fun instantiateViewModel(): VM {
        return getViewModelType().newInstance()
    }

    private fun getViewModelType(): Class<VM> {
        return getParametrizedType(javaClass).getActualTypeArguments()[0] as Class<VM>
    }

    private fun getParametrizedType(clazz: Class<*>): ParameterizedType {
        if (clazz.superclass == ViewModelAdapter::class.java) { // check that we are at the top of the hierarchy
            return clazz.genericSuperclass as ParameterizedType
        } else {
            return getParametrizedType(clazz.superclass)
        }
    }
}
