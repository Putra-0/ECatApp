package com.example.e_catapp.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.e_catapp.R
import com.example.e_catapp.activity.UpdateUserActivity
import com.example.e_catapp.activity.LoginActivity
import com.example.e_catapp.activity.RegisterActivity
import com.example.e_catapp.adapter.UsersAdapter
import com.example.e_catapp.api.ApiConfig
import com.example.e_catapp.helper.PrefManager
import com.example.e_catapp.models.Data
import com.example.e_catapp.models.UsersResponse
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UsersFragment : Fragment() {

    private lateinit var addingBtn: FloatingActionButton
    private lateinit var prefManager: PrefManager
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapterUser: UsersAdapter
    private lateinit var dataList: List<Data>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_users, container, false)
        initComponents(view)
        listenHandler()
        prefManager = PrefManager(requireContext())

        dataList=ArrayList()

        setupRecyclerView()
        swipeRefreshLayout = view.findViewById(R.id.swipe)
        swipeRefreshLayout.setOnRefreshListener {
            getUsers()
            swipeRefreshLayout.isRefreshing = false
        }

        getUsers()
        return view
    }
    private fun getUsers() {
        val api = ApiConfig.Create(requireContext())
        val callData = api.getUsers()

        callData.enqueue(object : Callback<UsersResponse> {
            override fun onResponse(
                call: Call<UsersResponse>,
                response: Response<UsersResponse>,
            ) {
                if (response.isSuccessful) {
                    val userResponse = response.body()
                    userResponse?.let {
                        val data = it.data
                        adapterUser.updateData(data)
                        Log.e("Data", data.toString())
                    }
                } else {
                    Log.e("Response Error", response.message())
                }
            }

            override fun onFailure(call: Call<UsersResponse>, t: Throwable) {
                Log.e("ERROR", t.message.toString())
            }
        })
    }

    private fun setupRecyclerView() {
        adapterUser = UsersAdapter(ArrayList())
        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
        }
        recyclerView.adapter = adapterUser
    }

    private fun listenHandler() {

        addingBtn.setOnClickListener {
            val intent = Intent(requireActivity(), RegisterActivity::class.java)
            startActivity(intent)
        }
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

    private fun initComponents(view: View) {
        addingBtn = view.findViewById(R.id.addingBtn)
        recyclerView = view.findViewById(R.id.rv_user)
    }
}
