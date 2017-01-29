package ds.meterscanner.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import at.nineyards.anyline.modules.energy.EnergyScanView
import ds.meterscanner.ui.widget.BetterEnergyScanView.Direction.*
import ds.meterscanner.util.abs
import ds.meterscanner.util.toDips

typealias DimensionsCallback = (Int, Int) -> Unit

class BetterEnergyScanView(context: Context, attrs: AttributeSet) : EnergyScanView(context, attrs), View.OnTouchListener {

    enum class Direction { HORIZONTAL_SCALE, VERTICAL_SCALE, POSITION, NONE }

    var currX = -1f
    var currY = -1f
    var editMode: Direction = NONE
    var positionCallback: DimensionsCallback? = null
    var scaleCallback: DimensionsCallback? = null

    init {
        setOnTouchListener(this)
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        val scale = cameraController.frameToViewScale
        with(config) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    currX = event.x / scale
                    currY = event.y / scale
                }
                MotionEvent.ACTION_UP -> {
                    editMode = NONE
                }
                MotionEvent.ACTION_MOVE -> {
                    val offsetX = (event.x / scale - currX).toInt()
                    val offsetY = (event.y / scale - currY).toInt()
                    val viewportHeight = cutoutWidth / cutoutRatio

                    if (editMode == NONE) {
                        if (currY > cutoutOffsetY && currY < cutoutOffsetY + viewportHeight) {
                            editMode = POSITION
                        } else {
                            val offsetThreshold = 10.toDips(context)
                            if (offsetX.abs() > offsetThreshold || offsetY.abs() > offsetThreshold)
                                if (offsetY.abs() > offsetX.abs())
                                    editMode = VERTICAL_SCALE
                                else
                                    editMode = HORIZONTAL_SCALE
                        }
                    }


                    when (editMode) {
                        POSITION -> {
                            val newY = cutoutOffsetY + offsetY
                            positionCallback?.invoke(0, newY.coerceIn(0..(height / scale - viewportHeight).toInt()))
                        }
                        VERTICAL_SCALE -> {
                            val newHeight = viewportHeight + offsetY
                            scaleCallback?.invoke(cutoutWidth, newHeight.toInt().coerceIn(10, (height / scale - cutoutOffsetY).toInt()))
                        }
                        HORIZONTAL_SCALE -> {
                            val newWidth = (cutoutWidth + offsetX)
                            scaleCallback?.invoke(newWidth.coerceIn(10..(width / scale).toInt()), viewportHeight.toInt())
                        }

                        else -> {
                        }
                    }

                    if (editMode != NONE) {
                        currX = event.x / scale
                        currY = event.y / scale
                    }
                }
            }
        }

        return true
    }


}