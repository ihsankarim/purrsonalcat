package com.capstoneproject.purrsonalcatapp.ui.detail

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.capstoneproject.purrsonalcatapp.R

class ListArtikelAdapter(private val listArtikel: ArrayList<Artikel>, private val onItemClick: (Artikel)->Unit) : RecyclerView.Adapter<ListArtikelAdapter.ListViewHolder>() {
    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgPhoto: ImageView = itemView.findViewById(R.id.img_item_photo)
        val tvName: TextView = itemView.findViewById(R.id.tv_item_name)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_artikel, parent, false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int= listArtikel.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val (name, description, photo) = listArtikel[position]
        holder.imgPhoto.setImageResource(photo)
        holder.tvName.text = name
        holder.itemView.setOnClickListener {
            onItemClick(listArtikel[position])
        }
    }
}