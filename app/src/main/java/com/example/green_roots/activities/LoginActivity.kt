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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private lateinit var etUsername : EditText
    private lateinit var etPassword : EditText
    private lateinit var buttonSingIn : Button
    private lateinit var buttonSignInGoogle : Button
    private lateinit var textRegister : TextView
    private lateinit var textRecoverPassword : TextView
    private lateinit var mGoogleSignInClient : GoogleSignInClient
    private val RC_SIGN_IN = 123
    private val TAG = "GoogleSignInActivity"


    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etUsername = findViewById(R.id.etUsernameLogin)
        etPassword = findViewById(R.id.etPasswordLogin)
        buttonSingIn = findViewById(R.id.buttonSingInLogin)
        textRegister = findViewById(R.id.textRegisterLogin)
        textRecoverPassword = findViewById(R.id.textRecoverPasswordLogin)

        sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE)

        //Configurar Google Sing in
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .build()

        //Crear el cliente de Google Sing in
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso)

        buttonSignInGoogle= findViewById(R.id.buttonInGoogleLogin)

        buttonSignInGoogle.setOnClickListener({
            signIn()
        })

        buttonSingIn.setOnClickListener {
            if (validateFields() && validateData()) {
                redirection(MainActivity::class.java)
            }
        }

        textRecoverPassword.setOnClickListener{
            redirection(RecoverPasswordActivity::class.java)
        }

        textRegister.setOnClickListener {
            redirection(RegisterActivity::class.java)
        }
    }

    private fun signIn(){
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==RC_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    // Función para manejar el inicio de sesión con Google
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {

            val account = completedTask.getResult(ApiException::class.java)
            val email = account.email ?: ""
            val name = account.givenName ?: ""
            val lastName = account.familyName ?: ""

            // Verificar si el usuario ya existe
            val userExists = checkIfUserExists(email)

            if (!userExists) {
                // Registrar automáticamente al usuario si no existe
                registerGoogleUser(
                    name = name,
                    lastName = lastName,
                    email = email,
                    phone = 0L,
                    password = "google_auth", // Contraseña ficticia para usuarios de Google
                    rol = "cliente"
                )
            }


            //Guardar el usuario activo despues del registro
            val usersJson = sharedPreferences.getString("users", "[]")
            val usersArray = JSONArray(usersJson)

            for (i in 0 until usersArray.length()) {
                val user = usersArray.getJSONObject(i)
                if (user.getString("email") == email) {

                    // Si los datos son correctos, guardamos al usuario activo
                    val editor = sharedPreferences.edit()
                    editor.putString("activeUser", user.toString())
                    editor.apply()
                }
            }

            // Iniciar sesión normalmente (ya sea usuario nuevo o existente)
            Log.d(TAG, "Inicio de sesión exitoso: ${account.id}")
            Toast.makeText(this, "Bienvenido ${account.displayName}", Toast.LENGTH_SHORT).show()

            redirection(MainActivity::class.java)
            finish()

        } catch (e: Exception) {
            Log.e(TAG, "Error en handleGoogleSignIn", e)
            Toast.makeText(this, "Error al procesar el inicio de sesión", Toast.LENGTH_SHORT).show()
        }
    }

    // Función para verificar si el usuario ya existe
    private fun checkIfUserExists(email: String): Boolean {
        val usersJson = sharedPreferences.getString("users", "[]")
        val usersArray = JSONArray(usersJson)

        for (i in 0 until usersArray.length()) {
            val user = usersArray.getJSONObject(i)
            if (user.getString("email") == email) {
                return true
            }
        }
        return false
    }

    // Función para registrar usuarios de Google
    private fun registerGoogleUser(name: String, lastName: String, email: String,
                                   phone: Long, password: String, rol: String) {
        try {
            val usersJson = sharedPreferences.getString("users", "[]")
            val usersArray = JSONArray(usersJson)
            val newUsersArray = JSONArray()

            // Copiar los usuarios existentes
            for (i in 0 until usersArray.length()) {
                newUsersArray.put(usersArray.getJSONObject(i))
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

            Log.d(TAG, "Registro automático exitoso para $email")

        } catch (e: Exception) {
            Log.e(TAG, "Error en registerGoogleUser", e)
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
        if (etUsername.text.toString().trim().isEmpty()){
            Toast.makeText(this,"El campo username es requerido", Toast.LENGTH_SHORT).show()
            return false
        } else if ( etPassword.text.toString().trim().isEmpty() ){
            Toast.makeText(this,"El campo password es requerido", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!etUsername.text.toString().trim().matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"))) {
            Toast.makeText(this, "El correo electrónico no es válido", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }
    private fun validateData(): Boolean {
        val email = etUsername.text.toString().trim()
        val password = etPassword.text.toString().trim()

        try {
            val usersJson = sharedPreferences.getString("users", "[]")
            val usersArray = JSONArray(usersJson)

            for (i in 0 until usersArray.length()) {
                val user = usersArray.getJSONObject(i)
                if (user.getString("email") == email &&
                    user.getString("password") == password) {

                    // Si los datos son correctos, guardamos al usuario activo
                    val editor = sharedPreferences.edit()
                    editor.putString("activeUser", user.toString())
                    editor.apply()

                    return true
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        Toast.makeText(this, "Los Datos son Incorrectos", Toast.LENGTH_SHORT).show()
        return false
    }

    private fun redirection(destinationActivity:Class<*>){
        val intent= Intent(this, destinationActivity)
        startActivity(intent)
    }

}