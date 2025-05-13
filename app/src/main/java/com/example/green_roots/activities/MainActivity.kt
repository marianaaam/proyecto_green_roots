package com.example.green_roots.activities

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.example.green_roots.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var toolbar: Toolbar
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE)

        /* Configuración de la barra de herramientas */
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        /* Navegación de los fragments */
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val activeUserJson = sharedPreferences.getString("activeUser", null)
        navigationView = findViewById(R.id.nav_view)
        drawerLayout = findViewById(R.id.drawer_layout)

        if (activeUserJson != null) {
            val activeUser = JSONObject(activeUserJson)
            val rol = activeUser.getString("rol")

            when (rol) {
                "admin" -> navigationView.inflateMenu(R.menu.drawer_menu_admin)
                "cliente" -> navigationView.inflateMenu(R.menu.drawer_menu_client)
                else -> {
                    Toast.makeText(this, "Rol desconocido", Toast.LENGTH_SHORT).show()
                    navigationView.inflateMenu(R.menu.drawer_menu_client) // Menú por defecto
                }
            }
        } else {
            Toast.makeText(this, "No hay usuario activo", Toast.LENGTH_SHORT).show()
        }


        // Define los destinos del top level (sin el botón de regreso)
        appBarConfiguration = AppBarConfiguration.Builder(
            R.id.inicioFragment,
            R.id.CartFragment,
            R.id.PerfilFragment,
            R.id.DirectionsFragment,
            R.id.ProductsFragment
        ).setDrawerLayout(drawerLayout)
        .build()

        // Configura la barra superior para trabajar con la navegación
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)

        // Conecta el BottomNavigationView con el NavController
        navigationView.setupWithNavController(navController)


    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}