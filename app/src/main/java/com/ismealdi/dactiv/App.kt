package com.ismealdi.dactiv

import android.app.Application

/**
 * Created by Al on 10/10/2018
 */

class App : Application() {

    override fun onCreate() {
        super.onCreate()
    }

    companion object {
        const val API = "https://simple-contact-crud.herokuapp.com/"
    }

}