package com.msmikeescom.minesweeper.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.msmikeescom.minesweeper.R
import com.msmikeescom.minesweeper.view.fragment.MineFieldFragment
import com.msmikeescom.minesweeper.view.fragment.ProfileFragment
import com.msmikeescom.minesweeper.view.fragment.SettingsFragment

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView : BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val settingsFragment = SettingsFragment()
        val mineFieldFragment = MineFieldFragment()
        val profileFragment = ProfileFragment()

        setCurrentFragment(settingsFragment)

        bottomNavigationView = findViewById(R.id.bottom_navigation_view)
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.settings -> setCurrentFragment(settingsFragment)
                R.id.mine_field -> setCurrentFragment(mineFieldFragment)
                R.id.profile -> setCurrentFragment(profileFragment)

            }
            true
        }
    }

    private fun setCurrentFragment(fragment: Fragment)=
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.flFragment,fragment)
                commit()
            }
}