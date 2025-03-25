package com.example.dwello

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PropertyAdapter(
    private val propertyList: List<Property>
) : RecyclerView.Adapter<PropertyAdapter.PropertyViewHolder>() {

    inner class PropertyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivImage: ImageView = itemView.findViewById(R.id.ivPropertyImage)
        val tvTitle: TextView = itemView.findViewById(R.id.tvPropertyTitle)
        val tvLocation: TextView = itemView.findViewById(R.id.tvPropertyLocation)
        val tvPrice: TextView = itemView.findViewById(R.id.tvPropertyPrice)
        val tvDescription: TextView = itemView.findViewById(R.id.tvPropertyDescription)
        val btnRegister: Button = itemView.findViewById(R.id.btnRegister)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_property, parent, false)
        return PropertyViewHolder(view)
    }

    override fun onBindViewHolder(holder: PropertyViewHolder, position: Int) {
        val property = propertyList[position]

        holder.tvTitle.text = property.title
        holder.tvLocation.text = property.location
        holder.tvPrice.text = property.price
        holder.tvDescription.text = property.description

        Glide.with(holder.itemView.context)
            .load(property.imageUrl)
            .into(holder.ivImage)

        holder.btnRegister.setOnClickListener {
            // You can handle register button clicks here
        }
    }

    override fun getItemCount(): Int = propertyList.size
}
