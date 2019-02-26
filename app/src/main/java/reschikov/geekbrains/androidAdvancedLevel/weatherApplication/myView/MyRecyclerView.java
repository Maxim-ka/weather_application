package reschikov.geekbrains.androidadvancedlevel.weatherapplication.myView;

import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R;

public class MyRecyclerView extends RecyclerView {

    private final boolean isXLargeLayout = getResources().getBoolean(R.bool.is_X_large_layout);

    public MyRecyclerView(@NonNull Context context) {
        super(context);
    }

    public MyRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void scrollChange(boolean change){
        LinearLayoutManager llm = (LinearLayoutManager) getLayoutManager();
        if (llm != null){
            int direction;
            switch (getResources().getConfiguration().orientation){
                case Configuration.ORIENTATION_LANDSCAPE:
                    direction = (isXLargeLayout) ? LinearLayoutManager.VERTICAL : LinearLayoutManager.HORIZONTAL;
                    break;
                default:
                    direction = (change) ? LinearLayoutManager.HORIZONTAL : LinearLayoutManager.VERTICAL;
            }
            llm.setOrientation(direction);
            setLayoutManager(llm);
        }
    }
}
