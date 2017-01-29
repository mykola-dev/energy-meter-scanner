package ds.meterscanner.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.evernote.android.state.StateSaver
import ds.meterscanner.R
import ds.meterscanner.databinding.ActivityChartsBinding
import ds.meterscanner.databinding.ChartsView
import ds.meterscanner.databinding.viewmodel.ChartsViewModel
import ds.meterscanner.databinding.viewmodel.Period
import ds.meterscanner.util.FileTools

class ChartsActivity : BaseActivity<ActivityChartsBinding, ChartsViewModel>(), ChartsView {

    override fun instantiateViewModel(state: Bundle?): ChartsViewModel {
        val vm = ChartsViewModel(this)
        StateSaver.restoreInstanceState(vm, state)
        return vm
    }

    override fun getLayoutId(): Int = R.layout.activity_charts

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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        StateSaver.saveInstanceState(viewModel, outState)
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
