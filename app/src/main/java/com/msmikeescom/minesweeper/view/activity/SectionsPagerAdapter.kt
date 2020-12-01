package com.msmikeescom.minesweeper.view.activity

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.msmikeescom.minesweeper.R
import com.msmikeescom.minesweeper.view.fragment.MineFieldFragment
import com.msmikeescom.minesweeper.view.fragment.ProfileFragment
import com.msmikeescom.minesweeper.view.fragment.SettingsFragment

private val TAB_TITLES = arrayOf(
        R.string.profile_title,
        R.string.mine_field_title,
        R.string.settings_title
)

class SectionsPagerAdapter(private val context: Context, fm: FragmentManager)
    : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        val fragment: Fragment = MineFieldFragment.getInstance(position)
        when (position) {
            0 -> return ProfileFragment.getInstance(position)
            1 -> return MineFieldFragment.getInstance(position)
            2 -> return SettingsFragment.getInstance(position)
        }
        return fragment
    }

    override fun getCount(): Int {
        return TAB_TITLES.size
    }
}