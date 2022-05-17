package com.msmikeescom.minesweeper.ui.activity

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.msmikeescom.minesweeper.R
import com.msmikeescom.minesweeper.data.FieldObject
import com.msmikeescom.minesweeper.ui.view.MineFieldChronometer
import java.util.*

class MineFieldActivity : AppCompatActivity() {
    private enum class FaceType {
        ANGRY, HAPPY, KILLED, SCARED, SMILE
    }

    private var chronometer: MineFieldChronometer? = null

    private var sharedPreferences: SharedPreferences? = null
    private var mMineFiled: GridLayout? = null
    private var mFace: ImageView? = null
    private var mSettings: ImageView? = null
    private var mDefaultNumberOfMines = 0
    private var mNumberOfMines = 0
    private var mMinesFound = 0
    private val mFieldObjects = Array(HORIZONTAL_SIZE) { arrayOfNulls<FieldObject>(VERTICAL_SIZE) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mine_field)
        initData()
        initFieldObjectsArray()
        buildMineFiled()
        initView()
        initMineField()
    }

    private fun initData() {
        sharedPreferences = getSharedPreferences(MINESWEEPER_PREFERENCES, MODE_PRIVATE)
        mDefaultNumberOfMines = difficulty
        mNumberOfMines = mDefaultNumberOfMines
    }

    private fun initView() {
        chronometer = findViewById(R.id.chronometer)
        mMineFiled = findViewById(R.id.mine_field)
        mFace = findViewById(R.id.face)
        mFace?.setOnClickListener { recreate() }
        mSettings = findViewById(R.id.settings)
        mSettings?.setOnClickListener { showSettingsPopupWindowClick(mMineFiled?.rootView) }
        updateCounter(mNumberOfMines)
    }

    private fun initFieldObjectsArray() {
        for (j in 0 until VERTICAL_SIZE) {
            for (i in 0 until HORIZONTAL_SIZE) {
                mFieldObjects[i][j] = FieldObject(null, EMPTY)
            }
        }
    }

    private fun buildMineFiled() {
        val rand = Random()
        var xMinePos: Int
        var yMinePos: Int
        var i = 0
        while (i < mNumberOfMines) {
            xMinePos = rand.nextInt(HORIZONTAL_SIZE)
            yMinePos = rand.nextInt(VERTICAL_SIZE)
            if (mFieldObjects[xMinePos][yMinePos]!!.squareImageToShow != MINE) {
                mFieldObjects[xMinePos][yMinePos] = FieldObject(null, MINE)
                i++
                Log.d(TAG, "Mine set up at: [$xMinePos, $yMinePos]")
            }
        }
        setUpFieldNumbers()
    }

    private fun setUpFieldNumbers() {
        for (j in 0 until VERTICAL_SIZE) {
            for (i in 0 until HORIZONTAL_SIZE) {
                if (mFieldObjects[i][j]!!.squareImageToShow == MINE) {
                    if (i - 1 >= 0 && j - 1 >= 0 && mFieldObjects[i - 1][j - 1]!!.squareImageToShow != MINE) { //Start Top position
                        mFieldObjects[i - 1][j - 1]!!.squareImageToShow = mFieldObjects[i - 1][j - 1]!!.squareImageToShow + 1
                    }
                    if (i - 1 >= 0 && mFieldObjects[i - 1][j]!!.squareImageToShow != MINE) { //Start position
                        mFieldObjects[i - 1][j]!!.squareImageToShow = mFieldObjects[i - 1][j]!!.squareImageToShow + 1
                    }
                    if (i - 1 >= 0 && j + 1 < VERTICAL_SIZE && mFieldObjects[i - 1][j + 1]!!.squareImageToShow != MINE) { //Start Bottom position
                        mFieldObjects[i - 1][j + 1]!!.squareImageToShow = mFieldObjects[i - 1][j + 1]!!.squareImageToShow + 1
                    }
                    if (j - 1 >= 0 && mFieldObjects[i][j - 1]!!.squareImageToShow != MINE) { //Top position
                        mFieldObjects[i][j - 1]!!.squareImageToShow = mFieldObjects[i][j - 1]!!.squareImageToShow + 1
                    }
                    if (i + 1 < HORIZONTAL_SIZE && j - 1 >= 0 && mFieldObjects[i + 1][j - 1]!!.squareImageToShow != MINE) { //Top End position
                        mFieldObjects[i + 1][j - 1]!!.squareImageToShow = mFieldObjects[i + 1][j - 1]!!.squareImageToShow + 1
                    }
                    if (i + 1 < HORIZONTAL_SIZE && mFieldObjects[i + 1][j]!!.squareImageToShow != MINE) { //End position
                        mFieldObjects[i + 1][j]!!.squareImageToShow = mFieldObjects[i + 1][j]!!.squareImageToShow + 1
                    }
                    if (i + 1 < HORIZONTAL_SIZE && j + 1 < VERTICAL_SIZE && mFieldObjects[i + 1][j + 1]!!.squareImageToShow != MINE) { //End Bottom position
                        mFieldObjects[i + 1][j + 1]!!.squareImageToShow = mFieldObjects[i + 1][j + 1]!!.squareImageToShow + 1
                    }
                    if (j + 1 < VERTICAL_SIZE && mFieldObjects[i][j + 1]!!.squareImageToShow != MINE) { //Bottom position
                        mFieldObjects[i][j + 1]!!.squareImageToShow = mFieldObjects[i][j + 1]!!.squareImageToShow + 1
                    }
                }
            }
        }
    }

    private fun unCoverEmptySquares(xPos: Int, yPos: Int) {
        if (xPos - 1 >= 0 && yPos - 1 >= 0 && mFieldObjects[xPos - 1][yPos - 1]!!.squareImageToShow != MINE &&
                mFieldObjects[xPos - 1][yPos - 1]!!.isCovered) { //Start Top position
            unCoverSquare(xPos - 1, yPos - 1)
            if (!isNumberOrMineSquare(mFieldObjects[xPos - 1][yPos - 1]!!.squareImageToShow)) {
                unCoverEmptySquares(xPos - 1, yPos - 1)
            }
        }
        if (xPos - 1 >= 0 && mFieldObjects[xPos - 1][yPos]!!.squareImageToShow != MINE &&
                mFieldObjects[xPos - 1][yPos]!!.isCovered) { //Start position
            unCoverSquare(xPos - 1, yPos)
            if (!isNumberOrMineSquare(mFieldObjects[xPos - 1][yPos]!!.squareImageToShow)) {
                unCoverEmptySquares(xPos - 1, yPos)
            }
        }
        if (xPos - 1 >= 0 && yPos + 1 < VERTICAL_SIZE && mFieldObjects[xPos - 1][yPos + 1]!!.squareImageToShow != MINE &&
                mFieldObjects[xPos - 1][yPos + 1]!!.isCovered) { //Start Bottom position
            unCoverSquare(xPos - 1, yPos + 1)
            if (!isNumberOrMineSquare(mFieldObjects[xPos - 1][yPos + 1]!!.squareImageToShow)) {
                unCoverEmptySquares(xPos - 1, yPos + 1)
            }
        }
        if (yPos - 1 >= 0 && mFieldObjects[xPos][yPos - 1]!!.squareImageToShow != MINE &&
                mFieldObjects[xPos][yPos - 1]!!.isCovered) { //Top position
            unCoverSquare(xPos, yPos - 1)
            if (!isNumberOrMineSquare(mFieldObjects[xPos][yPos - 1]!!.squareImageToShow)) {
                unCoverEmptySquares(xPos, yPos - 1)
            }
        }
        if (xPos + 1 < HORIZONTAL_SIZE && yPos - 1 >= 0 && mFieldObjects[xPos + 1][yPos - 1]!!.squareImageToShow != MINE &&
                mFieldObjects[xPos + 1][yPos - 1]!!.isCovered) { //Top End position
            unCoverSquare(xPos + 1, yPos - 1)
            if (!isNumberOrMineSquare(mFieldObjects[xPos + 1][yPos - 1]!!.squareImageToShow)) {
                unCoverEmptySquares(xPos + 1, yPos - 1)
            }
        }
        if (xPos + 1 < HORIZONTAL_SIZE && mFieldObjects[xPos + 1][yPos]!!.squareImageToShow != MINE &&
                mFieldObjects[xPos + 1][yPos]!!.isCovered) { //End position
            unCoverSquare(xPos + 1, yPos)
            if (!isNumberOrMineSquare(mFieldObjects[xPos + 1][yPos]!!.squareImageToShow)) {
                unCoverEmptySquares(xPos + 1, yPos)
            }
        }
        if (xPos + 1 < HORIZONTAL_SIZE && yPos + 1 < VERTICAL_SIZE && mFieldObjects[xPos + 1][yPos + 1]!!.squareImageToShow != MINE &&
                mFieldObjects[xPos + 1][yPos + 1]!!.isCovered) { //End Bottom position
            unCoverSquare(xPos + 1, yPos + 1)
            if (!isNumberOrMineSquare(mFieldObjects[xPos + 1][yPos + 1]!!.squareImageToShow)) {
                unCoverEmptySquares(xPos + 1, yPos + 1)
            }
        }
        if (yPos + 1 < VERTICAL_SIZE && mFieldObjects[xPos][yPos + 1]!!.squareImageToShow != MINE &&
                mFieldObjects[xPos][yPos + 1]!!.isCovered) { //Bottom position
            unCoverSquare(xPos, yPos + 1)
            if (!isNumberOrMineSquare(mFieldObjects[xPos][yPos + 1]!!.squareImageToShow)) {
                unCoverEmptySquares(xPos, yPos + 1)
            }
        }
    }

    private fun isNumberOrMineSquare(imageToShow: Int): Boolean {
        return when (imageToShow) {
            MINE -> true
            ONE -> true
            TWO -> true
            THREE -> true
            FOUR -> true
            FIVE -> true
            SIX -> true
            SEVEN -> true
            EIGHT -> true
            else -> false
        }
    }

    private fun unCoverSquare(x: Int, y: Int) {
        mFieldObjects[x][y]!!.isCovered = false
        (mFieldObjects[x][y]!!.squareView?.findViewById<View>(R.id.image_button) as ImageView).setImageDrawable(ResourcesCompat.getDrawable(resources, getResourceId(x, y), null))
    }

    private fun getResourceId(x: Int, y: Int): Int {
        val resourceId = when (mFieldObjects[x][y]!!.squareImageToShow) {
            MINE -> R.drawable.mine
            ONE -> R.drawable.one
            TWO -> R.drawable.two
            THREE -> R.drawable.three
            FOUR -> R.drawable.four
            FIVE -> R.drawable.five
            SIX -> R.drawable.six
            SEVEN -> R.drawable.seven
            EIGHT -> R.drawable.eight
            else -> R.drawable.uncovered
        }
        return resourceId
    }

    private fun initMineField() {
        mMineFiled!!.columnCount = HORIZONTAL_SIZE
        mMineFiled!!.orientation = GridLayout.HORIZONTAL
        for (j in 0 until VERTICAL_SIZE) {
            for (i in 0 until HORIZONTAL_SIZE) {
                val squareView = layoutInflater.inflate(R.layout.square_layout, null)
                val imageView = squareView.findViewById<ImageView>(R.id.image_button)
                imageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.covered, null))
                mFieldObjects[i][j]!!.squareView = squareView
                mMineFiled!!.addView(squareView)
                setOnClickListener(i, j, mFieldObjects[i][j], imageView, getResourceId(i, j))
                setOnLongClickListener(mFieldObjects[i][j], imageView)
            }
        }
    }

    private fun setOnClickListener(xPos: Int, yPos: Int, fieldObject: FieldObject?, imageView: ImageView, resourceId: Int) {
        fieldObject!!.squareView?.setOnClickListener(View.OnClickListener {
            if (resourceId == R.drawable.uncovered) {
                setFaceImage(FaceType.SCARED, true)
                unCoverEmptySquares(xPos, yPos)
            } else if (resourceId == R.drawable.mine) {
                setFaceImage(FaceType.KILLED, false)
                uncoverAllSquares()
                chronometer?.stopTimer()
                return@OnClickListener
            }
            imageView.setImageDrawable(ResourcesCompat.getDrawable(resources, resourceId, null))
            if (chronometer?.mTimerStarted == false) {
                chronometer?.startTimer()
            }
        })
    }

    private fun setOnLongClickListener(fieldObject: FieldObject?, imageView: ImageView) {
        fieldObject!!.squareView?.setOnLongClickListener(OnLongClickListener {
            if (fieldObject.isFlagged) {
                Log.i(TAG, "Square un flagged")
                setFaceImage(FaceType.ANGRY, true)
                mNumberOfMines++
                imageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.covered, null))
                fieldObject.isFlagged = false
                updateCounter(mNumberOfMines)
                return@OnLongClickListener true
            }
            if (fieldObject.isCovered) {
                Log.i(TAG, "Square flagged")
                setFaceImage(FaceType.SCARED, true)
                mNumberOfMines--
                if (mNumberOfMines >= 0) {
                    if (fieldObject.squareImageToShow == MINE) {
                        mMinesFound++
                    }
                    if (mMinesFound == mDefaultNumberOfMines) {
                        Log.i(TAG, "You won!$mMinesFound")
                        setFaceImage(FaceType.HAPPY, false)
                        showFinishedGamePopupWindowClick(mMineFiled!!.rootView)
                        chronometer?.stopTimer()
                        return@OnLongClickListener true
                    }
                    imageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.flaged, null))
                    fieldObject.isFlagged = true
                    updateCounter(mNumberOfMines)
                }
            }
            true
        })
    }

    private fun uncoverAllSquares() {
        for (j in 0 until VERTICAL_SIZE) {
            for (i in 0 until HORIZONTAL_SIZE) {
                unCoverSquare(i, j)
                mFieldObjects[i][j]!!.squareView?.setOnClickListener(null)
                mFieldObjects[i][j]!!.squareView?.setOnLongClickListener(null)
            }
        }
        mMineFiled!!.isClickable = false
    }

    private fun updateCounter(number: Int) {
        var numberTemp = number
        val hundredsImageView = findViewById<ImageView>(R.id.counter_hundreds)
        val tensImageView = findViewById<ImageView>(R.id.counter_tens)
        val unitsImageView = findViewById<ImageView>(R.id.counter_units)
        val units = numberTemp % 10
        numberTemp /= 10
        val tens = numberTemp % 10
        numberTemp /= 10
        val hundreds = numberTemp % 10
        setImageNumber(unitsImageView, units)
        setImageNumber(tensImageView, tens)
        setImageNumber(hundredsImageView, hundreds)
    }

    private fun setImageNumber(imageView: ImageView, digit: Int) {
        when (digit) {
            0 -> imageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.zero_digit, null))
            1 -> imageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.one_digit, null))
            2 -> imageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.two_digit, null))
            3 -> imageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.three_digit, null))
            4 -> imageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.four_digit, null))
            5 -> imageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.five_digit, null))
            6 -> imageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.six_digit, null))
            7 -> imageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.seven_digit, null))
            8 -> imageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.eight_digit, null))
            9 -> imageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.nine_digit, null))
        }
    }

    private fun setFaceImage(faceType: FaceType, keepSmiling: Boolean) {
        val resource: Int = when (faceType) {
            FaceType.ANGRY -> R.drawable.angry
            FaceType.HAPPY -> R.drawable.happy
            FaceType.KILLED -> R.drawable.killed
            FaceType.SCARED -> R.drawable.scared
            FaceType.SMILE -> R.drawable.smile
        }
        mFace!!.setImageDrawable(ResourcesCompat.getDrawable(resources, resource, null))
        if (keepSmiling) {
            Handler().postDelayed({ mFace!!.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.smile, null)) }, 500)
        }
    }

    private var difficulty: Int
        get() = sharedPreferences!!.getInt(SP_DIFFICULTY, EASY_LEVEL_NUMBER_MINES)
        private set(difficulty) {
            val editor = sharedPreferences!!.edit()
            editor.putInt(SP_DIFFICULTY, difficulty)
            editor.apply()
        }

    private fun dismissSettingsPopupWindow(popupWindow: PopupWindow, difficulty: Int, updateDifficulty: Boolean) {
        var toastText = ""
        popupWindow.dismiss()
        if (updateDifficulty) {
            this.difficulty = difficulty
            recreate()
            when (difficulty) {
                EASY_LEVEL_NUMBER_MINES -> toastText = "Easy level"
                MEDIUM_LEVEL_NUMBER_MINES -> toastText = "Medium level"
                HARD_LEVEL_NUMBER_MINES -> toastText = "Hard level"
            }
            Toast.makeText(this, toastText, Toast.LENGTH_LONG).show()
        }
    }

    private fun showSettingsPopupWindowClick(view: View?) {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.popup_windows_settings_layout, null)
        val easy = popupView.findViewById<ImageView>(R.id.easy)
        val medium = popupView.findViewById<ImageView>(R.id.medium)
        val difficult = popupView.findViewById<ImageView>(R.id.difficult)
        val cancel = popupView.findViewById<Button>(R.id.cancel)
        val width = LinearLayout.LayoutParams.MATCH_PARENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val popupWindow = PopupWindow(popupView, width, height, true)
        popupWindow.elevation = 5.0f
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)
        easy.setOnClickListener { dismissSettingsPopupWindow(popupWindow, EASY_LEVEL_NUMBER_MINES, true) }
        medium.setOnClickListener { dismissSettingsPopupWindow(popupWindow, MEDIUM_LEVEL_NUMBER_MINES, true) }
        difficult.setOnClickListener { dismissSettingsPopupWindow(popupWindow, HARD_LEVEL_NUMBER_MINES, true) }
        cancel.setOnClickListener { dismissSettingsPopupWindow(popupWindow, EASY_LEVEL_NUMBER_MINES, false) }
        popupView.setOnTouchListener { _, _ ->
            dismissSettingsPopupWindow(popupWindow, EASY_LEVEL_NUMBER_MINES, false)
            true
        }
    }

    private fun showFinishedGamePopupWindowClick(view: View?) {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.popup_windows_finished_game_layout, null)
        val playAgain = popupView.findViewById<Button>(R.id.play_again)
        val finishTime = popupView.findViewById<TextView>(R.id.finish_time)
        val width = LinearLayout.LayoutParams.MATCH_PARENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val popupWindow = PopupWindow(popupView, width, height, true)
        popupWindow.elevation = 5.0f
        val timeOfDay = chronometer?.mChronometerTime?.toLong() ?: { 0 }
        val time = timeOfDay.toString()
        finishTime.text = resources.getString(R.string.your_time_was, time)
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)
        playAgain.setOnClickListener { dismissSettingsPopupWindow(popupWindow, difficulty, true) }
        popupView.setOnTouchListener { _, _ ->
            dismissSettingsPopupWindow(popupWindow, difficulty, true)
            true
        }
    }

    companion object {
        private const val TAG = "MineFieldActivity"
        private const val MINESWEEPER_PREFERENCES = "MINESWEEPER_PREFERENCES"
        private const val SP_DIFFICULTY = "SP_DIFFICULTY"
        private const val EASY_LEVEL_NUMBER_MINES = 36
        private const val MEDIUM_LEVEL_NUMBER_MINES = 51
        private const val HARD_LEVEL_NUMBER_MINES = 66
        private const val HORIZONTAL_SIZE = 12
        private const val VERTICAL_SIZE = 20
        private const val EMPTY = 0
        private const val MINE = 9
        private const val ONE = 1
        private const val TWO = 2
        private const val THREE = 3
        private const val FOUR = 4
        private const val FIVE = 5
        private const val SIX = 6
        private const val SEVEN = 7
        private const val EIGHT = 8
    }
}