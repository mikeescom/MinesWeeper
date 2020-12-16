package com.msmikeescom.minesweeper.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.msmikeescom.minesweeper.model.Repository
import com.msmikeescom.minesweeper.model.SettingsObject
import com.msmikeescom.minesweeper.utilities.SharePreferencesHelper


class TabbedViewModel : ViewModel() {

    private var repository : Repository = Repository()
    private val userId = SharePreferencesHelper.getInstance().currentUserId

    init {
        repository.callSettingsDataObjects()
        repository.callSavedSettingsFromUser(userId)
    }

    fun saveSettingsFromUser (mineFiledSize : Int, difficultyLevel : Int) {
        repository.saveSettingsFromUser(userId, mineFiledSize, difficultyLevel)
    }

    fun getSettingsDataObject() : MutableLiveData<SettingsObject> {
        return repository.getSettingsDataObject()
    }

    fun getSettingsSavedSettingsFromUser () : MutableLiveData<Map<String, Int>>{
        return repository.getSavedSettingsFromUser()
    }
}