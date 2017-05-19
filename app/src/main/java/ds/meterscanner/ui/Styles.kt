package ds.meterscanner.ui

import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayout
import android.widget.ImageView
import android.widget.ImageView.ScaleType.CENTER
import ds.meterscanner.R
import org.jetbrains.anko.dip
import org.jetbrains.anko.margin
import org.jetbrains.anko.matchParent

/*<style name="dashboard_button">
<!-- <item name="android:layout_width">match_parent</item>
<item name="android:layout_height">match_parent</item>-->
<item name="android:background">@drawable/dashboard_button_background</item>
<item name="android:clickable">true</item>
<item name="android:scaleType">center</item>
<item name="android:elevation">4dp</item>
<item name="android:theme">@style/Theme.AppCompat</item>
<item name="android:adjustViewBounds">true</item>
<item name="layout_columnWeight">1</item>
<item name="android:layout_margin">8dp</item>
</style>*/
val dashboardButtonStyle: ImageView.() -> Unit = {
    layoutParams.width = matchParent
    layoutParams.height = matchParent
    (layoutParams as? GridLayout.LayoutParams)?.apply {
        columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
        margin = dip(8)
    }
    background = ContextCompat.getDrawable(context, R.drawable.dashboard_button_background)
    isClickable = true
    scaleType = CENTER
    elevation = dip(4).toFloat()
    adjustViewBounds = true
}
