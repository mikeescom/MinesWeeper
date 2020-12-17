package com.msmikeescom.minesweeper.model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Repository {

    private val db = Firebase.firestore
    private val mineFieldList : ArrayList<MineFieldSizeObject> = ArrayList()
    private val difficultyLevelList : ArrayList<DifficultyLevelObject> = ArrayList()

    private val profileLiveData : MutableLiveData<ProfileObject> = MutableLiveData()
    private val settingsLiveDataObject : MutableLiveData<SettingsObject> = MutableLiveData()
    private val savedSettingsFromUserLiveDataObject : MutableLiveData<Map<String, Long>> = MutableLiveData()
    private val mineFieldSizeLiveData : MutableLiveData<Pair<Long, Long>> = MutableLiveData()
    private val difficultyLevelLiveData : MutableLiveData<Long> = MutableLiveData()

    companion object {
        val TAG : String = Repository::class.java.simpleName
    }

    fun callProfile(id: String?) {
        db.collection("users").document(id!!).get()
                .addOnSuccessListener { document ->
                    if (!document.data.isNullOrEmpty()) {
                        profileLiveData.postValue(ProfileObject(document.data!!["displayName"] as String
                                , document.data!!["photoUrl"] as String))
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with ", exception)
                }
    }

    fun getProfile() : MutableLiveData<ProfileObject> {
        return profileLiveData
    }

    fun callSettingsDataObjects() {
        db.collection("mineFieldSizes").get()
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
                "mineFiledSize" to 0L,
                "difficultyLevel" to 0L
        )

        db.collection("users").document(id!!).get()
                .addOnSuccessListener { document ->
                    if (document.data!!["mineFiledSize"]?.toString().isNullOrEmpty()) {
                        db.collection("users").document(id)
                                .update(lastSavedSettings)
                                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                                .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
                    } else {
                        lastSavedSettings = mapOf(
                                "mineFiledSize" to document.data!!["mineFiledSize"] as Long,
                                "difficultyLevel" to document.data!!["difficultyLevel"] as Long
                        )
                    }

                    savedSettingsFromUserLiveDataObject.postValue(lastSavedSettings)
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with ", exception)
                }
    }

    fun getSavedSettingsFromUser () : MutableLiveData<Map<String, Long>> {
        return savedSettingsFromUserLiveDataObject
    }

    fun callSavedMineFiledSizes (id: String?) {
        db.collection("users").document(id!!).get()
                .addOnSuccessListener { document ->
                    val mineFiledSize = document.data!!["mineFiledSize"]
                    if (mineFiledSize?.toString().isNullOrEmpty()) {

                    } else {
                        db.collection("mineFieldSizes").get()
                                .addOnSuccessListener { result ->
                                    for (doc in result) {
                                        if (mineFiledSize == doc.data["id"]) {
                                            mineFieldSizeLiveData.postValue(Pair(doc.data["width"] as Long, doc.data["height"] as Long))
                                            break
                                        }
                                    }
                                }
                                .addOnFailureListener { exception ->
                                    Log.d(TAG, "get failed with ", exception)
                                }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with ", exception)
                }
    }

    fun getSavedMineFiledSizes () : MutableLiveData<Pair<Long, Long>> {
        return mineFieldSizeLiveData
    }

    fun callSavedDifficultyLevel (id: String?) {
        db.collection("users").document(id!!).get()
                .addOnSuccessListener { document ->
                    val difficultyLevel = document.data!!["difficultyLevel"]
                    if (difficultyLevel?.toString().isNullOrEmpty()) {

                    } else {
                        db.collection("difficultyLevels").get()
                                .addOnSuccessListener { result ->
                                    for (doc in result) {
                                        if (difficultyLevel == doc.data["id"]) {
                                            difficultyLevelLiveData.postValue(doc.data["value"] as Long)
                                            break
                                        }
                                    }
                                }
                                .addOnFailureListener { exception ->
                                    Log.d(TAG, "get failed with ", exception)
                                }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with ", exception)
                }
    }

    fun getSavedDifficultyLevel () : MutableLiveData<Long> {
        return difficultyLevelLiveData
    }
}