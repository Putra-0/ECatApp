package com.example.e_catapp.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.e_catapp.R
import com.example.e_catapp.activity.PetActivity
import com.example.e_catapp.activity.PetInfoActivity
import com.example.e_catapp.models.Jenis
import com.example.e_catapp.models.Pet
import me.relex.circleindicator.CircleIndicator3


class PetAdapter(private val dataList: ArrayList<Pet>) : RecyclerView.Adapter<PetAdapter.ViewHolderData>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderData {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.pet_card, parent, false)
        return ViewHolderData(layout)
    }

    override fun onBindViewHolder(holder: ViewHolderData, position: Int) {
        val item = dataList[position]
        holder.bind(item)

        holder.btndetail.setOnClickListener {
            val intent = Intent(holder.context, PetInfoActivity::class.java)
            val data = dataList[position]
            val no = "PreviewUser"
            intent.putExtra("id", data.id)
            intent.putExtra("nama_hewan", data.nama_hewan)
            intent.putExtra("description", data.description)
            intent.putExtra("jenis_kelamin", data.jenis_kelamin)
            intent.putExtra("berat", data.berat)
            intent.putExtra("harga", data.harga)
            intent.putExtra("umur", data.umur)
            intent.putExtra("nama_type", data.type.nama_type)
            intent.putExtra("status", data.status)
            intent.putExtra("status_vaksin", data.status_vaksin)
            intent.putExtra("no", no)
            val imageUrls = data.images.map { it.url } // Mendapatkan daftar URL gambar
            intent.putStringArrayListExtra("images", ArrayList(imageUrls))
            // Jalankan Intent
            holder.context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int = dataList.size

    fun updateData(newData: List<Pet>) {
        dataList.clear()
        dataList.addAll(newData)
        notifyDataSetChanged()
    }

    class ViewHolderData(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nama: TextView = itemView.findViewById(R.id.pet)
        val jenis_kelamin: TextView = itemView.findViewById(R.id.jenis_kelamin)
        val carousel: ViewPager2 = itemView.findViewById(R.id.petImage)
        val harga: TextView = itemView.findViewById(R.id.harga)
        val jenis: TextView = itemView.findViewById(R.id.jenis)
        val btndetail: Button = itemView.findViewById(R.id.detail)
        val indicator: CircleIndicator3 = itemView.findViewById(R.id.indikator)
        val tersedia: ConstraintLayout = itemView.findViewById(R.id.tersedia)
        val teradopsi: ConstraintLayout = itemView.findViewById(R.id.tidakTersedia)
        val terpesan: ConstraintLayout = itemView.findViewById(R.id.terpesan)

        val context = itemView.context

        @SuppressLint("SetTextI18n")
        fun bind(item: Pet) {
            nama.text = item.nama_hewan
            jenis_kelamin.text = item.jenis_kelamin
            jenis.text = item.type.nama_type
            if (item.harga == "0") harga.text = "Gratis!" else harga.text = "Rp."+item.harga
            when (item.status) {
                "Tersedia" -> {
                    teradopsi.visibility = View.GONE
                    terpesan.visibility = View.GONE
                }
                "Sudah Dibooking" -> {
                    tersedia.visibility = View.GONE
                    teradopsi.visibility = View.GONE
                }
                "Tidak Tersedia" -> {
                    tersedia.visibility = View.GONE
                    terpesan.visibility = View.GONE
                }
            }

            // Menggunakan Picasso untuk memuat gambar dari URL ke ImageView
            val imageAdapter = ImageAdapter(context, item.images)
            carousel.adapter = imageAdapter
            carousel.orientation = ViewPager2.ORIENTATION_HORIZONTAL
            carousel.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
//                    Toast.makeText(context, "Anda Memilih "+nama, Toast.LENGTH_SHORT).show()
                }
            })
            indicator.setViewPager(carousel)
        }
    }
}