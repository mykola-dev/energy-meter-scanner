package ds.meterscanner.databinding.layout

import android.content.res.Configuration
import android.support.design.widget.AppBarLayout
import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import ds.bindingtools.bind
import ds.bindingtools.invoke
import ds.meterscanner.R
import ds.meterscanner.databinding.viewmodel.MainViewModel
import ds.meterscanner.ui.dashboardButtonStyle
import ds.meterscanner.ui.lightFont
import org.jetbrains.anko.*
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.sdk19.listeners.onClick
import org.jetbrains.anko.support.v4.swipeRefreshLayout

class MainLayout(val viewModel: MainViewModel) : AnkoComponent<Any> {

    override fun createView(ui: AnkoContext<Any>): View = with(ui) {
        coordinatorLayout {
            addView(ToolbarLayout(viewModel.toolbar).createView(AnkoContext.create(ctx)).lparams(width = matchParent))

            swipeRefreshLayout {
                setColorSchemeResources(R.color.colorAccent)
                isEnabled = false

                bind(viewModel { it::isRefreshing }, this::setRefreshing, this::isRefreshing)

                frameLayout {

                    textView {
                        bind(viewModel { it::lastUpdated }, this::setText, this::getText)
                        typeface = lightFont
                        setTextAppearance(context, android.R.style.TextAppearance_Medium)
                        textColor = ContextCompat.getColor(context, R.color.secondary_text_color)
                    }.lparams {
                        gravity = Gravity.BOTTOM
                        margin = dip(16)
                    }

                    gridLayout {
                        useDefaultMargins = false
                        columnCount = if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) 2 else 4

                        themedImageView(R.style.Theme_AppCompat) {
                            imageResource = R.drawable.ic_photo_camera
                            onClick { viewModel.onCameraButton() }
                        }
                        themedImageView(R.style.Theme_AppCompat) {
                            imageResource = R.drawable.ic_timeline
                            onClick { viewModel.onChartsButton() }
                        }
                        themedImageView(R.style.Theme_AppCompat) {
                            imageResource = R.drawable.ic_view_list
                            onClick { viewModel.onListsButton() }
                        }
                        themedImageView(R.style.Theme_AppCompat) {
                            imageResource = R.drawable.ic_settings
                            onClick { viewModel.onSettingsButton() }
                        }

                    }.lparams(matchParent, wrapContent, Gravity.CENTER_VERTICAL) {
                        margin = dip(16)
                    }.applyRecursively {
                        when (it) {
                            is ImageView -> {
                                bind(viewModel { it::buttonsEnabled }, it::setEnabled)
                                it.apply(dashboardButtonStyle)
                            }
                        }
                    }

                    layoutParams = ViewGroup.LayoutParams(matchParent, matchParent)
                }

            }.lparams(matchParent, matchParent) {
                behavior = AppBarLayout.ScrollingViewBehavior()
            }
        }
    }
}