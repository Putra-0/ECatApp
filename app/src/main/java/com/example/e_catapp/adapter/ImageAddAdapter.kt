package com.example.e_catapp.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.e_catapp.R
import com.example.e_catapp.activity.PetActivity

class ImageAddAdapter(private val context: PetActivity, private val imageUris: MutableList<Uri>) :
    RecyclerView.Adapter<ImageAddAdapter.ImageViewHolder>() {

    private var imageClickListener: OnImageClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.pet_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUri = imageUris[position]
        Glide.with(context)
            .load(imageUri)
            .centerCrop()
            .placeholder(R.drawable.placeholder)
            .into(holder.imageView)
        // Set click listener to handle image click
        holder.itemView.setOnClickListener {
            imageClickListener?.onImageClick(position)
        }
    }

    override fun getItemCount(): Int {
        return imageUris.size
    }

    interface OnImageClickListener {
        fun onImageClick(position: Int)
    }

    fun setOnImageClickListener(listener: OnImageClickListener) {
        imageClickListener = listener
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.carouselImage)
    }
}