package ds.meterscanner.mvvm.view

import android.app.Activity
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import ds.databinding.bind
import ds.meterscanner.R
import ds.meterscanner.mvvm.BindableActivity
import ds.meterscanner.mvvm.ChartsView
import ds.meterscanner.mvvm.viewModelOf
import ds.meterscanner.mvvm.viewmodel.ChartsViewModel
import ds.meterscanner.mvvm.viewmodel.Period
import ds.meterscanner.util.FileTools
import kotlinx.android.synthetic.main.activity_charts.*
import kotlinx.android.synthetic.main.toolbar.*
import lecho.lib.hellocharts.gesture.ZoomType
import lecho.lib.hellocharts.model.ColumnChartData
import lecho.lib.hellocharts.model.Viewport
import lecho.lib.hellocharts.util.ChartUtils

class ChartsActivity : BindableActivity<ChartsViewModel>(), ChartsView {

    override fun provideViewModel(): ChartsViewModel = viewModelOf()

    override fun getLayoutId(): Int = R.layout.activity_charts

    override fun bindView() {
        super.bindView()
        toolbar.title = getString(R.string.charts)
        columnsChart.isScrollEnabled = false
        columnsChart.isZoomEnabled = false
        linesChart.isScrollEnabled = false
        linesChart.isZoomEnabled = false
        previewChart.setViewportChangeListener(ViewportListener(columnsChart, linesChart))

        viewModel.apply {
            bind(::linesData, {
                linesChart.lineChartData = it
                val v = Viewport(linesChart.maximumViewport.left, 30f, linesChart.maximumViewport.right, -30f)
                linesChart.maximumViewport = v
            })
            bind(::columnsData, columnsChart::setColumnChartData)
            bind(::columnsData, {
                val previewData = ColumnChartData(it)
                previewData
                    .columns
                    .flatMap { it.values }
                    .forEach { it.color = ChartUtils.DEFAULT_DARKEN_COLOR }
                previewData.axisYLeft = null
                previewData.axisXBottom = null
                previewChart.columnChartData = previewData
                val tempViewport = Viewport(columnsChart.maximumViewport)
                val visibleItems = 20
                tempViewport.left = tempViewport.right - visibleItems
                previewChart.currentViewport = tempViewport
                previewChart.zoomType = ZoomType.HORIZONTAL
            })
            bind(this::checkedButtonId, radioGroup::check, radioGroup::getCheckedRadioButtonId)
            bind(::showProgress, { radioGroup.isEnabled = !it })
            radioGroup.setOnCheckedChangeListener { _, checkedId -> viewModel.onCheckedChanged(checkedId) }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.charts, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.findItem(when (viewModel.period) {
            Period.ALL -> R.id.action_period_all
            Period.YEAR -> R.id.action_period_year
            Period.LAST_SEASON -> R.id.action_period_season
        }).isChecked = true
        menu.findItem(R.id.action_correction).isChecked = viewModel.positiveCorrection
        menu.findItem(R.id.action_show_temp).isChecked = viewModel.tempVisible

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.isCheckable)
            item.isChecked = !item.isChecked

        when (item.itemId) {
            R.id.action_correction -> viewModel.toggleCorection(item.isChecked)
            R.id.action_show_temp -> viewModel.toggleTemperature(item.isChecked)
            R.id.action_period_all -> viewModel.period = Period.ALL
            R.id.action_period_year -> viewModel.period = Period.YEAR
            R.id.action_period_season -> viewModel.period = Period.LAST_SEASON
            R.id.action_export_csv -> FileTools.chooseDir(this, FileTools.CSV_FILE_NAME)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Requests.SAVE_FILE && resultCode == Activity.RESULT_OK) {
            viewModel.onDirectoryChoosen(contentResolver, data!!.data)
        }
    }
}
