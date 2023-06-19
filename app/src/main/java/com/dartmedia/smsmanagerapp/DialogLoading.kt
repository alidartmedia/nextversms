package com.dartmedia.smsmanagerapp

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable

class DialogLoading(private val mActivity: Activity) {
    private var dialog: AlertDialog? = null

    fun showLoading() {
        if (dialog?.isShowing == true) return

        val inflater = mActivity.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_loading, null)

        val builder = AlertDialog.Builder(mActivity)
        builder.setView(dialogView)

        dialog = builder.create()
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.show()
    }

    fun hideLoading() {
        dialog?.dismiss()
    }
}