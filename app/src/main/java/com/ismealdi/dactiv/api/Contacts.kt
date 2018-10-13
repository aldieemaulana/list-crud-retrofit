package com.ismealdi.dactiv.api

import com.ismealdi.dactiv.model.response.Contact
import com.ismealdi.dactiv.model.response.ContactEdit
import com.ismealdi.dactiv.model.response.ContactMessage
import com.ismealdi.dactiv.util.Networks
import io.reactivex.Observable
import retrofit2.http.*
import com.ismealdi.dactiv.model.request.Contact as ModelContact


/**
 * Created by Al on 13/10/2018
 */

interface Contacts {

    @GET("contact")
    @Headers("Content-Type: application/json")
    fun get(): Observable<Contact>

    @DELETE("contact/{id}")
    @Headers("Content-Type: application/json")
    fun delete(@Path("id") id: String): Observable<ContactMessage>

    @POST("contact")
    @Headers("Content-Type: application/json")
    fun add(@Body contact: ModelContact): Observable<ContactMessage>

    @PUT("contact/{id}")
    @Headers("Content-Type: application/json")
    fun edit(@Path("id") id: String, @Body contact: ModelContact): Observable<ContactEdit>

    companion object {
        fun init(): Contacts {
            return Networks().retrofit.create(Contacts::class.java)
        }
    }

}