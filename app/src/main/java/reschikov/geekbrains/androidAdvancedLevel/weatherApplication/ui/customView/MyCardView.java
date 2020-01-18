package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.customView;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R;

public class MyCardView extends CardView {

    private enum Indent{

        DENSITY_160(6), DENSITY_240(16), DENSITY_320(26), DENSITY_420(34),DENSITY_480(38), DENSITY_560(40),
        DENSITY_640(44);

        private final int size;

        Indent(int size) {
            this.size = size;
        }
    }

    private final boolean isXLargeLayout = getResources().getBoolean(R.bool.is_X_large_layout);

    public MyCardView(@NonNull Context context) {
        super(context);
    }

    public MyCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void turnScreen(){
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metricsB = new DisplayMetrics();
        display.getMetrics(metricsB);
        int width = metricsB.widthPixels;
        int height = metricsB.heightPixels;
        int currWidth = (width < height) ? width : height;
        LinearLayout.LayoutParams params;
        int side = defineMargin();
        if (!isXLargeLayout && getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            params = new LinearLayout.LayoutParams(currWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(side / 2, side, side / 2, side);
        } else {
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(side, side / 2, side, side / 2);
        }
        setLayoutParams(params);
    }

    private int defineMargin(){
        DisplayMetrics dm = getResources().getDisplayMetrics();
        switch (dm.densityDpi){
            case DisplayMetrics.DENSITY_HIGH:
                return Indent.DENSITY_240.size;
            case DisplayMetrics.DENSITY_280:
            case DisplayMetrics.DENSITY_300:
            case DisplayMetrics.DENSITY_XHIGH:
                return Indent.DENSITY_320.size;
            case DisplayMetrics.DENSITY_360:
            case DisplayMetrics.DENSITY_400:
            case DisplayMetrics.DENSITY_420:
                return Indent.DENSITY_420.size;
            case DisplayMetrics.DENSITY_440:
            case DisplayMetrics.DENSITY_XXHIGH:
                return Indent.DENSITY_480.size;
            case DisplayMetrics.DENSITY_560:
                return Indent.DENSITY_560.size;
            case DisplayMetrics.DENSITY_XXXHIGH:
                return Indent.DENSITY_640.size;
            default:
                return Indent.DENSITY_160.size;
        }
    }
}
