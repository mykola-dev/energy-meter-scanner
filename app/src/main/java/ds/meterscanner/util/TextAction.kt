package ds.meterscanner.util

import android.graphics.Color
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import android.widget.TextView
import ds.meterscanner.R
import io.palaima.debugdrawer.actions.Action

class TextAction(private val name: String) : Action {

    override fun getView(linearLayout: LinearLayout): View {
        val context = linearLayout.context
        val resources = context.resources

        val lp = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        lp.topMargin = resources.getDimensionPixelOffset(R.dimen.dd_padding_small)

        val tv = TextView(context)
        tv.setTextColor(Color.WHITE)
        tv.layoutParams = lp
        tv.text = name

        return tv
    }

    override fun onOpened() = Unit
    override fun onClosed() = Unit
    override fun onResume() = Unit
    override fun onPause() = Unit
    override fun onStart() = Unit
    override fun onStop() = Unit

}
