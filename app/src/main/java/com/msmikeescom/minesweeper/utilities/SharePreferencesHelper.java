package com.msmikeescom.minesweeper.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import com.msmikeescom.minesweeper.GlobalApplication;

import static com.msmikeescom.minesweeper.utilities.Constants.EASY_LEVEL_NUMBER_MINES;
import static com.msmikeescom.minesweeper.utilities.Constants.MINESWEEPER_PREFERENCES;
import static com.msmikeescom.minesweeper.utilities.Constants.MINE_FILED_DEFAULT_SIZE_H;
import static com.msmikeescom.minesweeper.utilities.Constants.MINE_FILED_DEFAULT_SIZE_W;
import static com.msmikeescom.minesweeper.utilities.Constants.SP_DIFFICULTY;
import static com.msmikeescom.minesweeper.utilities.Constants.SP_MINE_SIZE_H;
import static com.msmikeescom.minesweeper.utilities.Constants.SP_MINE_SIZE_W;

public class SharePreferencesHelper {

    private final SharedPreferences sharedPreferences;
    private static SharePreferencesHelper INSTANCE;

    private SharePreferencesHelper() {
        sharedPreferences = GlobalApplication.getAppContext().getSharedPreferences(MINESWEEPER_PREFERENCES, AppCompatActivity.MODE_PRIVATE);
    }

    public static synchronized SharePreferencesHelper getInstance() {
        if (INSTANCE == null) {
            return new SharePreferencesHelper();
        }
        return INSTANCE;
    }

    public void putMineFieldSizeW(int mineSizeW) {
        sharedPreferences.edit().putInt(SP_MINE_SIZE_W, mineSizeW).apply();
    }

    public int getMineFieldSizeW() {
        return sharedPreferences.getInt(SP_MINE_SIZE_W, MINE_FILED_DEFAULT_SIZE_W);
    }

    public void putMineFieldSizeH(int mineSizeW) {
        sharedPreferences.edit().putInt(SP_MINE_SIZE_H, mineSizeW).apply();
    }

    public int getMineFieldSizeH() {
        return sharedPreferences.getInt(SP_MINE_SIZE_H, MINE_FILED_DEFAULT_SIZE_H);
    }

    public void putDifficulty(int difficulty) {
        sharedPreferences.edit().putInt(SP_DIFFICULTY, difficulty).apply();
    }

    public int getDifficulty() {
        return sharedPreferences.getInt(SP_DIFFICULTY, EASY_LEVEL_NUMBER_MINES);
    }
}
