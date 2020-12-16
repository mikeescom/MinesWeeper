package com.msmikeescom.minesweeper.view.activity

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import com.msmikeescom.minesweeper.R
import com.msmikeescom.minesweeper.view.custom.NonSwipeableViewPager


class TabbedActivity : AppCompatActivity() {

    private lateinit var viewPager: NonSwipeableViewPager
    private lateinit var sectionsPagerAdapter : SectionsPagerAdapter
    private lateinit var tabs : TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tabbed)

        sectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        viewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        tabs = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
        viewPager.setCurrentItem(0, true)

        val imageView1 = ImageView(this)
        imageView1.setImageResource(R.drawable.settings_icon)
        imageView1.setPadding(0,0,0,40)
        tabs.getTabAt(0)?.customView = imageView1

        val imageView2 = ImageView(this)
        imageView2.setImageResource(R.drawable.game_pad_icon)
        imageView2.setPadding(0,0,0,40)
        tabs.getTabAt(1)?.customView = imageView2

        val imageView3 = ImageView(this)
        imageView3.setImageResource(R.drawable.profile_icon)
        imageView3.setPadding(0,0,0,40)
        tabs.getTabAt(2)?.customView = imageView3
    }
}