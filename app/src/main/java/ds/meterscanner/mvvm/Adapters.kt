package ds.meterscanner.mvvm

import android.databinding.BindingAdapter
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutCompat.VERTICAL
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

@BindingAdapter("visible")
fun setVisible(view: View, visible: Boolean?) {
    view.visibility = if (visible == null || visible) View.VISIBLE else View.GONE
}

@BindingAdapter("dividers")
fun dividers(recyclerView: RecyclerView, showDividers: Boolean) {
    if (showDividers) {
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, VERTICAL))
    }
}

@BindingAdapter("imageUrl")
fun loadImage(imageView: ImageView, url: String?) {
    Glide.with(imageView.context)
        .load(url)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        //.override(Target.SIZE_ORIGINAL,Target.SIZE_ORIGINAL)
        .into(imageView)
}

@Suppress("UNCHECKED_CAST")
@BindingAdapter("items", "adapter", requireAll = false)
fun <T> setAdapterItems(view: RecyclerView, items: List<T>?, adapter: ViewModelAdapter<*, T>) {
    if (view.adapter == null)
        view.adapter = adapter

    if (items != null) {
        (view.adapter as ViewModelAdapter<*, T>).data = items
    }

}