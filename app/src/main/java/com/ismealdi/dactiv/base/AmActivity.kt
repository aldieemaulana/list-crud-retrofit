package com.ismealdi.dactiv.base

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentFilter
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
import com.ismealdi.dactiv.interfaces.AmConnectionInterface
import com.ismealdi.dactiv.util.ConnectionReceiver
import com.ismealdi.dactiv.util.Dialogs
import com.ismealdi.dactiv.util.NoSwipeBehavior
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
    private var connectionReceiver : ConnectionReceiver? = null

    internal fun showProgress() {
        progress!!.show()
    }

    internal fun hideProgress() {
        if(progress!!.isShowing)
            progress!!.dismiss()
    }

    internal fun initData(amConnectionInterface: AmConnectionInterface? = null) {
        progress = Dialogs().initProgressDialog(context)

        if(amConnectionInterface != null) {
            connectionReceiver = ConnectionReceiver()
            connectionReceiver!!.registerReceiver(amConnectionInterface)

            val mIntentFilter = IntentFilter()
            mIntentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE")

            registerReceiver(connectionReceiver, mIntentFilter)
        }

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

    internal fun showSnackBar(view: CoordinatorLayout, message: String, duration: Int, delay: Long = 0, actionText: String = "", actionListener: View.OnClickListener? = null) {
        val mSnackBar = Snackbar.make(view, message, duration)

        if(message.contains("Unable to resolve") || message.contains(getString(R.string.text_no_internet))) mSnackBar.behavior = NoSwipeBehavior()

        mSnackBar.view.setBackgroundColor(context.resources.getColor(R.color.colorPrimaryDark))

        val textViewAction = mSnackBar.view.findViewById(android.support.design.R.id.snackbar_action) as TextView
        val textView = mSnackBar.view.findViewById(android.support.design.R.id.snackbar_text) as TextView

        textView.setTypeface(Typeface.createFromAsset(applicationContext.assets, "fonts/Montserrat-R.ttf"), Typeface.NORMAL)
        textView.setTextColor(context.resources.getColor(R.color.colorWhite))
        textView.gravity = Gravity.CENTER
        textView.textSize = 13f
        textView.maxLines = 1

        if(actionText != "") {

            textViewAction.setTypeface(Typeface.createFromAsset(applicationContext.assets, "fonts/Montserrat-B.ttf"), Typeface.NORMAL)
            textViewAction.setTextColor(context.resources.getColor(R.color.colorWhite))
            textViewAction.gravity = Gravity.CENTER
            textViewAction.setBackgroundDrawable(context.resources.getDrawable(R.drawable.button_primary_flat_snackbar))
            textViewAction.textSize = 13f

            mSnackBar.setAction(actionText, actionListener)
        }


        Handler().postDelayed({
            hideProgress()
            mSnackBar.show()
        }, delay)
    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }

    override fun onDestroy() {
        super.onDestroy()

        if(connectionReceiver != null)
            unregisterReceiver(connectionReceiver)
    }

}