package ds.meterscanner.databinding.layout

import android.view.View
import ds.bindingtools.bind
import ds.bindingtools.invoke
import ds.meterscanner.R
import ds.meterscanner.databinding.viewmodel.ToolbarViewModel
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.appcompat.v7.themedToolbar
import org.jetbrains.anko.design.appBarLayout
import org.jetbrains.anko.matchParent

class ToolbarLayout(val viewModel: ToolbarViewModel) : AnkoComponent<Any> {

    override fun createView(ui: AnkoContext<Any>): View = with(ui) {
        appBarLayout {
            themedToolbar(R.style.ActionBarTheme) {
                id = R.id.toolbar
                bind(viewModel { it::title_ }, this::setTitle)
                bind(viewModel { it::subtitle_ }, this::setSubtitle)
                popupTheme = R.style.ThemeOverlay_AppCompat_Light
            }.lparams(width = matchParent)
        }
    }

}