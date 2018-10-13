package com.ismealdi.dactiv.activity

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.view.View
import com.ismealdi.dactiv.R
import com.ismealdi.dactiv.base.AmActivity
import com.ismealdi.dactiv.util.RevealAnimation
import kotlinx.android.synthetic.main.activity_splash.*


/**
 * Created by Al on 10/10/2018
 */

class SplashActivity : AmActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        initData()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        handler(1500)
    }

    private fun handler(duration: Long) {
        val mHandler = Handler()
        val mRunnable = Runnable {
            startRevealActivity(layoutParent)
        }

        mHandler.postDelayed(mRunnable, duration)
    }

    private fun startRevealActivity(v: View) {
        val bounds = Rect()
        v.getDrawingRect(bounds)

        val intent = Intent(context, MainActivity::class.java)

        intent.putExtra(RevealAnimation.EXTRA_CIRCULAR_REVEAL_X, bounds.centerX())
        intent.putExtra(RevealAnimation.EXTRA_CIRCULAR_REVEAL_Y, bounds.centerY())

        ActivityCompat.startActivity(context, intent, null)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }

}