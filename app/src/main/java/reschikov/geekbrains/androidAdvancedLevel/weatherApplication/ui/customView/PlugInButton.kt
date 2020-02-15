package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.customView

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.HALF

private const val INDENT_ICON = 20.0f
private const val SCALE = 2.0f
private const val DOUBLING = 2

class PlugInButton private constructor(private val text: String?) {

    val clickRegion: RectF = RectF()
    var isRightSide: Boolean = false
    private val paint = Paint()
    private var buttonColor: Int = 0
    private var textColor: Int = 0
    private var textWidth: Float = 0.0f
    private var icon: Drawable? = null
    private var halfWidthIcon: Float = 0.0f
    private var halfHeightIcon: Float = 0.0f
    private lateinit var boundsIcon: Rect

    private fun setButtonColor(buttonColor: Int) {
        this.buttonColor = buttonColor
    }

    private fun setTextColor(textColor: Int) {
        this.textColor = textColor
    }

    private fun setTextSize(textSize: Int) {
        text?.let {
            paint.textSize = textSize.toFloat()
            textWidth = paint.measureText(it)
        }
    }

    private fun setIcon(icon: Drawable?) {
        icon?.let {
            this.icon = it
            boundsIcon = Rect()
            halfWidthIcon = (it.intrinsicWidth * SCALE) * HALF
            halfHeightIcon = (it.intrinsicHeight * SCALE) * HALF
        }
    }

    fun getFullWidth(): Float{
        var buttonWidth: Float = INDENT_ICON * DOUBLING
        icon?.let { buttonWidth += halfWidthIcon * DOUBLING}
        text?.let { buttonWidth += textWidth }
        return buttonWidth
    }

    fun onClick(x: Float, y: Float): Boolean {
        return !clickRegion.isEmpty && clickRegion.contains(x, y)
    }

    fun onDraw(canvas: Canvas, rect: RectF) {
        if (!rect.isEmpty) {
            paint.color = buttonColor
            canvas.drawRect(rect, paint)
            icon?.let {
                if (rect.width() > INDENT_ICON * DOUBLING) {
                    setBoundsIcon(rect)
                    it.draw(canvas)
                }
            }
            text?.let {
                paint.color = textColor
                if (rect.width() - halfWidthIcon * DOUBLING >= textWidth) {
                    drawText(canvas, rect)
                }
            }
        }
        clickRegion.set(rect)
    }

    private fun setBoundsIcon(rect: RectF) {
        icon?.let {
            rect.run{
                val topIcon = (centerY() - halfHeightIcon).toInt()
                val bottomIcon = (centerY() + halfHeightIcon).toInt()
                val leftIcon: Int = (centerX() - halfWidthIcon).toInt()
                val rightIcon: Int = (centerX() + halfWidthIcon).toInt()
                boundsIcon.set(leftIcon, topIcon, rightIcon, bottomIcon)
                it.bounds = boundsIcon
            }
        }
    }

    private fun drawText(canvas: Canvas, rect: RectF) {
        text?.let {
            val r = Rect()
            val cHeight = rect.height()
            val cWidth = rect.width()
            paint.textAlign = Paint.Align.CENTER
            paint.getTextBounds(it, 0, it.length, r)
            val y = cHeight * HALF + r.height() * HALF - r.bottom
            val x = cWidth * HALF - r.width() * HALF - r.left
            if (isRightSide) {
                val startX = icon?.let {
                    INDENT_ICON + boundsIcon.width() + x
                } ?: rect.left + x
                canvas.drawText(it, startX, rect.top + y, paint)
            } else {
                canvas.drawText(it, rect.left + x, rect.top + y, paint)
            }
        }
    }

    class Builder(text: String?) {

        private val button: PlugInButton = PlugInButton(text)

        fun setButtonColor(buttonColor: Int): Builder {
            button.setButtonColor(buttonColor)
            return this
        }

        fun setTextColor(textColor: Int): Builder {
            button.setTextColor(textColor)
            return this
        }

        fun setTextSize(textSize: Int): Builder {
            button.setTextSize(textSize)
            return this
        }

        fun setIcon(icon: Drawable?): Builder {
            button.setIcon(icon)
            return this
        }

        fun build(): PlugInButton {
            return button
        }
    }
}
