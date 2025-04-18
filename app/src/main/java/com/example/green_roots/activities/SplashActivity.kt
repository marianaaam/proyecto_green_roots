package com.example.green_roots.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.green_roots.R
import org.json.JSONArray
import org.json.JSONObject

class SplashActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE)

        val intent = Intent(this, LoginActivity::class.java)

        loadData()

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(intent)
            finish()
        }, 3000)
    }

    override fun onStart() {
        super.onStart()
        Log.d("Lifecycle", "onStart llamado")
    }

    override fun onResume() {
        super.onResume()
        Log.d("Lifecycle", "onResume llamado")
    }

    override fun onPause() {
        super.onPause()
        Log.d("Lifecycle", "onPause llamado")
    }

    override fun onStop() {
        super.onStop()
        Log.d("Lifecycle", "onStop llamado")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("Lifecycle", "onRestart llamado")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Lifecycle", "onDestroy llamado")
    }

    fun loadData(){
        // Obtener el array existente o crear uno nuevo
        val usersJson = sharedPreferences.getString("users", "[]")
        val usersArray = JSONArray(usersJson)
        val newUsersArray = JSONArray()


        for (i in 0 until usersArray.length()) {
            val user = usersArray.getJSONObject(i)
            newUsersArray.put(user) // Mantener los usuarios existentes
        }

        // Agregar el usuario por defecto
        val newUser = JSONObject().apply {
            put("name", "Admin")
            put("lastName", "Admin")
            put("phone", "00000000".toLong())
            put("email", "admin@gmail.com")
            put("password", "admin")
            put("rol", "admin")
        }
        newUsersArray.put(newUser)

        // Guardar el array actualizado
        sharedPreferences.edit()
            .putString("users", newUsersArray.toString())
            .apply()
    }
}