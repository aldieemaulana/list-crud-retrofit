package com.ismealdi.dactiv.model

import java.io.Serializable


/**
 * Created by Al on 13/10/2018
 */

data class Contact(
    val id: String?,
    val firstName: String?,
    val lastName: String?,
    val age: Int?,
    val photo: String?
) : Serializable 