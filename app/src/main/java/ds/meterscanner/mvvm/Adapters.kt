package ds.meterscanner.mvvm

import android.databinding.BindingAdapter
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.View.VISIBLE
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import ds.meterscanner.mvvm.activity.ViewportListener
import ds.meterscanner.ui.recyclerview.DividerItemDecoration
import lecho.lib.hellocharts.gesture.ZoomType
import lecho.lib.hellocharts.model.ColumnChartData
import lecho.lib.hellocharts.model.LineChartData
import lecho.lib.hellocharts.model.Viewport
import lecho.lib.hellocharts.util.ChartUtils
import lecho.lib.hellocharts.view.ColumnChartView
import lecho.lib.hellocharts.view.LineChartView
import lecho.lib.hellocharts.view.PreviewColumnChartView

@BindingAdapter("visible")
fun setVisible(view: View, visible: Boolean?) {
    view.visibility = if (visible == null || visible) View.VISIBLE else View.GONE
}

@BindingAdapter("dividers")
fun dividers(recyclerView: RecyclerView, showDividers: Boolean) {
    if (showDividers) {
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL_LIST))
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

@BindingAdapter("data")
fun setColumnData(view: ColumnChartView, data: ColumnChartData?) {
    if (data == null)
        return

    view.visibility = VISIBLE
    view.columnChartData = data
}

@BindingAdapter("data")
fun setLineData(view: LineChartView, data: LineChartData?) {
    if (data == null || data.lines.isEmpty())
        view.visibility = View.GONE
    else {
        view.visibility = View.VISIBLE
        view.lineChartData = data
        val v = Viewport(view.maximumViewport.left, 30f, view.maximumViewport.right, -30f)
        view.maximumViewport = v
    }
}

@BindingAdapter("data", "columns", "lines", requireAll = false)
fun setupPreview(view: PreviewColumnChartView, data: ColumnChartData?, columns: ColumnChartView, lines: LineChartView) {
    if (data == null)
        return

    view.visibility = View.VISIBLE

    val previewData = ColumnChartData(data)
    previewData
        .columns
        .flatMap { it.values }
        .forEach { it.color = ChartUtils.DEFAULT_DARKEN_COLOR }
    previewData.axisYLeft = null
    previewData.axisXBottom = null
    view.columnChartData = previewData
    view.setViewportChangeListener(ViewportListener(columns, lines))

    val tempViewport = Viewport(columns.maximumViewport)
    val visibleItems = 20
    tempViewport.left = tempViewport.right - visibleItems
    view.currentViewport = tempViewport
    view.zoomType = ZoomType.HORIZONTAL
}
