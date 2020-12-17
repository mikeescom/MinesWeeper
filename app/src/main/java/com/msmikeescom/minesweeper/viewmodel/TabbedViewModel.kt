package com.msmikeescom.minesweeper.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.msmikeescom.minesweeper.model.ProfileObject
import com.msmikeescom.minesweeper.model.Repository
import com.msmikeescom.minesweeper.model.SettingsObject
import com.msmikeescom.minesweeper.utilities.SharePreferencesHelper


class TabbedViewModel : ViewModel() {

    private var repository : Repository = Repository()
    private val userId = SharePreferencesHelper.getInstance().currentUserId

    init {
        repository.callProfile(userId)
        repository.callSettingsDataObjects()
        repository.callSavedSettingsFromUser(userId)
        repository.callSavedMineFiledSizes(userId)
        repository.callSavedDifficultyLevel(userId)
    }

    fun getProfile() : MutableLiveData<ProfileObject> {
        return repository.getProfile()
    }

    fun saveSettingsFromUser (mineFiledSize : Int, difficultyLevel : Int) {
        repository.saveSettingsFromUser(userId, mineFiledSize, difficultyLevel)
    }

    fun getSettingsDataObject() : MutableLiveData<SettingsObject> {
        return repository.getSettingsDataObject()
    }

    fun getSettingsSavedSettingsFromUser () : MutableLiveData<Map<String, Long>>{
        return repository.getSavedSettingsFromUser()
    }

    fun getSavedMineFiledSizes () : MutableLiveData<Pair<Long, Long>> {
        return repository.getSavedMineFiledSizes()
    }

    fun getSavedDifficultyLevel () : MutableLiveData<Long> {
        return repository.getSavedDifficultyLevel()
    }
}