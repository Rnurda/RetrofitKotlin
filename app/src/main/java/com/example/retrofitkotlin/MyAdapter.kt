package com.example.retrofitkotlin

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.support.v7.widget.RecyclerView
import com.example.retrofitkotlin.PojoModel.Language
import kotlinx.android.synthetic.main.row_layout.view.*

class MyAdapter (private val languageList : ArrayList<Language>, private val listener : Listener) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {
    interface Listener {
        fun onItemClick(language: Language,position: Int)
    }
    private val colors : Array<String> = arrayOf("#7E57C2", "#42A5F5", "#26C6DA", "#66BB6A", "#FFEE58", "#FF7043" , "#EC407A" , "#d32f2f")

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(languageList[position], listener, colors, position)
    }

    override fun getItemCount(): Int = languageList.count()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_layout, parent, false)
        return ViewHolder(view)

    }

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        fun bind(language:Language, listener: Listener, colors : Array<String>, position: Int) {

            itemView.setOnClickListener{ listener.onItemClick(language,position) }
            itemView.setBackgroundColor(Color.parseColor(colors[position % 8]))
            itemView.language_id.text = language.id.toString()
            itemView.language_name.text = language.name
        }
    }
}