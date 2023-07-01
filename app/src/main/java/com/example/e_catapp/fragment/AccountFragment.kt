package com.example.e_catapp.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.e_catapp.activity.LoginActivity
import com.example.e_catapp.R
import com.example.e_catapp.activity.UpdateUserActivity
import com.example.e_catapp.api.ApiConfig
import com.example.e_catapp.helper.PrefManager
import com.example.e_catapp.models.ProfileResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AccountFragment : Fragment() {

    private lateinit var logout: Button
    private lateinit var prefManager: PrefManager
    private lateinit var showPopupMenu: Button
    private lateinit var tvnamalengkap: TextView
    private lateinit var tvalamat: TextView
    private lateinit var tvnotelp: TextView
    private lateinit var tvemail: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_account, container, false)
        initComponents(view)
        listenHandler()
        prefManager = PrefManager(requireContext())
        showProfile()
        return view
    }

    private fun showProfile() {
        val api = ApiConfig.Create(requireContext())
        val callData = api.getProfile()

        callData.enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(
                call: Call<ProfileResponse>,
                response: Response<ProfileResponse>,
            ) {
                if (response.isSuccessful) {
                    val profileresponse = response.body()
                    profileresponse?.let {
                        val data = it.data

                        tvnamalengkap.setText(data.name)
                        tvalamat.setText(data.alamat)
                        tvnotelp.setText(data.email)
                        tvemail.setText(data.no_telp)

                        Log.e("Data", data.toString())
                    }
                } else {
                    Log.e("Response Error", response.message())
                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                Log.e("ERROR", t.message.toString())
            }
        })
    }

    private fun listenHandler() {
        logout.setOnClickListener {
            logOut()
        }
        showPopupMenu.setOnClickListener{
            showPopupMenu()
        }
    }

    private fun initComponents(view: View) {
        logout = view.findViewById(R.id.logout)
        showPopupMenu = view.findViewById(R.id.more)
        tvnamalengkap = view.findViewById(R.id.tvnamalengkap)
        tvalamat = view.findViewById(R.id.tvalamat)
        tvnotelp = view.findViewById(R.id.tvnophone)
        tvemail = view.findViewById(R.id.tvemail)
    }

    private fun showPopupMenu() {
        val popupMenu = PopupMenu(requireContext(), showPopupMenu)
        popupMenu.inflate(R.menu.menu_akunuser)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.editakun -> {
                    val intent = Intent(requireActivity(), UpdateUserActivity::class.java)
                    val no = "editakun"
                    intent.putExtra("no", no)
                    startActivity(intent)
                    true
                }
                R.id.gantipass -> {
                    val intent = Intent(requireActivity(), UpdateUserActivity::class.java)
                    val no = "gantipass"
                    intent.putExtra("no", no)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun logOut() {
        AlertDialog.Builder(requireContext())
            .setTitle("Keluar")
            .setMessage("Apakah anda yakin ingin keluar?")
            .setPositiveButton("Ya") { dialog, which ->
                prefManager.deleteAccessToken()

                val loginPage = Intent(requireContext(), LoginActivity::class.java)
                loginPage.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(loginPage)
                Toast.makeText(requireContext(), "Mengeluarkan akun", Toast.LENGTH_SHORT).show()
                true
            }
            .setNegativeButton("Tidak") { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }
}
