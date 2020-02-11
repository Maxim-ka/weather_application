package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.customView

import android.content.Context
import android.content.res.Configuration
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.*
import kotlin.math.min

private const val INDENT = 10
private const val THIRD = 0.33f
private const val DEFAULT_TEXT_SIZE = 28.0f
private const val STEP_CHANGE_SIZE = 2

class SensorView : View {

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initAttr(context, attrs)
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initAttr(context, attrs)
        init()
    }

    private val pText: Paint = Paint()
    private val pFrame: Paint = Paint()
    private val rectFrame = Rect()
    private val regionIcon = Rect()
    private val regionInfo = Rect()
    private val beginTextName: PointF = PointF()
    private val beginTextValue: PointF = PointF()
    private var cvSensorName: String? = null
    private var cvSensorDrawable: Drawable? = null
    private var cvStrokeWidth = 0.0f
    private var inPortrait: Boolean = true
    private var cvTextColor: Int = 0
    private var cvActiveColor: Int = 0
    private var cvDisableColor: Int = 0
    private var cvTextSize: Float = 0.0f
    private lateinit var cvSensorValue: String

    fun setSensorValue(cvSensorValue: String) {
        this.cvSensorValue = cvSensorValue
        checkSizeText()
        identifyBeginningOfText()
        invalidate()
    }

    fun setMissing(missing: Boolean) {
        if (missing) {
            setBackgroundColor(cvDisableColor)
            pText.color = Color.BLACK
        } else {
            setBackgroundColor(cvActiveColor)
            pText.color = cvTextColor
        }
        invalidate()
    }

    private fun initAttr(context: Context, attrs: AttributeSet?) {
        context.obtainStyledAttributes(attrs, R.styleable.SensorView, NAN_INT, NAN_INT).run {
            cvSensorName = getString(R.styleable.SensorView_cv_SensorName)
            cvSensorValue = getString(R.styleable.SensorView_cv_SensorValue) ?: resources.getString(R.string.absent)
            val resId = getResourceId(R.styleable.SensorView_cv_srcDrawable, NAN)
            if (resId != NAN)  cvSensorDrawable = AppCompatResources.getDrawable(context, resId)
            cvSensorDrawable?.let { it.bounds = Rect() }
            cvTextSize = getDimension(R.styleable.SensorView_cv_textSize, DEFAULT_TEXT_SIZE)
            cvStrokeWidth = getDimension(R.styleable.SensorView_cv_widthStroke, NAN_FLOAT)
            cvTextColor = getColor(R.styleable.SensorView_cv_textColor, Color.BLACK)
            cvActiveColor = getColor(R.styleable.SensorView_cv_activeColor, Color.WHITE)
            cvDisableColor = getColor(R.styleable.SensorView_cv_disableColor, Color.BLACK)
            recycle()
        }
    }

    private fun init() {
        inPortrait = resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        pText.apply {
            textSize = cvTextSize
            style = Paint.Style.FILL
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
        pFrame.apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = cvStrokeWidth
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var width = 0
        var height = 0
        if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY) {
            width = MeasureSpec.getSize(widthMeasureSpec)
        }
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
            height = MeasureSpec.getSize(heightMeasureSpec)
        }
        if (width > 0 && height > 0){
            setMeasuredDimension(width, height)
            rectFrame.set(0, 0, width, height)
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        cvSensorDrawable?.let { setBoundariesRegions(it)} ?: regionInfo.set(rectFrame)
        checkSizeText()
        identifyBeginningOfText()
    }

    private fun setBoundariesRegions(drawable: Drawable){
        if (inPortrait){
            regionIcon.set(rectFrame.left + INDENT, rectFrame.top + INDENT, (rectFrame.left + INDENT + rectFrame.width() * THIRD).toInt(), rectFrame.bottom - INDENT)
            regionInfo.set(regionIcon.right + INDENT,  rectFrame.top + INDENT, rectFrame.right - INDENT, rectFrame.bottom - INDENT)
        } else {
            regionIcon.set(rectFrame.left + INDENT, rectFrame.top + INDENT, rectFrame.right - INDENT, (rectFrame.top + INDENT + rectFrame.width() * THIRD).toInt())
            regionInfo.set(rectFrame.left + INDENT,  regionIcon.bottom + INDENT, rectFrame.right - INDENT, rectFrame.bottom - INDENT)
        }
        val scale = getScale(drawable)
        drawable.bounds.set((regionIcon.centerX() - drawable.intrinsicWidth * HALF * scale).toInt(), (regionIcon.centerY() - drawable.intrinsicHeight * HALF * scale).toInt(),
                (regionIcon.centerX() + drawable.intrinsicWidth * HALF * scale).toInt(), (regionIcon.centerY() + drawable.intrinsicHeight * HALF * scale).toInt())
    }

    private fun getScale(drawable: Drawable): Int {
        return min(regionIcon.width() / drawable.intrinsicWidth, regionIcon.height() / drawable.intrinsicHeight)
    }

    private fun checkSizeText(){
        cvSensorName?.let { pickTextSize(it) }
        pickTextSize(cvSensorValue)
    }

    private fun pickTextSize(text: String){
        while (regionInfo.width() <= pText.measureText(text)){
            pText.textSize -= STEP_CHANGE_SIZE
        }
    }

    private fun identifyBeginningOfText(){
        val regionHalfX = regionInfo.centerX().toFloat()
        val regionHalfY = regionInfo.centerY().toFloat()
        val offsetY = if (inPortrait) regionHalfY * HALF else regionHalfY * HALF * HALF
        cvSensorName?.let {
            beginTextName.set(regionHalfX, regionHalfY - offsetY)
            beginTextValue.set(regionHalfX, regionHalfY + offsetY)
        } ?: run{
            beginTextValue.set(regionHalfX, regionHalfY)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRect(rectFrame, pFrame)
        cvSensorDrawable?.draw(canvas)
        cvSensorName?.let { canvas.drawText(it, beginTextName.x, beginTextName.y, pText) }
        canvas.drawText(cvSensorValue, beginTextValue.x, beginTextValue.y, pText)
    }
}