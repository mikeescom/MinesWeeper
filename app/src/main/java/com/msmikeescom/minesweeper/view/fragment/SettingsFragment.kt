package com.msmikeescom.minesweeper.view.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.msmikeescom.minesweeper.R
import com.msmikeescom.minesweeper.model.DifficultyLevelObject
import com.msmikeescom.minesweeper.model.MineFieldSizeObject
import com.msmikeescom.minesweeper.model.SettingsObject
import com.msmikeescom.minesweeper.utilities.Constants.RC_SIGN_OUT
import com.msmikeescom.minesweeper.viewmodel.TabbedViewModel


class SettingsFragment : Fragment() {

    private lateinit var fieldSizeRadioGroup : RadioGroup
    private lateinit var difficultyLevelRadioGroup : RadioGroup

    private lateinit var viewModel: TabbedViewModel
    
    private lateinit var applyButton : ImageView
    private lateinit var exitButton : ImageView

    companion object {
        fun getInstance(position: Int): Fragment {
            val bundle = Bundle()
            bundle.putInt("pos", position)
            val tabFragment = SettingsFragment()
            tabFragment.arguments = bundle
            return tabFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        viewModel = ViewModelProvider(this).get(TabbedViewModel::class.java)

        viewModel.getSettingsDataObject().observe(viewLifecycleOwner, { settingsDataObjects ->
            updateUI(settingsDataObjects)
        })

        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    private fun updateUI(settingsDataObjects: SettingsObject) {
        val mineFieldSizes = settingsDataObjects.mineFieldSizeObjects.sortedBy { it.id }
        val difficultyLevels = settingsDataObjects.difficultyLevelObjects.sortedBy { it.id }

        setUpApplyButton()
        setUpMineFieldSize(mineFieldSizes)
        setUpDifficultyLevel(difficultyLevels)

        viewModel.getSettingsSavedSettingsFromUser().observe(viewLifecycleOwner, { savedSettingFromUser ->
            setUpSavedSettingsFromUser(savedSettingFromUser)
        })
    }

    private fun setUpSavedSettingsFromUser(savedSettingsFromUser: Map<String, Long>) {
        savedSettingsFromUser.forEach {
            if (it.key == "mineFiledSize") {
                fieldSizeRadioGroup.check(it.value.toInt())
            } else if (it.key == "difficultyLevel") {
                difficultyLevelRadioGroup.check(it.value.toInt())
            }
        }
    }

    private fun setUpApplyButton () {
        applyButton = view?.findViewById(R.id.apply_button)!!
        applyButton.setOnClickListener {
            saveSettings()
            // TODO
        }

        exitButton = view?.findViewById(R.id.exit_button)!!
        exitButton.setOnClickListener {
            requireActivity().setResult(RC_SIGN_OUT)
            requireActivity().finish()
        }
    }

    private fun setUpMineFieldSize(mineFieldSizes: List<MineFieldSizeObject>) {
        fieldSizeRadioGroup = view?.findViewById(R.id.field_size_radio_group)!!

        mineFieldSizes.forEach {
            val radioButton: RadioButton = layoutInflater.inflate(R.layout.settings_radio_button, null) as RadioButton
            radioButton.text = it.label
            radioButton.id = it.id.toInt()
            fieldSizeRadioGroup.addView(radioButton)
        }
    }

    private fun setUpDifficultyLevel(difficultyLevels: List<DifficultyLevelObject>) {
        difficultyLevelRadioGroup = view?.findViewById(R.id.difficulty_level_radio_group)!!

        difficultyLevels.forEach {
            val radioButton: RadioButton = layoutInflater.inflate(R.layout.settings_radio_button, null) as RadioButton
            radioButton.text = it.label
            radioButton.id = it.id.toInt()
            difficultyLevelRadioGroup.addView(radioButton)
        }
    }

    private fun saveSettings() {
        viewModel.saveSettingsFromUser(fieldSizeRadioGroup.checkedRadioButtonId, difficultyLevelRadioGroup.checkedRadioButtonId)
    }
}