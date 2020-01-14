package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.myView

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.HALF

private const val INDENT_ICON = 20.0f
private const val SCALE = 1.5f

class PlugInButton private constructor(private val text: String?) {

    val clickRegion: RectF = RectF()
    var isRightSide: Boolean = false
    private val paint = Paint()
    private var buttonColor: Int = 0
    private var textColor: Int = 0
    private var textWidth: Float = 0.0f
    private var icon: Drawable? = null
    private var widthIcon: Float = 0.0f
    private var heightIcon: Float = 0.0f
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

    private fun setIcon(icon: Drawable) {
        this.icon = icon
        boundsIcon = Rect()
        widthIcon = icon.intrinsicWidth * SCALE
        heightIcon = icon.intrinsicHeight * SCALE
    }

    fun getFullWidth(): Float{
        var buttonWidth: Float = INDENT_ICON * 2
        icon?.let { buttonWidth += widthIcon }
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
                if (rect.width() > INDENT_ICON * 2) {
                    setBoundsIcon(rect)
                    it.draw(canvas)
                }
            }
            text?.let {
                paint.color = textColor
                if (rect.width() - widthIcon >= textWidth) {
                    drawText(canvas, rect)
                }
            }
        }
        clickRegion.set(rect)
    }

    private fun setBoundsIcon(rect: RectF) {
        icon?.let {
            with(rect){
                val leftIcon: Int
                val rightIcon: Int
                val topIcon = (top + height() * HALF - heightIcon * HALF).toInt()
                val bottomIcon = (bottom - height() * HALF + heightIcon * HALF).toInt()
                if (isRightSide) {
                    leftIcon = (left + INDENT_ICON).toInt()
                    rightIcon = (leftIcon + widthIcon + INDENT_ICON).toInt()
                } else {
                    rightIcon = (right - INDENT_ICON).toInt()
                    leftIcon = (rightIcon - widthIcon - INDENT_ICON).toInt()
                }
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

        fun setIcon(icon: Drawable): Builder {
            button.setIcon(icon)
            return this
        }

        fun build(): PlugInButton {
            return button
        }
    }
}
