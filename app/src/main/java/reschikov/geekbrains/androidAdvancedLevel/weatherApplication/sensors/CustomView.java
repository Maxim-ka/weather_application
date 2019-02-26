package reschikov.geekbrains.androidadvancedlevel.weatherapplication.sensors;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R;

public class CustomView extends View {

    private static final float TEXT_SIZE = 42.0f;
    private static final float STROKE_WIDTH = 10.0f;
    private String cv_SensorName;
    private String cv_SensorReadings;
    private Paint paint;
    private Paint frame;
    private int color;
    private boolean missing;
    private boolean inPortrait;

    private void setCv_SensorName(String cv_SensorName) {
        this.cv_SensorName = cv_SensorName;
    }

    public void setCv_SensorReadings(String cv_SensorReadings) {
        this.cv_SensorReadings = cv_SensorReadings;
    }

    private void setColor(int color) {
        this.color = color;
    }

    public void setMissing(boolean missing) {
        this.missing = missing;
    }

    public CustomView(Context context) {
        super(context);
        init();
    }

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttr(context, attrs);
        init();
    }

    public CustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CustomView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttr(context, attrs);
        init();
    }

    private void initAttr(Context context, AttributeSet attrs){
        TypedArray typedArray = context.obtainStyledAttributes (attrs, R.styleable.CustomView, 0, 0);
        setCv_SensorName(typedArray.getString(R.styleable.CustomView_cv_SensorName));
        setCv_SensorReadings(typedArray.getString(R.styleable.CustomView_cv_SensorReadings));
        setColor(typedArray.getColor(R.styleable.CustomView_cv_Color, Color.BLUE));
        typedArray.recycle();
    }

    private void init() {
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        if (inPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            paint.setTextSize(TEXT_SIZE);
        else paint.setTextSize(TEXT_SIZE / 2);
        paint.setTextAlign(Paint.Align.CENTER);
        frame = new Paint();
        frame.setColor(Color.BLACK);
        frame.setStyle(Paint.Style.STROKE);
        frame.setStrokeWidth(STROKE_WIDTH);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int x = getWidth() / 2;
        canvas.drawRect(0, 0, getWidth(), getHeight(), frame);
        if (missing){
            setBackgroundColor(Color.GRAY);
            paint.setColor(Color.BLACK);
        }else{
            setBackgroundColor(Color.YELLOW);
            paint.setColor(color);
        }
        canvas.drawText(cv_SensorName, x, TEXT_SIZE, paint);
        if (inPortrait) canvas.drawText(cv_SensorReadings, x, TEXT_SIZE  * 3, paint);
        else canvas.drawText(cv_SensorReadings, x, TEXT_SIZE  * 6, paint);
    }
}


