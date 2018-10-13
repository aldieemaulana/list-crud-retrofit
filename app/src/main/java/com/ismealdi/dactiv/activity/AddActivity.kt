package com.ismealdi.dactiv.activity

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import com.ismealdi.dactiv.R
import com.ismealdi.dactiv.api.Contacts
import com.ismealdi.dactiv.base.AmActivity
import com.ismealdi.dactiv.model.request.Contact
import com.ismealdi.dactiv.util.Constants
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_contact_add.*
import kotlinx.android.synthetic.main.toolbar_primary.*

class AddActivity : AmActivity() {

    private val mApiContacts by lazy { Contacts.init() }

    private fun initToolbar() {
        setTitle(getString(R.string.add_contact))
        buttonBackToolbar.visibility = View.VISIBLE

        buttonBackToolbar.setOnClickListener {
            onBackPressed()
        }
    }

    private fun listener() {
        buttonSave.setOnClickListener {
            validateSave()
        }
    }

    private fun init() {
        initData()
        initToolbar()
        listener()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_add)

        init()
    }

    private fun validateSave() {
        val age = if(text_age.text.toString().isNotEmpty()) text_age.text.toString().toInt() else 0
        val firstName = text_first_name.text.toString()
        val lastName = text_last_name.text.toString()

        if(firstName.isNotEmpty() && lastName.isNotEmpty() && age > 0) {
            storeSave(firstName, lastName, age)
        }else{
            showSnackBar(layoutParent, "Please fill in all data", Snackbar.LENGTH_SHORT)
        }
    }

    private fun storeSave(firstName: String, lastName: String, age: Int) {

        val contact = Contact(firstName, lastName, " ", age)

        showProgress()

        disposable = mApiContacts.add(contact).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe (
                        { result ->
                            hideProgress()
                            if (result?.message == "contact saved") {
                                setResult(Constants.INTENT.SUCCESS)
                                finish()
                            }else{
                                showSnackBar(layoutParent, "Unknown Failure", Snackbar.LENGTH_LONG, 850)
                            }
                        },
                        { error ->
                            hideProgress()
                            showSnackBar(layoutParent, error.message.toString(), Snackbar.LENGTH_LONG, 850)
                        }
                )
    }

}
