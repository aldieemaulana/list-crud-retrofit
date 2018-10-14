package com.ismealdi.dactiv.activity

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.ismealdi.dactiv.R
import com.ismealdi.dactiv.adapter.AlphabetAdapter
import com.ismealdi.dactiv.adapter.ContactAdapter
import com.ismealdi.dactiv.base.AmActivity
import com.ismealdi.dactiv.model.Contact
import com.ismealdi.dactiv.util.CircleTransform
import com.ismealdi.dactiv.util.Constants
import com.ismealdi.dactiv.util.Logs
import com.ismealdi.dactiv.util.RevealAnimation
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_contact.view.*
import com.ismealdi.dactiv.api.Contacts as API


/**
 * Created by Al on 10/10/2018
 */

class MainActivity : AmActivity() {

    private lateinit var mAdapterAlphabet : AlphabetAdapter
    private lateinit var dialog : Dialog
    private lateinit var layoutInflaterDialog : LayoutInflater
    private lateinit var dialogView : View

    private var mAdapter : ContactAdapter? = null
    private val mApiContacts by lazy { API.init() }

    private fun initList(data: MutableList<Contact>) {
        if(mAdapter == null) {
            mAdapter = ContactAdapter(data, context)
            recyclerView.layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)
            recyclerView.adapter = mAdapter

        }else{
            mAdapter!!.addAll(data)
        }

    }

    private fun initAlphabet() {
        val data : MutableList<String> = mutableListOf()

        var ab = 'A'
        while (ab <= 'Z') {
            data.add(ab.toString())
            ++ab
        }

        mAdapterAlphabet = AlphabetAdapter(data.toList(), context)

        recyclerViewCalendar.layoutManager = LinearLayoutManager(context,
                LinearLayout.HORIZONTAL, false)
        recyclerViewCalendar.adapter = mAdapterAlphabet
    }

    private fun listener() {
        buttonAdd.setOnClickListener {
            startActivityForResult(Intent(context, AddActivity::class.java), Constants.INTENT.CONTACT.ADD)
        }
    }

    private fun init() {
        initData()
        RevealAnimation(layoutParent, intent, context as Activity)
        getContact()
        initAlphabet()
        listener()

        dialog = Dialog(context, R.style.AppTheme_Dialog)
        layoutInflaterDialog = LayoutInflater.from(context)
        dialogView = layoutInflaterDialog.inflate(R.layout.dialog_contact, null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
    }

    internal fun getContact() {
        showProgress()

        disposable = mApiContacts.get().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (
                { result ->
                    if (result?.data != null) {
                        val contacts = result.data as MutableList<Contact>

                        contacts.sortBy { contact -> contact.firstName?.toUpperCase() }

                        initList(contacts)
                    }

                    hideProgress()
                },
                { error ->
                    showSnackBar(layoutParent, error.message.toString(), Snackbar.LENGTH_INDEFINITE, 850, getString(R.string.retry), View.OnClickListener { getContact() })
                }
            )
    }

    internal fun showDetail(position: Int, contact: Contact) {

        dialogView.textName.text = "${contact.firstName} ${contact.lastName}"
        dialogView.textAge.text = "${contact.age} y.o"

        if(contact.photo!!.isNotBlank()) {
            Picasso.get().load(contact.photo)
                    .transform(CircleTransform())
                    .placeholder(R.drawable.label_waiting)
                    .into(dialogView.imagePhoto)
        }

        dialogView.buttonDelete.setOnClickListener {
            dialogView.layoutConfirmDelete.visibility = View.VISIBLE
            dialogView.buttonDelete.visibility = View.GONE
        }

        dialogView.buttonCancelDelete.setOnClickListener {
            dialogView.layoutConfirmDelete.visibility = View.GONE
            dialogView.buttonDelete.visibility = View.VISIBLE
            dialog.dismiss()
        }

        dialogView.buttonConfirmDelete.setOnClickListener {
            deleteContact(contact.id, position)
        }

        dialog.setOnCancelListener {
            dialogView.buttonCancelDelete.performClick()
        }

        dialogView.buttonEdit.setOnClickListener {
            dialog.dismiss()

            val mIntent = Intent(context, AddActivity::class.java)
            mIntent.putExtra(Constants.INTENT.CONTACT.ID, contact.id)
            mIntent.putExtra(Constants.INTENT.CONTACT.DATA, contact)
            mIntent.putExtra(Constants.INTENT.CONTACT.POSITION, position)

            startActivityForResult(mIntent , Constants.INTENT.CONTACT.EDIT)
        }

        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(dialogView)
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()
    }

    private fun deleteContact(id: String?, position: Int) {

        if(id!!.isNotEmpty()) {
            dialogView.buttonCancelDelete.performClick()

            showProgress()
            disposable = mApiContacts.delete(id).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                    { result ->
                        Logs.e("${result.message}")
                        mAdapter!!.delete(position)

                        hideProgress()
                    },
                    { error ->
                        hideProgress()

                        dialog.show()

                        showSnackBar(dialogView.layoutParentDialog, error.message.toString(), Snackbar.LENGTH_SHORT, 500)
                    }
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode) {
            Constants.INTENT.CONTACT.ADD -> {
                if (resultCode == Constants.INTENT.SUCCESS) {
                    getContact()
                }
            }
            Constants.INTENT.CONTACT.EDIT -> {
                if (resultCode == Constants.INTENT.SUCCESS) {
                    val contact = data!!.getSerializableExtra(Constants.INTENT.CONTACT.DATA) as Contact
                    mAdapter!!.update(contact, data!!.getIntExtra(Constants.INTENT.CONTACT.POSITION, 0))
                }
            }
        }
    }

}
