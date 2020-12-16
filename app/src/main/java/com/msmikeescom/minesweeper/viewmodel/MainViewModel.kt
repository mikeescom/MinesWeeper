package com.msmikeescom.minesweeper.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.msmikeescom.minesweeper.model.Repository
import com.msmikeescom.minesweeper.model.SettingsObject


class MainViewModel : ViewModel() {

    private var repository : Repository = Repository()

    init {
        repository.callSettingsDataObjects()
    }

    fun getSettingsDataObject() : MutableLiveData<SettingsObject> {
        return repository.getSettingsDataObject()
    }
}