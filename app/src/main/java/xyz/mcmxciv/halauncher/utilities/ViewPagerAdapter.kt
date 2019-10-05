package xyz.mcmxciv.halauncher.utilities

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import xyz.mcmxciv.halauncher.fragments.AppListFragment
import xyz.mcmxciv.halauncher.fragments.MainFragment

class ViewPagerAdapter
internal constructor(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private val pageCount = 2

    override fun getItem(position: Int): Fragment {
        var fragment: Fragment? = null

        when (position) {
            0 -> fragment = MainFragment()
            1 -> fragment = AppListFragment()
        }

        return fragment!!
    }

    override fun getCount(): Int {
        return pageCount
    }
}