package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.weather

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.weather.current.FragmentCurrentDisplay
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.weather.forecast.FragmentForecastDisplay

private const val CURRENT = 0

class FragmentAdapter(private val fm: FragmentManager,
                      private val listTitle: List<String>)
        : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(i: Int): Fragment {
        return when(i){
            CURRENT -> FragmentCurrentDisplay()
            else -> FragmentForecastDisplay()
        }
    }

    override fun getCount(): Int {
        return listTitle.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return listTitle[position]
    }

    fun findFragment(title: CharSequence): Fragment? {
        return fm.fragments.find { fragment -> (fragment as Spreadable).getTitle() == title }
    }
}
