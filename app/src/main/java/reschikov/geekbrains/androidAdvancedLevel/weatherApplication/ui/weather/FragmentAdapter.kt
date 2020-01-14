package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.weather

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R
import java.util.*

internal class FragmentAdapter(fm: FragmentManager,
                               private val listFragment: ArrayList<Fragment>,
                               private val context: Context)
    : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(i: Int): Fragment {
        return listFragment[i]
    }

    override fun getCount(): Int {
        return listFragment.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return if (position == 0) context.getString(R.string.current_state) else context.getString(R.string.forecast)
    }
}
