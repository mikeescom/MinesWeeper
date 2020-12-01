package com.msmikeescom.minesweeper.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.msmikeescom.minesweeper.R
import com.msmikeescom.minesweeper.view.fragment.LeaderBoardFragment
import com.msmikeescom.minesweeper.view.fragment.MineFieldFragment
import com.msmikeescom.minesweeper.view.fragment.ProfileFragment
import com.msmikeescom.minesweeper.view.fragment.SettingsFragment


class MainActivity : AppCompatActivity() {

    private lateinit var pagerAdapter: PagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val viewPager = findViewById<ViewPager>(R.id.view_pager)
        val arrayFrag = arrayListOf(ProfileFragment(), MineFieldFragment(), SettingsFragment(), LeaderBoardFragment())
        pagerAdapter = PagerAdapter(supportFragmentManager, arrayFrag)
        viewPager.offscreenPageLimit = (arrayFrag.size - 1)
        viewPager.adapter = pagerAdapter
        viewPager.setCurrentItem(1, true)

        bottomNavigation.selectedItemId = R.id.game_pad

        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.profile -> {
                    viewPager.setCurrentItem(0, true)
                }
                R.id.game_pad -> {
                    viewPager.setCurrentItem(1, true)
                }
                R.id.settings -> {
                    viewPager.setCurrentItem(2, true)
                }
                R.id.leader_board -> {
                    viewPager.setCurrentItem(3, true)
                }
            }

            true
        }
    }
}