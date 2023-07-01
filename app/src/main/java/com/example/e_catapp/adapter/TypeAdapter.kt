package com.example.e_catapp.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.e_catapp.R
import com.example.e_catapp.activity.MainAdminActivity
import com.example.e_catapp.activity.TypeActivity
import com.example.e_catapp.api.ApiConfig
import com.example.e_catapp.models.Jenis
import com.example.e_catapp.models.JenisResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class TypeAdapter(private val dataList: ArrayList<Jenis>) : RecyclerView.Adapter<TypeAdapter.ViewHolderData>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderData {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.jenis_card, parent, false)
        return ViewHolderData(layout)
    }

    override fun onBindViewHolder(holder: ViewHolderData, position: Int) {
        val item = dataList[position]
        holder.nama.text = item.nama_type

        holder.showPopupMenu.setOnClickListener{
            showPopupMenu(holder.showPopupMenu, position)
        }
    }

    override fun getItemCount(): Int = dataList.size

    private fun showPopupMenu(view: View, position: Int) {
        val popupMenu = PopupMenu(view.context, view)
        val inflater = popupMenu.menuInflater
        inflater.inflate(R.menu.menu_type, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.edittype -> {
                    val intent = Intent(view.context, TypeActivity::class.java)
                    val data = dataList[position]
                    val no = "editjenis"
                    intent.putExtra("id", data.id)
                    intent.putExtra("nama_type", data.nama_type)
                    intent.putExtra("no", no)
                    ContextCompat.startActivity(
                        view.context,
                        intent,
                        null
                    ) // Menggunakan startActivity dari ContextCompat
                    true
                }

                R.id.hapustype -> {
                    AlertDialog.Builder(view.context)
                        .setTitle("Hapus Jenis")
                        .setMessage("Apakah anda yakin ingin menghapus jenis ini?")
                        .setPositiveButton("Ya") { dialog, which ->
                            val data = dataList[position]
                            val idP = data.id
                            deleteType(view.context, idP)
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

    private fun deleteType(context: Context, idP: Int) {
        val api = ApiConfig.Create(context)
        val callData = api.deleteJenis(idP)
        callData.enqueue(object : Callback<JenisResponse> {
            override fun onResponse(call: Call<JenisResponse>, response: Response<JenisResponse>) {
                Log.d("TAG", "onResponse: ${response.body()}")
                val data = response.body()
                if (response.isSuccessful) {
                    Toast.makeText(context, "Berhasil menghapus data jenis", Toast.LENGTH_SHORT).show()
                    val intent = Intent(context, MainAdminActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    context.startActivity(intent)
                } else {
                    Toast.makeText(context, "Gagal menghapus  data jenis", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<JenisResponse>, t: Throwable) {
                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun updateData(newData: List<Jenis>) {
        dataList.clear()
        dataList.addAll(newData)
        notifyDataSetChanged()
    }

    class ViewHolderData(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nama: TextView = itemView.findViewById(R.id.nama_jenis)
        val context = itemView.context

        val showPopupMenu: Button = itemView.findViewById(R.id.menu)
    }
}