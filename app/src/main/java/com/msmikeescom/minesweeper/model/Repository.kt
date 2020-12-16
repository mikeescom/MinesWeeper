package com.msmikeescom.minesweeper.model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Repository {

    private val db = Firebase.firestore
    private val mineFieldList : ArrayList<MineFieldSizeObject> = ArrayList()
    private val difficultyLevelList : ArrayList<DifficultyLevelObject> = ArrayList()
    private val settingsLiveDataObject : MutableLiveData<SettingsObject> = MutableLiveData()

    companion object {
        val TAG : String = Repository::class.java.simpleName
    }

    fun callSettingsDataObjects() {
        val mineFieldSizes = db.collection("mineFieldSizes")
        mineFieldSizes.get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        Log.d(TAG, "${document.id} => ${document.data}")
                        mineFieldList.add(MineFieldSizeObject(document.data["id"] as Long
                                , document.data["label"] as String
                                , document.data["width"] as Long
                                , document.data["height"] as Long))
                    }

                    val difficultyLevels = db.collection("difficultyLevels")
                    difficultyLevels.get()
                            .addOnSuccessListener { result ->
                                for (document in result) {
                                    Log.d(TAG, "${document.id} => ${document.data}")
                                    difficultyLevelList.add(DifficultyLevelObject(document.data["id"] as Long
                                            , document.data["label"] as String
                                            , document.data["value"] as Long))
                                }
                                settingsLiveDataObject.postValue(SettingsObject(mineFieldList, difficultyLevelList))

                            }
                            .addOnFailureListener { exception ->
                                Log.d(TAG, "get failed with ", exception)
                            }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with ", exception)
                }
    }

    fun getSettingsDataObject() : MutableLiveData<SettingsObject> {
        return settingsLiveDataObject
    }
}