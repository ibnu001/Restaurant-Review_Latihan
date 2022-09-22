package com.ibnu.restaurantreview.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ibnu.restaurantreview.R

class ReviewAdapter(
    private val listReview: List<String>,
) : RecyclerView.Adapter<ReviewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_review, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvItem.text = listReview[position]
    }

    override fun getItemCount() = listReview.size

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tvItem: TextView = v.findViewById(R.id.tvItem)
    }
}