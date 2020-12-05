package com.msmikeescom.minesweeper.view.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.viewpager.widget.ViewPager
import com.msmikeescom.minesweeper.R
import com.msmikeescom.minesweeper.utilities.Constants.EASY_LEVEL_NUMBER_MINES
import com.msmikeescom.minesweeper.utilities.Constants.HARD_LEVEL_NUMBER_MINES
import com.msmikeescom.minesweeper.utilities.Constants.MEDIUM_LEVEL_NUMBER_MINES
import com.msmikeescom.minesweeper.utilities.Constants.MINE_FILED_DEFAULT_SIZE_H
import com.msmikeescom.minesweeper.utilities.Constants.MINE_FILED_DEFAULT_SIZE_W
import com.msmikeescom.minesweeper.utilities.SharePreferencesHelper
import com.msmikeescom.minesweeper.utilities.Utilities

class SettingsFragment : Fragment() {

    private lateinit var fieldSizeRadioGroup : RadioGroup
    private lateinit var radioButtonCustom : RadioButton
    private lateinit var customSizeLayout : View
    private lateinit var widthSpinner : Spinner
    private lateinit var heightSpinner : Spinner

    private lateinit var difficultyLevelRadioGroup : RadioGroup
    private lateinit var radioButtonCustomDif : RadioButton
    private lateinit var customDifficultyLayout : View
    private lateinit var numberOfMinesSpinner : Spinner
    
    private lateinit var applyButton : Button

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
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onStart() {
        super.onStart()
        setUpApplyButton()
        setUpMineFieldSize()
        setUpDifficultyLevel()
    }

    private fun setUpApplyButton () {
        applyButton = view?.findViewById(R.id.apply_button)!!
        applyButton.setOnClickListener {
            saveSettings()
            val viewPager = requireActivity().findViewById<ViewPager>(R.id.view_pager)
            viewPager.setCurrentItem(1, true)
        }
    }

    private fun saveSettings() {
        SharePreferencesHelper.getInstance().putDifficulty(getDifficulty())
        val mineSize = getFieldSize()
        SharePreferencesHelper.getInstance().putMineFieldSizeW(mineSize[0])
        SharePreferencesHelper.getInstance().putMineFieldSizeH(mineSize[1])
    }

    private fun getDifficulty() : Int {
        val radioButton = view?.findViewById<RadioButton>(difficultyLevelRadioGroup.checkedRadioButtonId)
        when (radioButton?.text) {
            "Easy" -> return EASY_LEVEL_NUMBER_MINES
            "Medium" -> return MEDIUM_LEVEL_NUMBER_MINES
            "Hard" -> return HARD_LEVEL_NUMBER_MINES
            "Custom difficulty" -> {
                return (widthSpinner.selectedItem as TextView).text as Int
            }
        }
        return EASY_LEVEL_NUMBER_MINES
    }

    private fun getFieldSize() : Array<Int> {
        var w : Int = MINE_FILED_DEFAULT_SIZE_W
        var h : Int = MINE_FILED_DEFAULT_SIZE_H
        val radioButton = view?.findViewById<RadioButton>(fieldSizeRadioGroup.checkedRadioButtonId)
        when (radioButton?.text) {
            "12 x 15" -> {
                w = 12
                h = 15
            }
            "15 x 18" -> {
                w = 15
                h = 18
            }
            "Custom size" -> {
                w = (widthSpinner.selectedItem as TextView).text as Int
                h = (heightSpinner.selectedItem as TextView).text as Int
            }
            "Fit screen size" -> {
                w = 0
                h = 0
            }
        }
        return arrayOf(w,h)
    }

    private fun setUpMineFieldSize () {
        val widths = resources.getStringArray(R.array.Widths)
        val heights = resources.getStringArray(R.array.Heights)

        widthSpinner = view?.findViewById(R.id.width_spinner)!!
        val adapterWidths = ArrayAdapter(requireContext(), R.layout.spinner_item, widths)
        widthSpinner.adapter = adapterWidths

        heightSpinner = view?.findViewById(R.id.height_spinner)!!
        val adapterHeights = ArrayAdapter(requireContext(), R.layout.spinner_item, heights)
        heightSpinner.adapter = adapterHeights

        customSizeLayout = view?.findViewById(R.id.custom_size_layout)!!
        fieldSizeRadioGroup = view?.findViewById(R.id.field_size_radio_group)!!
        fieldSizeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_button_fixed_1 -> Utilities.collapse(customSizeLayout)
                R.id.radio_button_fixed_2 -> Utilities.collapse(customSizeLayout)
                R.id.radio_button_custom -> Utilities.expand(customSizeLayout)
                R.id.radio_button_fit -> Utilities.collapse(customSizeLayout)
            }
        }
        radioButtonCustom = view?.findViewById(R.id.radio_button_custom)!!
    }

    private fun setUpDifficultyLevel () {
        val numberOfMines = resources.getStringArray(R.array.Mines)

        numberOfMinesSpinner = view?.findViewById(R.id.number_of_mines_spinner)!!
        val adapterWidths = ArrayAdapter(requireContext(), R.layout.spinner_item, numberOfMines)
        numberOfMinesSpinner.adapter = adapterWidths

        customDifficultyLayout = view?.findViewById(R.id.custom_difficulty_layout)!!
        difficultyLevelRadioGroup = view?.findViewById(R.id.difficulty_level_radio_group)!!
        difficultyLevelRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_button_easy -> Utilities.collapse(customDifficultyLayout)
                R.id.radio_button_medium -> Utilities.collapse(customDifficultyLayout)
                R.id.radio_button_hard -> Utilities.collapse(customDifficultyLayout)
                R.id.radio_button_custom_dif -> Utilities.expand(customDifficultyLayout)
            }
        }
        radioButtonCustomDif = view?.findViewById(R.id.radio_button_custom_dif)!!
    }

}