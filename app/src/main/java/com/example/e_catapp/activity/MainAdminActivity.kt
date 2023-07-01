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
import com.example.e_catapp.fragment.PetAdminFragment
import com.example.e_catapp.fragment.PetFragment
import com.example.e_catapp.fragment.TransactionFragment
import com.example.e_catapp.fragment.TypeFragment
import com.example.e_catapp.fragment.UsersFragment

class MainAdminActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var frameLayout: FrameLayout

    private lateinit var petAdminFragment: PetAdminFragment
    private lateinit var jenisFragment: TypeFragment
    private lateinit var transactionFragment: TransactionFragment
    private lateinit var usersFragment: UsersFragment
    private lateinit var accountFragment: AccountFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_view)

        initComponents()
        val fragment = intent.getStringExtra("fragment")
        when (fragment) {
            "pet" -> setFragment(petAdminFragment)
            "jenis" -> setFragment(jenisFragment)
            "transaksi" -> setFragment(transactionFragment)
            "pengguna" -> setFragment(usersFragment)
            "profil" -> setFragment(accountFragment)
            else -> setFragment(petAdminFragment) // Fragment default jika tidak ada nilai yang diterima
        }

        clickListener()
    }

    private fun clickListener() {
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_pet -> {
                    setFragment(petAdminFragment)
                    true
                }
                R.id.nav_type -> {
                    setFragment(jenisFragment)
                    true
                }
                R.id.nav_history -> {
                    setFragment(transactionFragment)
                    true
                }
                R.id.nav_account -> {
                    setFragment(usersFragment)
                    true
                }
                R.id.nav_profile -> {
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

        petAdminFragment = PetAdminFragment()
        jenisFragment = TypeFragment()
        transactionFragment = TransactionFragment()
        usersFragment = UsersFragment()
        accountFragment = AccountFragment()
    }
}