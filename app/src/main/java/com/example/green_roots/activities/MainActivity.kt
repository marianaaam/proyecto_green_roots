package com.example.green_roots.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import com.example.green_roots.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /* Configuración de la barra de herramientas */
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        /* Navegación de los fragments */
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        /* Configuración del BottomNavigationView */
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Define los destinos del top level (sin el botón de regreso)
        appBarConfiguration = AppBarConfiguration.Builder(
            R.id.ProductsFragment,
            R.id.CartFragment,
            R.id.PerfilFragment,
            R.id.CategoriasFragment
        ).build()

        // Configura la barra superior para trabajar con la navegación
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)

        // Conecta el BottomNavigationView con el NavController
        NavigationUI.setupWithNavController(bottomNavigationView, navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}