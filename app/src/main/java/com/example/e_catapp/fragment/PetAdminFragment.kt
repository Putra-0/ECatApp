package com.example.e_catapp.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.e_catapp.R
import com.example.e_catapp.activity.PetActivity
import com.example.e_catapp.adapter.PetAdminAdapter
import com.example.e_catapp.api.ApiConfig
import com.example.e_catapp.helper.PrefManager
import com.example.e_catapp.models.PetResponse
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PetAdminFragment : Fragment() {

    private lateinit var addingBtn: FloatingActionButton
    private lateinit var prefManager: PrefManager
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapterPet: PetAdminAdapter
    private lateinit var searchEditText: EditText
    private lateinit var searchButton: Button
    private lateinit var judul1: TextView
    private lateinit var judul2: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_pet, container, false)
        initComponents(view)
        listenHandler()
        prefManager = PrefManager(requireContext())

        searchButton.setOnClickListener {
            val keyword = searchEditText.text.toString().trim()
            if (keyword.isNotEmpty()) {
                performSearch(keyword)
            } else {
                Toast.makeText(requireContext(), "Tambahkan kata kunci yang akan dicari", Toast.LENGTH_SHORT).show()
            }
        }

        setupRecyclerView()
        swipeRefreshLayout.setOnRefreshListener {
            getPets()
            swipeRefreshLayout.isRefreshing = false
        }

        getPets()
        return view
    }



    private fun performSearch(keyword: String) {
        val apiClient = ApiConfig.Create(requireContext())
        val call = apiClient.search(keyword)

        call.enqueue(object : Callback<PetResponse> {
            override fun onResponse(call: Call<PetResponse>, response: Response<PetResponse>) {
                if (response.isSuccessful) {
                    val petResponse = response.body()
                    petResponse?.let {
                        val data = it.data
                        adapterPet.updateData(data)
                        Log.e("Data", data.toString())
                    }
                }
            }

            override fun onFailure(call: Call<PetResponse>, t: Throwable) {
                // Tangani kesalahan jaringan atau kesalahan lainnya
                Log.e("Error", t.toString())
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getPets() {
        val api = ApiConfig.Create(requireContext())
        val callData = api.getHewan()

        callData.enqueue(object : Callback<PetResponse> {
            override fun onResponse(
                call: Call<PetResponse>,
                response: Response<PetResponse>,
            ) {
                if (response.isSuccessful) {
                    val userResponse = response.body()
                    userResponse?.let {
                        val data = it.data
                        adapterPet.updateData(data)
                        Log.e("Data", data.toString())
                    }
                } else {
                    Log.e("Response Error", response.message())
                }
            }

            override fun onFailure(call: Call<PetResponse>, t: Throwable) {
                Log.e("ERROR", t.message.toString())
            }
        })
    }

    private fun setupRecyclerView() {
        adapterPet = PetAdminAdapter(ArrayList())
        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = adapterPet
        }
    }

    private fun listenHandler() {

        addingBtn.setOnClickListener {
            val intent = Intent(requireActivity(), PetActivity::class.java)
            val no = "tambahhewan"
            intent.putExtra("no", no)
            startActivity(intent)
        }

    }

    private fun initComponents(view: View) {
        addingBtn = view.findViewById(R.id.addingBtn)
        recyclerView = view.findViewById(R.id.rv_petadmin)
        swipeRefreshLayout = view.findViewById(R.id.swipeadmin)
        searchEditText = view.findViewById(R.id.searchBar)
        searchButton = view.findViewById(R.id.searchBtn)
        judul1 = view.findViewById(R.id.data)
        judul2 = view.findViewById(R.id.hewan)

        judul1.visibility = View.VISIBLE
        judul2.visibility = View.VISIBLE
        addingBtn.visibility = View.VISIBLE
        swipeRefreshLayout.visibility = View.VISIBLE
    }
}