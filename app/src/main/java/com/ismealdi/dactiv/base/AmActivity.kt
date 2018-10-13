package com.ismealdi.dactiv.base

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.graphics.Typeface
import android.os.Handler
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import com.ismealdi.dactiv.R
import com.ismealdi.dactiv.util.Dialogs
import com.kaopiz.kprogresshud.KProgressHUD
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.toolbar_primary.*


/**
 * Created by Al on 10/10/2018
 */

@SuppressLint("Registered")
open class AmActivity : AppCompatActivity() {

    protected val context : Context = this
    protected var disposable : Disposable? = null

    private var progress: KProgressHUD? = null

    internal fun showProgress() {
        progress!!.show()
    }

    internal fun hideProgress() {
        progress!!.dismiss()
    }

    internal fun initData() {
        progress = Dialogs().initProgressDialog(context)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    internal fun setTitle(title: String) {
        if(textTitleToolbar.text != title) {
            val fadeOutAnimation = ObjectAnimator.ofFloat(textTitleToolbar, View.ALPHA, 1.0f, 0.0f)
            val fadeInAnimation = ObjectAnimator.ofFloat(textTitleToolbar, View.ALPHA, 0.0f, 1.0f)

            fadeOutAnimation.duration = 500
            fadeOutAnimation.start()

            textTitleToolbar.text = title

            fadeInAnimation.duration = 500
            fadeInAnimation.start()
        }
    }

    internal fun showSnackBar(view: CoordinatorLayout, message: String, duration: Int, delay: Long = 0) {
        val mSnackBar = Snackbar.make(view, message, duration)

        mSnackBar.view.setBackgroundColor(context.resources.getColor(R.color.colorPrimaryDark))

        val textView = mSnackBar.view.findViewById(android.support.design.R.id.snackbar_text) as TextView
        val font = Typeface.createFromAsset(applicationContext.assets, "fonts/Montserrat-R.ttf")

        textView.setTypeface(font, Typeface.NORMAL)
        textView.setTextColor(context.resources.getColor(R.color.colorWhite))
        textView.gravity = Gravity.CENTER
        textView.textSize = 13f
        textView.maxLines = 5

        Handler().postDelayed({
            mSnackBar.show()
        }, delay)
    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }


}