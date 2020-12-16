package com.msmikeescom.minesweeper.utilities;

import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import com.msmikeescom.minesweeper.GlobalApplication;

import static com.msmikeescom.minesweeper.utilities.Constants.MINESWEEPER_PREFERENCES;
import static com.msmikeescom.minesweeper.utilities.Constants.SP_USER_ID;

public class SharePreferencesHelper {

    private final SharedPreferences sharedPreferences;
    private static SharePreferencesHelper INSTANCE;

    private SharePreferencesHelper() {
        sharedPreferences = GlobalApplication.getAppContext().getSharedPreferences(MINESWEEPER_PREFERENCES, AppCompatActivity.MODE_PRIVATE);
    }

    public static synchronized SharePreferencesHelper getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SharePreferencesHelper();
        }
        return INSTANCE;
    }

    public void putCurrentUserId(String id) {
        sharedPreferences.edit().putString(SP_USER_ID, id).apply();
    }

    public String getCurrentUserId() {
        return sharedPreferences.getString(SP_USER_ID, "");
    }
}
