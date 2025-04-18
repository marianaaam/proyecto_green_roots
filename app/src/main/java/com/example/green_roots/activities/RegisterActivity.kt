package com.example.green_roots.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.green_roots.R
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {

    private lateinit var etName : EditText
    private lateinit var etLastName : EditText
    private lateinit var etEmail : EditText
    private lateinit var etPhone : EditText
    private lateinit var etPassword : EditText
    private lateinit var etConfirmPassword : EditText
    private lateinit var checkBoxTerms: CheckBox
    private lateinit var buttonRegister : Button
    private lateinit var textYesCount : TextView

    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etName = findViewById(R.id.etNameRegister)
        etLastName = findViewById(R.id.etLastNameRegister)
        etEmail = findViewById(R.id.etEmailRegister)
        etPhone = findViewById(R.id.etPhoneRegister)
        etPassword = findViewById(R.id.etPasswordRegister)
        etConfirmPassword = findViewById(R.id.etConfirmPasswordRegister)
        checkBoxTerms = findViewById(R.id.checkboxTermsRegister)
        buttonRegister = findViewById(R.id.buttonRegister)
        textYesCount = findViewById(R.id.textYesCountRegister)

        sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE)

        buttonRegister.setOnClickListener{
            if (validateFields()){
                registerData()
                redirection(LoginActivity::class.java)
            }
        }

        textYesCount.setOnClickListener{
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

    //Functions Apart from the life cycles of the activity

    private fun validateFields() : Boolean{

        fun isFieldEmpty(field: EditText, errorMessage: String): Boolean {
            if (field.text.toString().trim().isEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                return true
            }
            return false
        }

        if (isFieldEmpty(etName, "El campo nombre es requerido")) return false
        if (isFieldEmpty(etLastName, "El campo apellido es requerido")) return false
        if (isFieldEmpty(etEmail, "El campo email es requerido")) return false
        if (isFieldEmpty(etPhone, "El campo teléfono es requerido")) return false
        if (isFieldEmpty(etPassword, "El campo contraseña es requerido")) return false
        if (isFieldEmpty(etConfirmPassword, "El campo confirmar contraseña es requerido")) return false

        if (etPhone.text.toString().trim().length<10){
            Toast.makeText(this, "El número de teléfono debe tener al menos 10 dígitos", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!etEmail.text.toString().trim().matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"))) {
            Toast.makeText(this, "El correo electrónico no es válido", Toast.LENGTH_SHORT).show()
            return false
        }

        if (etPassword.text.toString().trim() != etConfirmPassword.text.toString().trim()) {
            Toast.makeText(this, "La confirmación de la contraseña es incorrecta", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!checkBoxTerms.isChecked) {
            Toast.makeText(this, "Debes aceptar los términos y condiciones", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }
    private fun registerData() {
        try {
            val name = etName.text.toString().trim()
            val lastName = etLastName.text.toString().trim()
            val phone = etPhone.text.toString().trim().toLong()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val rol = "cliente"

            // Obtener el array existente o crear uno nuevo
            val usersJson = sharedPreferences.getString("users", "[]")
            val usersArray = JSONArray(usersJson)
            val newUsersArray = JSONArray()

            // Verificar si el email ya existe
            var userExists = false
            for (i in 0 until usersArray.length()) {
                val user = usersArray.getJSONObject(i)
                if (user.getString("email") == email) {
                    userExists = true
                    break
                }
                newUsersArray.put(user) // Mantener los usuarios existentes
            }

            if (userExists) {
                Toast.makeText(this, "Este correo ya está registrado", Toast.LENGTH_SHORT).show()
                return
            }

            // Agregar el nuevo usuario
            val newUser = JSONObject().apply {
                put("name", name)
                put("lastName", lastName)
                put("phone", phone)
                put("email", email)
                put("password", password)
                put("rol", rol)
            }
            newUsersArray.put(newUser)

            // Guardar el array actualizado
            sharedPreferences.edit()
                .putString("users", newUsersArray.toString())
                .apply()

            Toast.makeText(this, "Registro Exitoso", Toast.LENGTH_SHORT).show()

        } catch (e: NumberFormatException) {
            Toast.makeText(this, "El número de teléfono no es válido", Toast.LENGTH_SHORT).show()
        } catch (e: JSONException) {
            Toast.makeText(this, "Error en el formato de datos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun redirection(destinationActivity:Class<*>){
        val intent= Intent(this, destinationActivity)
        startActivity(intent)
    }

}