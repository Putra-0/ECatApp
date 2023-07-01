package com.example.e_catapp.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.e_catapp.R
import com.example.e_catapp.activity.PetActivity
import com.example.e_catapp.activity.MainAdminActivity
import com.example.e_catapp.activity.PetInfoActivity
import com.example.e_catapp.api.ApiConfig
import com.example.e_catapp.models.Pet
import com.example.e_catapp.models.PetResponse
import me.relex.circleindicator.CircleIndicator3
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PetAdminAdapter(private val dataList: ArrayList<Pet>) : RecyclerView.Adapter<PetAdminAdapter.ViewHolderData>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderData {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.petadmin_card, parent, false)
        return ViewHolderData(layout)
    }

    override fun onBindViewHolder(holder: ViewHolderData, position: Int) {
        val item = dataList[position]
        holder.bind(item)

        holder.showPopupMenu.setOnClickListener {
            showPopupMenu(holder.showPopupMenu, position)
        }
        holder.cardView.setOnClickListener {
            val intent = Intent(holder.context, PetInfoActivity::class.java)
            val data = dataList[position]
            val no = "PreviewAdmin"
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

    private fun showPopupMenu(view: View, position: Int) {
        val popupMenu = PopupMenu(view.context, view)
        val inflater = popupMenu.menuInflater
        inflater.inflate(R.menu.menu_hewan, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.editpet -> {
                    val intent = Intent(view.context, PetActivity::class.java)
                    val data = dataList[position]
                    val no = "edithewan"
                    intent.putExtra("id", data.id)
                    intent.putExtra("nama_hewan", data.nama_hewan)
                    intent.putExtra("description", data.description)
                    intent.putExtra("jenis_kelamin", data.jenis_kelamin)
                    intent.putExtra("berat", data.berat)
                    intent.putExtra("harga", data.harga)
                    intent.putExtra("umur", data.umur)
                    intent.putExtra("status", data.status)
                    intent.putExtra("type_id", data.type.nama_type)
                    intent.putExtra("id_type", data.type_id)
                    intent.putExtra("status_vaksin", data.status_vaksin)
                    val imageUrls = data.images.map { it.url } // Mendapatkan daftar URL gambar
                    intent.putStringArrayListExtra("images", ArrayList(imageUrls))
                    intent.putExtra("no", no)
                    ContextCompat.startActivity(view.context, intent, null) // Menggunakan startActivity dari ContextCompat
                    // Menggunakan startActivity dari ContextCompat
                    true
                }

                R.id.hapuspet -> {
                    AlertDialog.Builder(view.context)
                        .setTitle("Hapus Hewan")
                        .setMessage("Apakah kamu yakin ingin menghapus hewan ini?")
                        .setPositiveButton("Ya") { dialog, which ->
                            val data = dataList[position]
                            val idP = data.id
                            deleteHewan(view.context, idP)
                        }
                        .setNegativeButton("Tidak", null)
                        .show()
                    true
                }

                else -> false
            }
        }
        popupMenu.show()
    }

    private fun deleteHewan(context: Context, idP: Int) {
        val api = ApiConfig.Create(context)
        val callData = api.deleteHewan(idP)
        callData.enqueue(object : Callback<PetResponse> {
            override fun onResponse(call: Call<PetResponse>, response: Response<PetResponse>) {
                Log.d("TAG", "onResponse: ${response.body()}")
                val data = response.body()
                if (response.isSuccessful) {
                    Toast.makeText(context, "Berhasil menghapus Hewan", Toast.LENGTH_SHORT).show()
                    val intent = Intent(context, MainAdminActivity::class.java)
                    val fragment = "pet"
                    intent.putExtra("fragment", fragment)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    context.startActivity(intent)
                } else {
                    Toast.makeText(context, "Gagal menghapus Hewan", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PetResponse>, t: Throwable) {
                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun updateData(newData: List<Pet>) {
        dataList.clear()
        dataList.addAll(newData)
        notifyDataSetChanged()
    }

    class ViewHolderData(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nama: TextView = itemView.findViewById(R.id.pet)
        val jenis_kelamin: TextView = itemView.findViewById(R.id.jenis_kelamin)
        val jenis: TextView = itemView.findViewById(R.id.jenis)
        val harga: TextView = itemView.findViewById(R.id.harga)
        val showPopupMenu: ImageView = itemView.findViewById(R.id.menu)
        val carousel: ViewPager2 = itemView.findViewById(R.id.petImage)
        val indicator: CircleIndicator3 = itemView.findViewById(R.id.indikator)
        val tersedia: ConstraintLayout = itemView.findViewById(R.id.tersedia)
        val teradopsi: ConstraintLayout = itemView.findViewById(R.id.teradopsi)
        val terpesan: ConstraintLayout = itemView.findViewById(R.id.terpesan)
        val cardView: ConstraintLayout = itemView.findViewById(R.id.card)

        val context = itemView.context

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
                    // Tambahkan logika atau aksi yang ingin Anda lakukan saat halaman gambar berubah
                }
            })
            indicator.setViewPager(carousel)
        }
    }
}