package com.msmikeescom.minesweeper.model

import android.view.View

class FieldObject(var squareView: View?, squareImageToShow: Int) {
    var squareImageToShow = 0
    var isCovered = true
    var isFlagged = false

    init {
        this.squareImageToShow = squareImageToShow
    }
}