package com.msmikeescom.minesweeper.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.msmikeescom.minesweeper.R
import com.msmikeescom.minesweeper.view.custom.CustomDialog
import com.msmikeescom.minesweeper.view.fragment.MineFieldFragment
import com.msmikeescom.minesweeper.view.fragment.ProfileFragment
import com.msmikeescom.minesweeper.view.fragment.SettingsFragment

class MainActivity : AppCompatActivity() {

    lateinit var bottomNavigationView : BottomNavigationView
    private var currentFragment: Fragment = SettingsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val settingsFragment = SettingsFragment()
        val mineFieldFragment = MineFieldFragment()
        val profileFragment = ProfileFragment()

        setCurrentFragment(settingsFragment)

        bottomNavigationView = findViewById(R.id.bottom_navigation_view)
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.settings_menu -> {
                    if (currentFragment is MineFieldFragment) {
                        showAlertDialog(settingsFragment)
                        false
                    } else {
                        setCurrentFragment(settingsFragment)
                        true
                    }
                }
                R.id.mine_field_menu -> {
                    setCurrentFragment(mineFieldFragment)
                    true
                }
                R.id.profile_menu -> {
                    if (currentFragment is MineFieldFragment) {
                        showAlertDialog(profileFragment)
                        false
                    } else {
                        setCurrentFragment(profileFragment)
                        true
                    }
                }
                else -> false
            }
        }
    }

    private fun showAlertDialog(fragment: Fragment) {
        val customDialog = CustomDialog()
        customDialog.setOnClickListener(object : CustomDialog.OnClickListener {
            override fun onOkClick() {
                setCurrentFragment(fragment)
                bottomNavigationView.selectedItemId =
                        if (fragment is SettingsFragment) {
                            R.id.settings_menu
                        } else {
                            R.id.profile_menu
                        }
            }
        })
        customDialog.show(supportFragmentManager, fragment.tag)
    }

    private fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_fragment, fragment)
            commit()
        }
        currentFragment = fragment
    }
}