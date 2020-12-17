package com.msmikeescom.minesweeper.view.fragment

import android.os.Bundle
import android.os.CountDownTimer
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.msmikeescom.minesweeper.R
import com.msmikeescom.minesweeper.model.FieldObject
import com.msmikeescom.minesweeper.utilities.Constants.EASY_LEVEL_NUMBER_MINES
import com.msmikeescom.minesweeper.utilities.Constants.EIGHT
import com.msmikeescom.minesweeper.utilities.Constants.EMPTY
import com.msmikeescom.minesweeper.utilities.Constants.FIVE
import com.msmikeescom.minesweeper.utilities.Constants.FOUR
import com.msmikeescom.minesweeper.utilities.Constants.HARD_LEVEL_NUMBER_MINES
import com.msmikeescom.minesweeper.utilities.Constants.MEDIUM_LEVEL_NUMBER_MINES
import com.msmikeescom.minesweeper.utilities.Constants.MINE
import com.msmikeescom.minesweeper.utilities.Constants.ONE
import com.msmikeescom.minesweeper.utilities.Constants.SEVEN
import com.msmikeescom.minesweeper.utilities.Constants.SIX
import com.msmikeescom.minesweeper.utilities.Constants.THREE
import com.msmikeescom.minesweeper.utilities.Constants.TWO
import com.msmikeescom.minesweeper.viewmodel.TabbedViewModel
import java.time.LocalTime
import java.util.*


class MineFieldFragment : Fragment() {

    private var mMineFiled: GridLayout? = null
    private var mNew: ImageView? = null
    private var mProfileImage: ImageView? = null
    private var mProfileName: TextView? = null
    private var mCountDownTimer: CountDownTimer? = null
    private var mTimerStarted = false
    private var mDefaultNumberOfMines = 0
    private var mNumberOfMines = 0
    private var mMinesFound = 0
    private var mChronometerTime = 0
    private var dpHeight = 0
    private var dpWidth = 0
    private var horizontalSize = 0
    private var verticalSize = 0
    private var mFieldObjects = Array(horizontalSize) { arrayOfNulls<FieldObject>(verticalSize) }

    private lateinit var viewModel: TabbedViewModel

    private var difficulty = 10

