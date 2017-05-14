package ds.meterscanner.activity

import L
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.text.InputType
import android.view.View
import android.widget.LinearLayout
import ds.bindingtools.bind
import ds.bindingtools.debug
import ds.bindingtools.invoke
import ds.meterscanner.R
import ds.meterscanner.databinding.AuthView
import ds.meterscanner.databinding.viewmodel.AuthViewModel
import org.jetbrains.anko.*
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.design.textInputLayout
import org.jetbrains.anko.sdk19.listeners.onClick
import org.jetbrains.anko.support.v4.swipeRefreshLayout

class AuthActivity : AnkoActivity<AuthViewModel>(), AuthView {

    override fun createLayout() {
        L.e("create view!")

        coordinatorLayout {

            include<View>(R.layout.toolbar)

            swipeRefreshLayout {
                setColorSchemeResources(R.color.colorAccent)
                isEnabled = false

                bind(viewModel { it::isRefreshing }, this::setRefreshing, this::isRefreshing)

                verticalLayout {
                    horizontalPadding = dip(72)
                    topPadding = dip(32)

                    textInputLayout {
                        editText {
                            hintResource = R.string.email
                            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                            bind(viewModel { it::login }, this::setText, this::getText)
                        }
                    }
                    textInputLayout {
                        editText {
                            hintResource = R.string.password
                            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                            bind(viewModel { it::password }, this::setText, this::getText)
                        }
                    }
                    linearLayout {
                        val lp = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT).also { it.weight = 1f }

                        button(R.string.sign_in) {
                            onClick {
                                L.e(viewModel.toString())
                                viewModel.debug()
                                viewModel.onSignIn()
                            }
                            bind(viewModel { it::isRefreshing }, { isEnabled = !it })
                        }.lparams(lp)

                        button(R.string.sign_up) {
                            onClick { viewModel.onSignUp() }
                            bind(viewModel { it::isRefreshing }, { isEnabled = !it })
                        }.lparams(lp)
                    }
                }
            }.lparams {
                behavior = AppBarLayout.ScrollingViewBehavior()
            }
        }

    }

    override fun instantiateViewModel(state: Bundle?) = AuthViewModel(this)

    override fun runAuthScreen() {
        // no-op
    }

}
