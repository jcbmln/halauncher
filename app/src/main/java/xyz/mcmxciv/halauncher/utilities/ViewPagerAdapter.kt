package xyz.mcmxciv.halauncher.utilities

import android.graphics.drawable.Drawable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import xyz.mcmxciv.halauncher.fragments.AppListFragment
import xyz.mcmxciv.halauncher.fragments.MainFragment

class ViewPagerAdapter
internal constructor(fm: FragmentManager, drawable: Drawable?) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private val wallpaper = drawable
    private val pageCount = 2

    override fun getItem(position: Int): Fragment {
        var fragment: Fragment? = null

        when (position) {
            0 -> fragment = MainFragment(wallpaper)
            1 -> fragment = AppListFragment(wallpaper)
        }

        return fragment!!
    }

    override fun getCount(): Int {
        return pageCount
    }
}