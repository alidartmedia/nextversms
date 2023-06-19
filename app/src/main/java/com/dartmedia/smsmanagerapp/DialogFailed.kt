package com.dartmedia.smsmanagerapp

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper

class DialogFailed(private val mActivity: Activity) {
    private var dialog: AlertDialog? = null

    fun showDialog() {
        if (dialog?.isShowing == true) return

        val inflater = mActivity.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_failed, null)

        val builder = AlertDialog.Builder(mActivity)
        builder.setView(dialogView)

        dialog = builder.create()
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.show()

        Handler(Looper.getMainLooper()).postDelayed({
            if (dialog?.isShowing == true) {
                dialog?.dismiss()
            }
        }, 3500)
    }
}