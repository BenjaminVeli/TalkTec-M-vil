package com.benjaminveli.integrador.activity


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.benjaminveli.integrador.API.PostApiService
import com.google.android.material.navigation.NavigationView
import com.benjaminveli.integrador.ConfigurationFragment
import com.benjaminveli.integrador.HomeFragment
import com.benjaminveli.integrador.ProfileFragment
import com.benjaminveli.integrador.R
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class WelcomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navHeaderTextView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Accede al encabezado del cajón de navegación
        val navHeader = navigationView.getHeaderView(0)
        navHeaderTextView = navHeader.findViewById(R.id.navHeaderTextView)

        val userId = intent.getIntExtra("userId", -1)
        Log.d("WelcomeActivity", "User ID: $userId")


        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment()).commit()
            navigationView.setCheckedItem(R.id.nav_home)
        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, HomeFragment()).commit()
            }

            R.id.nav_configuration -> {
                val configurationFragment = ConfigurationFragment()
                val bundle = Bundle()

                // Obtén el email, la contraseña y el userId de los extras del intent
                val email = intent.getStringExtra("emailview")
                val password = intent.getStringExtra("passwordview")
                val userId = intent.getIntExtra("userId", -1)

                // Pasa el email, la contraseña y el userId al ConfigurationFragment
                bundle.putString("email", email)
                bundle.putString("password", password)
                bundle.putInt("userId", userId)

                configurationFragment.arguments = bundle

                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, configurationFragment)
                    .commit()
            }

            R.id.nav_profile -> {

                val retrofit = Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:4000/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val service = retrofit.create(PostApiService::class.java)
                val profileFragment = ProfileFragment(service)
                val bundle = Bundle()

                // Obtén el username y el usercode de los extras del intent
                val username = intent.getStringExtra("username")
                val departmentview = intent.getStringExtra("departmentview")
                val usercode = intent.getStringExtra("usercode")

                // Pasa el username y el usercode al ProfileFragment
                bundle.putString("username", username)
                bundle.putString("departmentview", departmentview)
                bundle.putString("usercode", usercode)

                profileFragment.arguments = bundle

                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, profileFragment)
                    .commit()
            }

            R.id.nav_logout -> {
                Toast.makeText(this, "Cerrando sesión", Toast.LENGTH_SHORT).show()
                cerrarSesion()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }


    private fun cerrarSesion() {
        val intent = Intent(this@WelcomeActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