    companion object {
        private const val TAG = "MineFieldFragment"

        fun getInstance(position: Int): Fragment {
            val bundle = Bundle()
            bundle.putInt("pos", position)
            val tabFragment = MineFieldFragment()
            tabFragment.arguments = bundle
            return tabFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        viewModel = ViewModelProvider(this).get(TabbedViewModel::class.java)
        viewModel.getSavedMineFiledSizes().observe(viewLifecycleOwner, { savedMineFiledSizes ->
            if (false) { // TODO: Handle to fit screen
                val displayMetrics: DisplayMetrics = resources.displayMetrics
                dpHeight = (displayMetrics.heightPixels / displayMetrics.density).toInt() - 220
                dpWidth = (displayMetrics.widthPixels / displayMetrics.density).toInt() - 10
                verticalSize = (dpHeight / 30)
                horizontalSize = (dpWidth / 30)
            } else {
                verticalSize = savedMineFiledSizes.first.toInt()
                horizontalSize = savedMineFiledSizes.second.toInt()
            }

            mFieldObjects = Array(horizontalSize) { arrayOfNulls(verticalSize) }

            viewModel.getSavedDifficultyLevel().observe(viewLifecycleOwner, { difficultyLevel ->
                difficulty = difficultyLevel.toInt()
                handleSignInResult()
            })
        })

        return inflater.inflate(R.layout.fragment_mine_field, container, false)
    }

    private fun handleSignInResult() {
        initData()
        initFieldObjectsArray()
        buildMineFiled()
        initView()
        initProfile()
        initMineField()
    }

    private fun initData() {
        mDefaultNumberOfMines = difficulty
        mNumberOfMines = mDefaultNumberOfMines
    }

    private fun initFieldObjectsArray() {
        for (j in 0 until verticalSize) {
            for (i in 0 until horizontalSize) {
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
            xMinePos = rand.nextInt(horizontalSize)
            yMinePos = rand.nextInt(verticalSize)
            if (mFieldObjects[xMinePos][yMinePos]!!.squareImageToShow != MINE) {
                mFieldObjects[xMinePos][yMinePos] = FieldObject(null, MINE)
                i++
                Log.d(TAG, "Mine set up at: [$xMinePos, $yMinePos]")
            }
        }
        setUpFieldNumbers()
    }

    private fun initView() {
        mProfileImage = view?.findViewById(R.id.profile_image)
        mProfileName = view?.findViewById(R.id.profile_name)
        mMineFiled = view?.findViewById(R.id.mine_field)
        mNew = view?.findViewById(R.id.new_icon)
        mNew?.setOnClickListener { requireActivity().recreate() }
        updateCounter(mNumberOfMines)
    }

    private fun initProfile() {
        viewModel.getProfile().observe(viewLifecycleOwner, {
            mProfileName?.text = it.profileName
            Glide.with(this).load(it.photoUrl).into(mProfileImage!!);
        })
    }

    private fun initMineField() {
        mMineFiled!!.columnCount = horizontalSize
        mMineFiled!!.orientation = GridLayout.HORIZONTAL
        for (j in 0 until verticalSize) {
            for (i in 0 until horizontalSize) {
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

    private fun setUpFieldNumbers() {
        for (j in 0 until verticalSize) {
            for (i in 0 until horizontalSize) {
                if (mFieldObjects[i][j]!!.squareImageToShow == MINE) {
                    if (i - 1 >= 0 && j - 1 >= 0 && mFieldObjects[i - 1][j - 1]!!.squareImageToShow != MINE) { //Start Top position
                        mFieldObjects[i - 1][j - 1]!!.squareImageToShow = mFieldObjects[i - 1][j - 1]!!.squareImageToShow + 1
                    }
                    if (i - 1 >= 0 && mFieldObjects[i - 1][j]!!.squareImageToShow != MINE) { //Start position
                        mFieldObjects[i - 1][j]!!.squareImageToShow = mFieldObjects[i - 1][j]!!.squareImageToShow + 1
                    }
                    if (i - 1 >= 0 && j + 1 < verticalSize && mFieldObjects[i - 1][j + 1]!!.squareImageToShow != MINE) { //Start Bottom position
                        mFieldObjects[i - 1][j + 1]!!.squareImageToShow = mFieldObjects[i - 1][j + 1]!!.squareImageToShow + 1
                    }
                    if (j - 1 >= 0 && mFieldObjects[i][j - 1]!!.squareImageToShow != MINE) { //Top position
                        mFieldObjects[i][j - 1]!!.squareImageToShow = mFieldObjects[i][j - 1]!!.squareImageToShow + 1
                    }
                    if (i + 1 < horizontalSize && j - 1 >= 0 && mFieldObjects[i + 1][j - 1]!!.squareImageToShow != MINE) { //Top End position
                        mFieldObjects[i + 1][j - 1]!!.squareImageToShow = mFieldObjects[i + 1][j - 1]!!.squareImageToShow + 1
                    }
                    if (i + 1 < horizontalSize && mFieldObjects[i + 1][j]!!.squareImageToShow != MINE) { //End position
                        mFieldObjects[i + 1][j]!!.squareImageToShow = mFieldObjects[i + 1][j]!!.squareImageToShow + 1
                    }
                    if (i + 1 < horizontalSize && j + 1 < verticalSize && mFieldObjects[i + 1][j + 1]!!.squareImageToShow != MINE) { //End Bottom position
                        mFieldObjects[i + 1][j + 1]!!.squareImageToShow = mFieldObjects[i + 1][j + 1]!!.squareImageToShow + 1
                    }
                    if (j + 1 < verticalSize && mFieldObjects[i][j + 1]!!.squareImageToShow != MINE) { //Bottom position
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
        if (xPos - 1 >= 0 && yPos + 1 < verticalSize && mFieldObjects[xPos - 1][yPos + 1]!!.squareImageToShow != MINE &&
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
        if (xPos + 1 < horizontalSize && yPos - 1 >= 0 && mFieldObjects[xPos + 1][yPos - 1]!!.squareImageToShow != MINE &&
                mFieldObjects[xPos + 1][yPos - 1]!!.isCovered) { //Top End position
            unCoverSquare(xPos + 1, yPos - 1)
            if (!isNumberOrMineSquare(mFieldObjects[xPos + 1][yPos - 1]!!.squareImageToShow)) {
                unCoverEmptySquares(xPos + 1, yPos - 1)
            }
        }
        if (xPos + 1 < horizontalSize && mFieldObjects[xPos + 1][yPos]!!.squareImageToShow != MINE &&
                mFieldObjects[xPos + 1][yPos]!!.isCovered) { //End position
            unCoverSquare(xPos + 1, yPos)
            if (!isNumberOrMineSquare(mFieldObjects[xPos + 1][yPos]!!.squareImageToShow)) {
                unCoverEmptySquares(xPos + 1, yPos)
            }
        }
        if (xPos + 1 < horizontalSize && yPos + 1 < verticalSize && mFieldObjects[xPos + 1][yPos + 1]!!.squareImageToShow != MINE &&
                mFieldObjects[xPos + 1][yPos + 1]!!.isCovered) { //End Bottom position
            unCoverSquare(xPos + 1, yPos + 1)
            if (!isNumberOrMineSquare(mFieldObjects[xPos + 1][yPos + 1]!!.squareImageToShow)) {
                unCoverEmptySquares(xPos + 1, yPos + 1)
            }
        }
        if (yPos + 1 < verticalSize && mFieldObjects[xPos][yPos + 1]!!.squareImageToShow != MINE &&
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
        (mFieldObjects[x][y]!!.squareView?.findViewById<View>(R.id.image_button) as ImageView)
                .setImageDrawable(ResourcesCompat.getDrawable(resources, getResourceId(x, y), null))
    }

    private fun getResourceId(x: Int, y: Int): Int {
        var resourceId = R.drawable.uncovered
        resourceId = when (mFieldObjects[x][y]!!.squareImageToShow) {
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

    private fun setOnClickListener(xPos: Int, yPos: Int, fieldObject: FieldObject?, imageView: ImageView, resourceId: Int) {
        fieldObject!!.squareView?.setOnClickListener(View.OnClickListener {
            if (resourceId == R.drawable.uncovered) {
                //setFaceImage(FaceType.SCARED, true)
                unCoverEmptySquares(xPos, yPos)
            } else if (resourceId == R.drawable.mine) {
                //setFaceImage(FaceType.KILLED, false)
                uncoverAllSquares()
                stopTimer()
                return@OnClickListener
            }
            imageView.setImageDrawable(ResourcesCompat.getDrawable(resources, resourceId, null))
            if (!mTimerStarted) {
                startTimer()
            }
        })
    }

    private fun setOnLongClickListener(fieldObject: FieldObject?, imageView: ImageView) {
        fieldObject!!.squareView?.setOnLongClickListener(View.OnLongClickListener {
            if (fieldObject.isFlagged) {
                Log.i(TAG, "Square un flagged")
                //setFaceImage(FaceType.ANGRY, true)
                mNumberOfMines++
                imageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.covered, null))
                fieldObject.isFlagged = false
                updateCounter(mNumberOfMines)
                return@OnLongClickListener true
            }
            if (fieldObject.isCovered) {
                Log.i(TAG, "Square flagged")
                //setFaceImage(FaceType.SCARED, true)
                mNumberOfMines--
                if (mNumberOfMines >= 0) {
                    if (fieldObject.squareImageToShow == MINE) {
                        mMinesFound++
                    }
                    if (mMinesFound == mDefaultNumberOfMines) {
                        Log.i(TAG, "You won!$mMinesFound")
                        //setFaceImage(FaceType.HAPPY, false)
                        showFinishedGamePopupWindowClick(mMineFiled!!.rootView)
                        stopTimer()
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
        for (j in 0 until verticalSize) {
            for (i in 0 until horizontalSize) {
                unCoverSquare(i, j)
                mFieldObjects[i][j]!!.squareView?.setOnClickListener(null)
                mFieldObjects[i][j]!!.squareView?.setOnLongClickListener(null)
            }
        }
        mMineFiled!!.isClickable = false
    }

    private fun startTimer() {
        mTimerStarted = true
        val tensMinutesImageView = view?.findViewById<ImageView>(R.id.tens_minutes)
        val unitsMinutesImageView = view?.findViewById<ImageView>(R.id.units_minutes)
        val tensSecondsImageView = view?.findViewById<ImageView>(R.id.tens_seconds)
        val unitsSecondsImageView = view?.findViewById<ImageView>(R.id.units_seconds)
        mCountDownTimer = object : CountDownTimer(3600000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                Log.i(TAG, "millisUntilFinished: $millisUntilFinished")
                Log.i(TAG, "millisSinceStarted: " + (3600000 - millisUntilFinished))
                val millisSinceStarted = 3600000 - millisUntilFinished
                val secondsSinceStarted = (millisSinceStarted - millisSinceStarted % 1000) / 1000
                var seconds = 0
                var minutes = 0
                mChronometerTime = secondsSinceStarted.toInt()
                Log.i(TAG, "Time: $mChronometerTime")
                minutes = mChronometerTime / 60
                seconds = mChronometerTime - minutes * 60
                if (mChronometerTime % 60 != 0) {
                    val units = seconds % 10
                    seconds /= 10
                    val tens = seconds % 10
                    unitsSecondsImageView?.let { setImageNumber(it, units) }
                    tensSecondsImageView?.let { setImageNumber(it, tens) }
                } else {
                    val units = minutes % 10
                    minutes /= 10
                    val tens = minutes % 10
                    unitsMinutesImageView?.let { setImageNumber(it, units) }
                    tensMinutesImageView?.let { setImageNumber(it, tens) }
                    unitsSecondsImageView?.let { setImageNumber(it, 0) }
                    tensSecondsImageView?.let { setImageNumber(it, 0) }
                }
            }

            override fun onFinish() {
                unitsMinutesImageView?.let { setImageNumber(it, 0) }
                tensMinutesImageView?.let { setImageNumber(it, 0) }
                unitsSecondsImageView?.let { setImageNumber(it, 0) }
                tensSecondsImageView?.let { setImageNumber(it, 0) }
            }
        }
        mCountDownTimer?.start()
    }

    private fun stopTimer() {
        if (mCountDownTimer != null) {
            mCountDownTimer!!.cancel()
        }
    }

    private fun updateCounter(number: Int) {
        var number = number
        val tensImageView = view?.findViewById<ImageView>(R.id.counter_tens)
        val unitsImageView = view?.findViewById<ImageView>(R.id.counter_units)
        val units = number % 10
        number /= 10
        val tens = number % 10
        number /= 10
        val hundreds = number % 10
        unitsImageView?.let { setImageNumber(it, units) }
        tensImageView?.let { setImageNumber(it, tens) }
    }

    private fun setImageNumber(imageView: ImageView, digit: Int) {
        when (digit) {
            0 -> imageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.digit_0, null))
            1 -> imageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.digit_1, null))
            2 -> imageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.digit_2, null))
            3 -> imageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.digit_3, null))
            4 -> imageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.digit_4, null))
            5 -> imageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.digit_5, null))
            6 -> imageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.digit_6, null))
            7 -> imageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.digit_7, null))
            8 -> imageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.digit_8, null))
            9 -> imageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.digit_9, null))
        }
    }

    private fun dismissSettingsPopupWindow(popupWindow: PopupWindow, difficulty: Int, updateDifficulty: Boolean) {
        var toastText = ""
        popupWindow.dismiss()
        if (updateDifficulty) {
            this.difficulty = difficulty
            requireActivity().recreate()
            when (difficulty) {
                EASY_LEVEL_NUMBER_MINES -> toastText = "Easy level"
                MEDIUM_LEVEL_NUMBER_MINES -> toastText = "Medium level"
                HARD_LEVEL_NUMBER_MINES -> toastText = "Hard level"
            }
            Toast.makeText(requireContext(), toastText, Toast.LENGTH_LONG).show()
        }
    }

    private fun showSettingsPopupWindowClick(view: View?) {
        val inflater = requireContext().getSystemService(AppCompatActivity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
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
        val inflater = requireContext().getSystemService(AppCompatActivity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.popup_windows_finished_game_layout, null)
        val playAgain = popupView.findViewById<Button>(R.id.play_again)
        val finishTime = popupView.findViewById<TextView>(R.id.finish_time)
        val width = LinearLayout.LayoutParams.MATCH_PARENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val popupWindow = PopupWindow(popupView, width, height, true)
        popupWindow.elevation = 5.0f
        val timeOfDay = LocalTime.ofSecondOfDay(mChronometerTime.toLong())
        val time = timeOfDay.toString()
        finishTime.text = resources.getString(R.string.your_time_was, time)
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)
        playAgain.setOnClickListener { dismissSettingsPopupWindow(popupWindow, difficulty, true) }
        popupView.setOnTouchListener { _, _ ->
            dismissSettingsPopupWindow(popupWindow, difficulty, true)
            true
        }
    }

}