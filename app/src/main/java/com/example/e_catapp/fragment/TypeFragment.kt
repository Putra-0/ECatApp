package com.example.e_catapp.fragment

import JenisAdapter
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
import com.example.e_catapp.activity.PetActivity
import com.example.e_catapp.activity.RegisterActivity
import com.example.e_catapp.activity.TypeActivity
import com.example.e_catapp.adapter.TypeAdapter
import com.example.e_catapp.adapter.UsersAdapter
import com.example.e_catapp.api.ApiConfig
import com.example.e_catapp.helper.PrefManager
import com.example.e_catapp.models.Data
import com.example.e_catapp.models.Jenis
import com.example.e_catapp.models.JenisResponse
import com.example.e_catapp.models.UsersResponse
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TypeFragment : Fragment() {

    private lateinit var addingBtn: FloatingActionButton
    private lateinit var prefManager: PrefManager
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapterJenis: TypeAdapter
    private lateinit var dataList: List<Jenis>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_jenis, container, false)
        initComponents(view)
        listenHandler()
        prefManager = PrefManager(requireContext())

        dataList=ArrayList()

        setupRecyclerView()
        swipeRefreshLayout = view.findViewById(R.id.swipe)
        swipeRefreshLayout.setOnRefreshListener {
            getType()
            swipeRefreshLayout.isRefreshing = false
        }

        getType()
        return view
    }
    private fun getType() {
        val api = ApiConfig.Create(requireContext())
        val callData = api.getJenis()

        callData.enqueue(object : Callback<JenisResponse> {
            override fun onResponse(
                call: Call<JenisResponse>,
                response: Response<JenisResponse>,
            ) {
                if (response.isSuccessful) {
                    val jenisResponse = response.body()
                    jenisResponse?.let {
                        val data = it.data
                        adapterJenis.updateData(data)
                        Log.e("Data", data.toString())
                    }
                } else {
                    Log.e("Response Error", response.message())
                }
            }

            override fun onFailure(call: Call<JenisResponse>, t: Throwable) {
                Log.e("ERROR", t.message.toString())
            }
        })
    }

    private fun setupRecyclerView() {
        adapterJenis = TypeAdapter(ArrayList())
        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
        }
        recyclerView.adapter = adapterJenis
    }

    private fun listenHandler() {

        addingBtn.setOnClickListener {
            val intent = Intent(requireActivity(), TypeActivity::class.java)
            val no = "tambahjenis"
            intent.putExtra("no", no)
            startActivity(intent)
        }
    }

    private fun initComponents(view: View) {
        addingBtn = view.findViewById(R.id.addingBtn)
        recyclerView = view.findViewById(R.id.rv_type)
    }
}
