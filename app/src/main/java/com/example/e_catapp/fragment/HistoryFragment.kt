package com.example.e_catapp.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.e_catapp.R
import com.example.e_catapp.adapter.HistoryAdapter
import com.example.e_catapp.api.ApiConfig
import com.example.e_catapp.helper.PrefManager
import com.example.e_catapp.models.Transaction
import com.example.e_catapp.models.TransactionShowResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistoryFragment : Fragment() {

    private lateinit var prefManager: PrefManager
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapterHistory: HistoryAdapter
    private lateinit var dataList: List<Transaction>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)
        initComponents(view)
        prefManager = PrefManager(requireContext())

        dataList=ArrayList()

        setupRecyclerView()
        swipeRefreshLayout = view.findViewById(R.id.swipe)
        swipeRefreshLayout.setOnRefreshListener {
            getTransaction()
            swipeRefreshLayout.isRefreshing = false
        }

        getTransaction()
        return view
    }
    private fun getTransaction() {
        val api = ApiConfig.Create(requireContext())
        val userID = prefManager.getUserId() ?:0
        val callData = api.getFragmentTransactions(userID)

        callData.enqueue(object : Callback<TransactionShowResponse> {
            override fun onResponse(
                call: Call<TransactionShowResponse>,
                response: Response<TransactionShowResponse>,
            ) {
                if (response.isSuccessful) {
                    val historyResponse = response.body()
                    historyResponse?.let {
                        val data = it.data
                        adapterHistory.updateData(data)
                        Log.e("Data", data.toString())
                    }
                } else {
                    Log.e("Response Error", response.message())
                }
            }

            override fun onFailure(call: Call<TransactionShowResponse>, t: Throwable) {
                Log.e("ERROR", t.message.toString())
            }
        })
    }

    private fun setupRecyclerView() {
        adapterHistory = HistoryAdapter(ArrayList())
        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
        }
        recyclerView.adapter = adapterHistory
    }

    private fun initComponents(view: View) {
        recyclerView = view.findViewById(R.id.rv_history)
    }
}
