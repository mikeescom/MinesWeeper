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
    private val savedSettingsFromUserLiveDataObject : MutableLiveData<Map<String, Int>> = MutableLiveData()

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

    fun saveUserData(displayName: String?, email: String?, photoUrl: String?, id: String?) {
        val user = hashMapOf(
                "displayName" to displayName,
                "email" to email,
                "photoUrl" to photoUrl,
                "idToken" to id,
        )

        db.collection("users").document(id!!).get()
                .addOnSuccessListener { document ->
                    if (document.data.isNullOrEmpty()) {
                        db.collection("users").document(id)
                                .set(user)
                                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                                .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with ", exception)
                }
    }

    fun saveSettingsFromUser (id: String?, mineFiledSize : Int, difficultyLevel : Int) {
        val savedSettings = mapOf(
                "mineFiledSize" to mineFiledSize,
                "difficultyLevel" to difficultyLevel
        )

        db.collection("users").document(id!!)
                .update(savedSettings)
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
    }

    fun callSavedSettingsFromUser (id: String?) {
        var lastSavedSettings = mapOf(
                "mineFiledSize" to 0,
                "difficultyLevel" to 0
        )

        db.collection("users").document(id!!).get()
                .addOnSuccessListener { document ->
                    if (document.data!!["lastSavedSettings"]?.toString().isNullOrEmpty()) {
                        db.collection("users").document(id)
                                .update(lastSavedSettings)
                                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                                .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
                    } else {
                        lastSavedSettings = document.data?.get("lastSavedSettings") as HashMap<String, Int>
                    }

                    savedSettingsFromUserLiveDataObject.postValue(lastSavedSettings)
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with ", exception)
                }
    }

    fun getSavedSettingsFromUser () : MutableLiveData<Map<String, Int>> {
        return savedSettingsFromUserLiveDataObject
    }
}