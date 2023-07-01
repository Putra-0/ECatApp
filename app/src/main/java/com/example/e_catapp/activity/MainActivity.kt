package com.example.e_catapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.e_catapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.e_catapp.fragment.AccountFragment
import com.example.e_catapp.fragment.HistoryFragment
import com.example.e_catapp.fragment.PetFragment

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var frameLayout: FrameLayout

    private lateinit var petFragment: PetFragment
    private lateinit var historyFragment: HistoryFragment
    private lateinit var accountFragment: AccountFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_view)

        initComponents()
        val fragment = intent.getStringExtra("fragment")
        when (fragment) {
            "pet" -> setFragment(petFragment)
            "history" -> setFragment(historyFragment)
            "profil" -> setFragment(accountFragment)
            else -> setFragment(petFragment) // Fragment default jika tidak ada nilai yang diterima
        }

        clickListener()
    }

    private fun clickListener() {
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_pet -> {
                    setFragment(petFragment)
                    true
                }
                R.id.nav_history -> {
                    setFragment(historyFragment)
                    true
                }
                R.id.nav_account -> {
                    setFragment(accountFragment)
                    true
                }
                else -> false
            }
        }
    }

    private fun setFragment(fragment: Fragment, data: String? = null) {
        val bundle = Bundle().apply {
            putString("CUSTOMERID", data)
        }
        fragment.arguments = bundle
        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.framelayout, fragment)
        fragmentTransaction.commit()
    }

    private fun initComponents() {
        bottomNavigationView = findViewById(R.id.bottom_nav)
        frameLayout = findViewById(R.id.framelayout)

        petFragment = PetFragment()
        historyFragment = HistoryFragment()
        accountFragment = AccountFragment()

    }
}