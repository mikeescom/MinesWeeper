package com.msmikeescom.minesweeper.view.activity

import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.msmikeescom.minesweeper.R
import com.msmikeescom.minesweeper.view.custom.NonSwipeableViewPager

class TabbedActivity : AppCompatActivity() {

    private lateinit var viewPager: NonSwipeableViewPager
    private lateinit var sectionsPagerAdapter : SectionsPagerAdapter
    private lateinit var tabs : TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tabbed)

        sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        viewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        tabs = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
        tabs.getTabAt(0)?.icon = ResourcesCompat.getDrawable(resources, R.drawable.user, null)
        tabs.getTabAt(1)?.icon = ResourcesCompat.getDrawable(resources, R.drawable.gamepad, null)
        tabs.getTabAt(2)?.icon = ResourcesCompat.getDrawable(resources, R.drawable.gear, null)
    }
}