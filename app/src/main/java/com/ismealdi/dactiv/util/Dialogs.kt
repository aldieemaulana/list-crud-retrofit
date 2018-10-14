package com.ismealdi.dactiv.util

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import com.ismealdi.dactiv.R
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.dialog_no_internet.view.*

class Dialogs {

    fun initProgressDialog(context: Context): KProgressHUD {
        return KProgressHUD.create(context)
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setCancellable(false)
            .setDimAmount(0.1f)
            .setCornerRadius(4f)
            .setSize(45,45)
            .setWindowColor(context.resources.getColor(R.color.colorWhite))
            .setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
            .setAnimationSpeed(2)
    }


    internal fun dialogNoInternet(context: Context, actionListener: View.OnClickListener? = null) : Dialog {
        val dialog = Dialog(context)
        val layoutInflaterDialog = LayoutInflater.from(context)
        val dialogView = layoutInflaterDialog.inflate(R.layout.dialog_no_internet, null)

        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(dialogView)
        dialog.setCanceledOnTouchOutside(false)

        dialogView.buttonRetry.setOnClickListener(actionListener)

        if(!dialog.isShowing) {
            dialog.show()
        }

        return dialog
    }

}
