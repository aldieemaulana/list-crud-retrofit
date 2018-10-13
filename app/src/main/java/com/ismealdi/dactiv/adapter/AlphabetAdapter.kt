package com.ismealdi.dactiv.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ismealdi.dactiv.R
import com.ismealdi.dactiv.activity.MainActivity
import kotlinx.android.synthetic.main.list_alphabet.view.*

class AlphabetAdapter(private var chars: List<String>, private val context: Context) : RecyclerView.Adapter<AlphabetAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val character: AppCompatTextView = itemView.textChar
        val frame: ConstraintLayout = itemView.layoutFrame
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_alphabet, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.character.text = chars[holder.adapterPosition]

        holder.frame.setOnClickListener {
            changeDate(chars[holder.adapterPosition].get(0))
        }

    }

    private fun changeDate(character: Char) {
        context as MainActivity
        context.scrollToAlphabet(character)
    }

    override fun getItemCount(): Int {
        return chars.size
    }

}
