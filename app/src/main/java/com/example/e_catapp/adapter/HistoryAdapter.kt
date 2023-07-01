package com.example.e_catapp.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.e_catapp.R
import com.example.e_catapp.activity.BookingDoneActivity
import com.example.e_catapp.models.Transaction
import me.relex.circleindicator.CircleIndicator3

class HistoryAdapter(private val dataList: ArrayList<Transaction>) : RecyclerView.Adapter<HistoryAdapter.ViewHolderData>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderData {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.history_card, parent, false)
        return ViewHolderData(layout)
    }

    override fun onBindViewHolder(holder: ViewHolderData, position: Int) {
        val item = dataList[position]
        holder.bind(item)

        holder.cardView.setOnClickListener {
            val intent = Intent(holder.context, BookingDoneActivity::class.java)
            val data = dataList[position]
            val todone = "check"
            intent.putExtra("idTransaksiView", data.id)
            intent.putExtra("idPet", data.hewan.id)
            intent.putExtra("sk", data.status)
            intent.putExtra("sp", data.status_penerimaan)
            intent.putExtra("pickup", data.tanggal_pengambilan)
            intent.putExtra("updetat", data.updated_at)
            intent.putExtra("todone", todone)
            // Jalankan Intent
            holder.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = dataList.size

    fun updateData(newData: List<Transaction>) {
        dataList.clear()
        dataList.addAll(newData)
        notifyDataSetChanged()
    }

    class ViewHolderData(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val namahewan: TextView = itemView.findViewById(R.id.petName)
        val bookingID: TextView = itemView.findViewById(R.id.bookingID)
        val customerName: TextView = itemView.findViewById(R.id.customerName)
        val pickupDate: TextView = itemView.findViewById(R.id.pickupDate)
        val redStatus: TextView = itemView.findViewById(R.id.redStatus)
        val yellowStatus: TextView = itemView.findViewById(R.id.yellowStatus)
        val orenStatus: TextView = itemView.findViewById(R.id.orenStatus)
        val greenStatus: TextView = itemView.findViewById(R.id.greenStatus)
        val cardView : CardView = itemView.findViewById(R.id.card)
        val context = itemView.context

        @SuppressLint("SetTextI18n")
        fun bind(item: Transaction) {
            namahewan.text = item.hewan.nama_hewan
            bookingID.text = item.id.toString()
            customerName.text = item.user.name
            when (item.status){
                "Dibatalkan" -> {
                    pickupDate.text = "Gagal diambil"
                }
                else -> {
                    pickupDate.text = item.tanggal_pengambilan
                }
            }
            if (item.status == "Menunggu Konfirmasi") {
                greenStatus.visibility = View.GONE
                redStatus.visibility = View.GONE
                yellowStatus.visibility = View.GONE
            } else if (item.status == "Selesai" && item.status_penerimaan == "Diterima") {
                orenStatus.visibility = View.GONE
                redStatus.visibility = View.GONE
                yellowStatus.visibility = View.GONE
            } else if (item.status == "Di Konfirmasi") {
                orenStatus.visibility = View.GONE
                redStatus.visibility = View.GONE
                greenStatus.visibility = View.GONE
            } else if (item.status == "Dibatalkan") {
                greenStatus.visibility = View.GONE
                orenStatus.visibility = View.GONE
                yellowStatus.visibility = View.GONE
            }
        }
    }
}