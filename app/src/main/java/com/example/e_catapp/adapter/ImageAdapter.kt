package com.example.e_catapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.e_catapp.R
import com.example.e_catapp.models.Image

class ImageAdapter(private val context: Context, private val images: List<Image>) : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.pet_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val imageUrl = images[position].url
        if (images.isNotEmpty()) {
            Glide.with(context)
                .load(imageUrl)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.notfound404)
                .into(holder.image)
        } else {
            Glide.with(context)
                .load(R.drawable.nophoto)
                .into(holder.image)
        }
    }

    override fun getItemCount(): Int = images.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.carouselImage)
    }
}
