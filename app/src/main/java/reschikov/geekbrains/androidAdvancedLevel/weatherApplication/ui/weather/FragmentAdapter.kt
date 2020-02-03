package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.weather

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

internal class FragmentAdapter(fm: FragmentManager,
    private val listFragment: List<Pair<String, Fragment>>)
        : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(i: Int): Fragment {
        return listFragment[i].second
    }

    override fun getCount(): Int {
        return listFragment.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return listFragment[position].first
    }

    fun findFragment(title: String): Fragment? {
        return listFragment.find {it.first == title}?.second
    }
}
