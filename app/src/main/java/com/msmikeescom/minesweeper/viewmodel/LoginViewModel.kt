package com.msmikeescom.minesweeper.viewmodel

import androidx.lifecycle.ViewModel
import com.msmikeescom.minesweeper.model.Repository

class LoginViewModel: ViewModel() {

    private var repository : Repository = Repository()

    fun saveUserData(displayName: String?, email: String?, photoUrl: String?, id: String?) {
        repository.saveUserData(displayName, email, photoUrl, id)
    }
}