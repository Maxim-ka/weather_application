package reschikov.geekbrains.androidadvancedlevel.weatherapplication.reply;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import java.util.ArrayList;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R;

class FragmentAdapter extends FragmentPagerAdapter {

    private final ArrayList<Fragment> listFragment;
    private final Context context;
    private final FragmentManager fm;

    FragmentAdapter(FragmentManager fm, ArrayList<Fragment> listFragment, Context context) {
        super(fm);
        this.fm = fm;
        this.listFragment = listFragment;
        this.context = context;
    }

    public void clear(){
        for (int i = 0; i <listFragment.size() ; i++) {
            fm.beginTransaction().remove(listFragment.get(i)).commitNowAllowingStateLoss();
        }
    }

    @Override
    public Fragment getItem(int i) {
        return listFragment.get(i);
    }

    @Override
    public int getCount() {
        if (listFragment == null) return 0;
        return listFragment.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) return context.getString(R.string.current_state);
        return context.getString(R.string.forecast);
    }
}
