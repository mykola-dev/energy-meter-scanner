package ds.meterscanner.mvvm

import android.content.Context
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ds.meterscanner.adapter.DiffCallback
import java.lang.reflect.ParameterizedType

abstract class SimpleAdapter<H : RecyclerView.ViewHolder, D : Any>(
    data: List<D> = emptyList()
) : RecyclerView.Adapter<H>() {

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): H {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(layoutId, parent, false)
        return instantiateHolder(view)
    }

    override fun onBindViewHolder(holder: H, position: Int) {
        val item = getItem(position)
        onFillView(holder, item, position)
    }

    fun getItem(position: Int): D = data[position]

    override fun getItemId(position: Int): Long = position.toLong()

    protected abstract val layoutId: Int

    protected abstract fun onFillView(holder: H, item: D, position: Int) : Any

    open protected fun instantiateHolder(view: View): H = getHolderType().getConstructor(View::class.java).newInstance(view)

    @Suppress("UNCHECKED_CAST")
    private fun getHolderType(): Class<H> = getParametrizedType(javaClass).actualTypeArguments[0] as Class<H>

    private fun getParametrizedType(clazz: Class<*>): ParameterizedType =
        if (clazz.superclass == SimpleAdapter::class.java) { // check that we are at the top of the hierarchy
            clazz.genericSuperclass as ParameterizedType
        } else {
            getParametrizedType(clazz.superclass)
        }
}
