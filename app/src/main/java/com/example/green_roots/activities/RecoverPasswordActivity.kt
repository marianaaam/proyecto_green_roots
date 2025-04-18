package com.example.green_roots.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.green_roots.R
import java.util.regex.Pattern
import org.json.JSONArray
import org.json.JSONException

class RecoverPasswordActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var buttonRecoveryPassword: Button
    private lateinit var textGoBack: TextView
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recover_password)

        etEmail = findViewById(R.id.etEmailRecoveryPassword)
        buttonRecoveryPassword = findViewById(R.id.buttonRecoveryPassword)
        textGoBack = findViewById(R.id.textGoBackRecoveryPassword)

        sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE)

        buttonRecoveryPassword.setOnClickListener {
            if (validateFields()) {
                recoverPassword()
            }
        }

        textGoBack.setOnClickListener {
            redirection(LoginActivity::class.java)
        }
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

    private fun validateFields(): Boolean {
        if (etEmail.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "El campo correo electrónico es requerido", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
                .matcher(etEmail.text.toString().trim())
                .matches()) {
            Toast.makeText(this, "El correo electrónico no es válido", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun recoverPassword() {
        val email = etEmail.text.toString().trim()

        try {
            val usersJson = sharedPreferences.getString("users", "[]")
            val usersArray = JSONArray(usersJson)

            for (i in 0 until usersArray.length()) {
                val user = usersArray.getJSONObject(i)
                if (user.getString("email") == email) {
                    Toast.makeText(this, "Se envió el correo exitosamente", Toast.LENGTH_SHORT).show()
                    return
                }
            }

            Toast.makeText(this, "El correo digitado no está registrado", Toast.LENGTH_SHORT).show()
        } catch (e: JSONException) {
            Toast.makeText(this, "Error al procesar los datos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun redirection(destinationActivity: Class<*>) {
        val intent = Intent(this, destinationActivity)
        startActivity(intent)
    }

}