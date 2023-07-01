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
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.e_catapp.R
import com.example.e_catapp.activity.MainAdminActivity
import com.example.e_catapp.activity.UpdateUserActivity
import com.example.e_catapp.api.ApiConfig
import com.example.e_catapp.models.Data
import com.example.e_catapp.models.FormResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UsersAdapter(private val dataList: ArrayList<Data>) : RecyclerView.Adapter<UsersAdapter.ViewHolderData>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderData {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.users_card, parent, false)
        return ViewHolderData(layout)
    }

    override fun onBindViewHolder(holder: ViewHolderData, position: Int) {
        val item = dataList[position]
        holder.nama.text = item.name
        holder.alamat.text = item.alamat
        holder.notelp.text = item.no_telp
        holder.emaill.text = item.email

        holder.showPopupMenu.setOnClickListener{
            showPopupMenu(holder.showPopupMenu, position)
        }
    }

    override fun getItemCount(): Int = dataList.size

    private fun showPopupMenu(view: View, position: Int) {
        val popupMenu = PopupMenu(view.context, view)
        val inflater = popupMenu.menuInflater
        inflater.inflate(R.menu.menu_users, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.edituser -> {

                    val intent = Intent(view.context, UpdateUserActivity::class.java)
                    val data = dataList[position]
                    val no = "edituser"
                    intent.putExtra("id", data.id)
                    intent.putExtra("name", data.name)
                    intent.putExtra("email", data.email)
                    intent.putExtra("alamat", data.alamat)
                    intent.putExtra("no_telp", data.no_telp)
                    intent.putExtra("no", no)
                    ContextCompat.startActivity(
                        view.context,
                        intent,
                        null
                    ) // Menggunakan startActivity dari ContextCompat
                    true
                }

                R.id.hapususer -> {
                    AlertDialog.Builder(view.context)
                        .setTitle("Delete Project")
                        .setMessage("Are you sure you want to delete this project?")
                        .setPositiveButton("Yes") { dialog, which ->
                            val data = dataList[position]
                            val idP = data.id
                            deleteUser(view.context, idP)
                        }
                        .setNegativeButton("No", null)
                        .show()
                    true
                }

                else -> false
            }
        }
        popupMenu.show()
    }

    private fun deleteUser(context: Context, idP: Int) {
        val api = ApiConfig.Create(context)
        val callData = api.deleteUser(idP)
        callData.enqueue(object : Callback<FormResponse> {
            override fun onResponse(call: Call<FormResponse>, response: Response<FormResponse>) {
                Log.d("TAG", "onResponse: ${response.body()}")
                val data = response.body()
                if (response.isSuccessful) {
                    Toast.makeText(context, "Berhasil menghapus data pengguna", Toast.LENGTH_SHORT).show()
                    val intent = Intent(context, MainAdminActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    context.startActivity(intent)
                } else {
                    Toast.makeText(context, "Gagal menghapus  data pengguna", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<FormResponse>, t: Throwable) {
                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun updateData(newData: List<Data>) {
        dataList.clear()
        dataList.addAll(newData)
        notifyDataSetChanged()
    }

    class ViewHolderData(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nama: TextView = itemView.findViewById(R.id.namalengkap)
        val alamat: TextView = itemView.findViewById(R.id.alamat)
        val notelp: TextView = itemView.findViewById(R.id.notelp)
        val emaill: TextView = itemView.findViewById(R.id.email)
        val context = itemView.context

        val showPopupMenu: ImageView = itemView.findViewById(R.id.menu)
    }
}