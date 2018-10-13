package com.ismealdi.dactiv.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.ismealdi.dactiv.R
import com.ismealdi.dactiv.activity.MainActivity
import com.ismealdi.dactiv.model.Contact
import com.ismealdi.dactiv.util.CircleTransform
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_contact.view.*

class ContactAdapter(private var contacts: MutableList<Contact>, private val context: Context) : RecyclerView.Adapter<ContactAdapter.ViewHolder>() {

    private val Int.px: Int
        get() = (this * Resources.getSystem().displayMetrics.density).toInt()

    private var mCurrentAlphabet: Char? = null

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: AppCompatTextView = itemView.textName
        val time: AppCompatTextView = itemView.textTime
        val category: AppCompatTextView = itemView.textCategory
        val frame: ConstraintLayout = itemView.layoutFrame
        val image : ImageView = itemView.imagePhoto
        val lineFirst: View = itemView.viewLineFirst
        val lineLast: View = itemView.viewLine

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_contact, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contact = contacts[holder.adapterPosition]
        val alpha = contact.firstName?.get(0)?.toUpperCase()

        if(holder.adapterPosition == 0)
            holder.lineFirst.visibility = View.INVISIBLE

        if(position == (contacts.size - 1))
            holder.lineLast.visibility = View.INVISIBLE

        holder.time.text = "${contact.firstName} ${contact.lastName}"
        holder.name.text = "${contact.age} y.o"

        if(mCurrentAlphabet != alpha) {
            holder.category.visibility = View.VISIBLE
            holder.category.text = "$alpha"

        }else {
            holder.category.visibility = View.GONE
            if(position == (contacts.size - 1))
                holder.lineLast.visibility = View.INVISIBLE
            else
                holder.lineLast.visibility = View.VISIBLE
        }

        mCurrentAlphabet = alpha

        if(contact.photo!!.isNotBlank()) {
            Picasso.get().load(contact.photo)
                .transform(CircleTransform())
                .placeholder(R.drawable.label_waiting)
                .into(holder.image)
        }

        holder.frame.setOnClickListener {
            context as MainActivity
            context.showDetail(holder.adapterPosition, contact)
        }

    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    fun addAll(data: MutableList<Contact>) {
        mCurrentAlphabet = null
        contacts.clear()
        contacts = data

        notifyDataSetChanged()
    }

    fun delete(position: Int) {
        mCurrentAlphabet = if(position == 0) null else contacts[position - 1].firstName?.get(0)?.toUpperCase()

        contacts.removeAt(position)

        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount - position)
    }

    fun update(data: Contact, position: Int) {
        contacts[position] = data
        contacts.sortBy { contact -> contact.firstName?.toUpperCase() }

        mCurrentAlphabet = null
        notifyDataSetChanged()
    }

}
