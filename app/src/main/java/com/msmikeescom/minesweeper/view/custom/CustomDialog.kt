package com.msmikeescom.minesweeper.view.custom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.msmikeescom.minesweeper.R

class CustomDialog: DialogFragment() {

    private lateinit var listener : OnClickListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog!!.window?.setBackgroundDrawableResource(R.drawable.dialog_round_corner);
        return inflater.inflate(R.layout.custom_dialog, container, false)
    }

    override fun onStart() {
        super.onStart()
        val okButton = dialog!!.findViewById<ImageView>(R.id.ok_button)
        okButton.setOnClickListener {
            dismiss()
            listener.onOkClick()
        }
        val cancelButton = dialog!!.findViewById<ImageView>(R.id.cancel_button)
        cancelButton.setOnClickListener {
            dismiss()
        }
    }

    fun setOnClickListener(listener : OnClickListener) {
        this.listener = listener
    }

    interface OnClickListener {
        fun onOkClick()
    }
}