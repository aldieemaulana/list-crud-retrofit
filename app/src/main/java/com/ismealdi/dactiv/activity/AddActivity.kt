package com.ismealdi.dactiv.activity

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import com.ismealdi.dactiv.R
import com.ismealdi.dactiv.api.Contacts
import com.ismealdi.dactiv.model.Contact as ModelContact
import com.ismealdi.dactiv.base.AmActivity
import com.ismealdi.dactiv.model.request.Contact
import com.ismealdi.dactiv.util.Constants
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_contact_add.*
import kotlinx.android.synthetic.main.toolbar_primary.*

class AddActivity : AmActivity() {

    private val mApiContacts by lazy { Contacts.init() }
    private var data : ModelContact? = null

    private fun initToolbar() {
        setTitle(if(intent.getStringExtra(Constants.INTENT.CONTACT.ID).isNullOrEmpty()) getString(R.string.add_contact) else getString(R.string.edit_contact))
        buttonBackToolbar.visibility = View.VISIBLE

        buttonSave.text = if(intent.getStringExtra(Constants.INTENT.CONTACT.ID).isNullOrEmpty()) getString(R.string.save) else getString(R.string.update
        )
        buttonBackToolbar.setOnClickListener {
            onBackPressed()
        }
    }

    private fun listener() {
        buttonSave.setOnClickListener {
            validateSave()
        }
    }

    private fun initForm() {
        data = intent.getSerializableExtra(Constants.INTENT.CONTACT.DATA) as ModelContact
        text_age.setText(data!!.age.toString())
        text_first_name.setText(data!!.firstName)
        text_last_name.setText(data!!.lastName)
    }


    private fun init() {
        initData()
        initToolbar()
        listener()

        if(!intent.getStringExtra(Constants.INTENT.CONTACT.ID).isNullOrEmpty())
            initForm()
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
            if(intent.getStringExtra(Constants.INTENT.CONTACT.ID).isNullOrEmpty()) storeSave(firstName, lastName, age)
            else storeUpdate(firstName, lastName, age)
        }else{
            showSnackBar(layoutParent, "Please fill in all data", Snackbar.LENGTH_SHORT)
        }
    }

    private fun storeUpdate(firstName: String, lastName: String, age: Int) {
        val contact = Contact(firstName, lastName, data?.photo!!, age)

        showProgress()

        disposable = mApiContacts.edit(data!!.id!!, contact).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe (
                        { result ->
                            hideProgress()
                            if (result?.data != null) {
                                val mIntent = Intent(context, MainActivity::class.java)
                                mIntent.putExtra(Constants.INTENT.CONTACT.DATA, result?.data)
                                mIntent.putExtra(Constants.INTENT.CONTACT.POSITION, intent.getIntExtra(Constants.INTENT.CONTACT.POSITION, 0))
                                setResult(Constants.INTENT.SUCCESS, mIntent)
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
