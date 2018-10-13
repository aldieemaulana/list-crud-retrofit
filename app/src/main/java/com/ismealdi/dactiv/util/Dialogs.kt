package com.ismealdi.dactiv.util

import android.content.Context
import com.ismealdi.dactiv.R
import com.kaopiz.kprogresshud.KProgressHUD

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

}
