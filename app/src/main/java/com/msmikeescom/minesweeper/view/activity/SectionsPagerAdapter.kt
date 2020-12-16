package com.msmikeescom.minesweeper.view.activity

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.msmikeescom.minesweeper.view.fragment.MineFieldFragment
import com.msmikeescom.minesweeper.view.fragment.ProfileFragment
import com.msmikeescom.minesweeper.view.fragment.SettingsFragment

class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        val fragment: Fragment = MineFieldFragment.getInstance(position)
        when (position) {
            0 -> return SettingsFragment.getInstance(position)
            1 -> return MineFieldFragment.getInstance(position)
            2 -> return ProfileFragment.getInstance(position)
        }
        return fragment
    }

    override fun getCount(): Int {
        return 3
    }
}