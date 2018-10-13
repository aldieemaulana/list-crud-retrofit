package com.ismealdi.dactiv.model.response

import com.ismealdi.dactiv.model.Contact


/**
 * Created by Al on 13/10/2018
 */

open class Contact(
    val message: String?,
    val data: List<Contact?>?
)

open class ContactMessage(
    val message: String?
)