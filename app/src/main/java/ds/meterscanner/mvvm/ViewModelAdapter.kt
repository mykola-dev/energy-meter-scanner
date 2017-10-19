package ds.meterscanner.mvvm

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import ds.meterscanner.BR
import ds.meterscanner.adapter.DiffCallback
import java.lang.reflect.ParameterizedType

// simple viewmodel adapter
abstract class ViewModelAdapter<VM, D>(
    data: List<D> = emptyList(),
    private val viewModelId: Int = BR.viewModel
) : RecyclerView.Adapter<BindingHolder<ViewDataBinding, VM>>() {

    lateinit protected var context: Context

    var data: List<D> = data
        set(value) {
            val diffResult = DiffUtil.calculateDiff(DiffCallback(field, value))
            field = value
            diffResult.dispatchUpdatesTo(this)
        }

    override fun getItemCount(): Int = data.size

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

    fun getItem(position: Int): D = data[position]

    override fun getItemId(position: Int): Long = position.toLong()

    protected abstract val layoutId: Int

    protected abstract fun onFillViewModel(holder: BindingHolder<ViewDataBinding, VM>, viewModel: VM, item: D, position: Int)

    open protected fun instantiateViewModel(): VM = getViewModelType().newInstance()

    @Suppress("UNCHECKED_CAST")
    private fun getViewModelType(): Class<VM> = getParametrizedType(javaClass).actualTypeArguments[0] as Class<VM>

    private fun getParametrizedType(clazz: Class<*>): ParameterizedType =
        if (clazz.superclass == ViewModelAdapter::class.java) { // check that we are at the top of the hierarchy
            clazz.genericSuperclass as ParameterizedType
        } else {
            getParametrizedType(clazz.superclass)
        }
}
