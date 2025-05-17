package com.example.green_roots.fragments

import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.green_roots.R
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream

class EditarPerfilFragment : Fragment() {

    private lateinit var etName: EditText
    private lateinit var etLastName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPhone: EditText
    private lateinit var buttonEdit: Button
    private lateinit var buttonChangePhoto: Button
    private lateinit var textGoBack: TextView
    private lateinit var imageViewProfile: ImageView

    private lateinit var sharedPreferences: SharedPreferences

    // Variables para manejo de imágenes
    private var imagenUri: Uri? = null
    private var imagenBitmap: Bitmap? = null
    private lateinit var galeriaLauncher: ActivityResultLauncher<String>
    private lateinit var camaraLauncher: ActivityResultLauncher<Intent>

    // Email del usuario actual para identificar su imagen
    private var currentUserEmail: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = requireActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE)

        // Inicializar lanzadores de actividad
        inicializarLanzadores()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)

        etName = view.findViewById(R.id.etNameProfile)
        etLastName = view.findViewById(R.id.etLastNameProfile)
        etEmail = view.findViewById(R.id.etEmailProfile)
        etPhone = view.findViewById(R.id.etPhoneProfile)
        buttonEdit = view.findViewById(R.id.buttonEditProfile)
        buttonChangePhoto = view.findViewById(R.id.buttonChangePhoto)
        textGoBack = view.findViewById(R.id.textGoBackProfile)
        imageViewProfile = view.findViewById(R.id.imageProfile)

        loadData()

        buttonEdit.setOnClickListener {
            if (validateFields()) {
                editData()
                findNavController().navigate(R.id.PerfilFragment)
            }
        }

        buttonChangePhoto.setOnClickListener {
            mostrarOpcionesImagen()
        }

        textGoBack.setOnClickListener {
            findNavController().navigate(R.id.PerfilFragment)
        }

        return view
    }

    private fun inicializarLanzadores() {
        // Lanzador para la galería
        galeriaLauncher = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { result ->
            result?.let {
                imagenUri = it
                cargarImagenDesdeUri()
                guardarImagenEnUsuario()
            }
        }

        // Lanzador para la cámara
        camaraLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val extras = result.data?.extras
                imagenBitmap = extras?.get("data") as? Bitmap
                imagenBitmap?.let {
                    imageViewProfile.setImageBitmap(it)
                    guardarImagenEnUsuario()
                }
            }
        }
    }

    private fun mostrarOpcionesImagen() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Seleccionar imagen")

            val opciones = arrayOf("Tomar foto", "Seleccionar de galería")
            setItems(opciones) { _, which ->
                when (which) {
                    0 -> abrirCamara()
                    1 -> abrirGaleria()
                }
            }
            show()
        }
    }

    private fun abrirGaleria() {
        galeriaLauncher.launch("image/*")
    }

    private fun abrirCamara() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            camaraLauncher.launch(intent)
        } else {
            Toast.makeText(requireContext(), "No hay aplicación de cámara disponible", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cargarImagenDesdeUri() {
        try {
            imagenUri?.let {
                imagenBitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, it)
                imageViewProfile.setImageBitmap(imagenBitmap)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error al cargar la imagen", Toast.LENGTH_SHORT).show()
        }
    }

    private fun guardarImagenEnUsuario() {
        if (imagenBitmap == null || currentUserEmail.isEmpty()) {
            Toast.makeText(requireContext(), "No se pudo guardar la imagen", Toast.LENGTH_SHORT).show()
            return
        }

        // Convertir Bitmap a String Base64
        val imagenBase64 = convertirBitmapABase64(imagenBitmap!!)

        try {
            // 1. Obtener la lista actual de usuarios
            val usersJson = sharedPreferences.getString("users", "[]")
            val usersArray = JSONArray(usersJson)
            val updatedUsersArray = JSONArray()

            // 2. Buscar y actualizar el usuario actual
            for (i in 0 until usersArray.length()) {
                val user = usersArray.getJSONObject(i)

                if (user.getString("email") == currentUserEmail) {
                    // Clonar el usuario y añadir/actualizar la imagen
                    val updatedUser = JSONObject(user.toString())
                    updatedUser.put("profileImage", imagenBase64)
                    updatedUsersArray.put(updatedUser)
                } else {
                    updatedUsersArray.put(user)
                }
            }

            // 3. Guardar la lista actualizada
            sharedPreferences.edit().apply {
                putString("users", updatedUsersArray.toString())

                // Actualizar también el activeUser si es el mismo
                val activeUserJson = sharedPreferences.getString("activeUser", null)
                activeUserJson?.let {
                    val activeUser = JSONObject(it)
                    if (activeUser.getString("email") == currentUserEmail) {
                        activeUser.put("profileImage", imagenBase64)
                        putString("activeUser", activeUser.toString())
                    }
                }
                apply()
            }

            Toast.makeText(requireContext(), "Foto de perfil actualizada", Toast.LENGTH_SHORT).show()

        } catch (e: JSONException) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error al guardar la imagen", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cargarImagenDeUsuario() {
        try {
            val activeUserJson = sharedPreferences.getString("activeUser", null)
            activeUserJson?.let {
                val activeUser = JSONObject(it)
                if (activeUser.has("profileImage")) {
                    val imagenBase64 = activeUser.getString("profileImage")
                    imagenBitmap = convertirBase64ABitmap(imagenBase64)
                    imagenBitmap?.let { bitmap ->
                        imageViewProfile.setImageBitmap(bitmap)
                    }
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun convertirBitmapABase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun convertirBase64ABitmap(base64String: String): Bitmap? {
        val decodedString = Base64.decode(base64String, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }

    private fun validateFields(): Boolean {
        fun isFieldEmpty(field: EditText, errorMessage: String): Boolean {
            if (field.text.toString().trim().isEmpty()) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                return true
            }
            return false
        }

        if (isFieldEmpty(etName, "El campo nombre es requerido")) return false
        if (isFieldEmpty(etLastName, "El campo apellido es requerido")) return false
        if (isFieldEmpty(etEmail, "El campo email es requerido")) return false
        if (isFieldEmpty(etPhone, "El campo teléfono es requerido")) return false

        if (etPhone.text.toString().trim().length < 10) {
            Toast.makeText(requireContext(), "El número de teléfono debe tener al menos 10 dígitos", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!etEmail.text.toString().trim().matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"))) {
            Toast.makeText(requireContext(), "El correo electrónico no es válido", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun editData() {
        try {
            // 1. Obtener datos del formulario
            val name = etName.text.toString().trim()
            val lastName = etLastName.text.toString().trim()
            val phone = etPhone.text.toString().trim().toLong()
            val email = etEmail.text.toString().trim()

            // 2. Obtener el usuario activo actual
            val activeUserJson = sharedPreferences.getString("activeUser", null)
                ?.let { JSONObject(it) } ?: run {
                Toast.makeText(requireContext(), "No hay usuario activo", Toast.LENGTH_SHORT).show()
                return
            }

            val currentEmail = activeUserJson.getString("email")
            val profileImage = if (activeUserJson.has("profileImage")) {
                activeUserJson.getString("profileImage")
            } else {
                ""
            }

            // 3. Obtener y procesar la lista de usuarios
            val usersJson = sharedPreferences.getString("users", "[]")
            val usersArray = JSONArray(usersJson)
            val updatedUsersArray = JSONArray()

            var userFound = false
            var updatedUser = JSONObject()

            // 4. Buscar y actualizar el usuario en la lista
            for (i in 0 until usersArray.length()) {
                val user = usersArray.getJSONObject(i)

                if (user.getString("email") == currentEmail) {
                    // Crear usuario actualizado
                    updatedUser = JSONObject().apply {
                        put("name", name)
                        put("lastName", lastName)
                        put("phone", phone)
                        put("email", email)
                        put("password", user.getString("password"))
                        put("rol", user.getString("rol"))
                        // Mantener la imagen de perfil si existe
                        if (user.has("profileImage")) {
                            put("profileImage", user.getString("profileImage"))
                        } else if (profileImage.isNotEmpty()) {
                            put("profileImage", profileImage)
                        }
                    }
                    updatedUsersArray.put(updatedUser)
                    userFound = true
                } else {
                    updatedUsersArray.put(user)
                }
            }

            if (!userFound) {
                Toast.makeText(requireContext(), "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                return
            }

            // 5. Guardar todos los cambios
            sharedPreferences.edit().apply {
                putString("users", updatedUsersArray.toString())
                putString("activeUser", updatedUser.toString())

                if (currentEmail != email) {
                    putString("activeUserEmail", email)
                }
            }.apply()

            Toast.makeText(requireContext(), "Datos actualizados correctamente", Toast.LENGTH_SHORT).show()

        } catch (e: NumberFormatException) {
            Toast.makeText(requireContext(), "Número de teléfono inválido", Toast.LENGTH_SHORT).show()
        } catch (e: JSONException) {
            Toast.makeText(requireContext(), "Error al procesar los datos", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun loadData() {
        val activeUserJson = sharedPreferences.getString("activeUser", null)

        if (activeUserJson != null) {
            val activeUser = JSONObject(activeUserJson)
            currentUserEmail = activeUser.getString("email")

            etName.setText(activeUser.getString("name"))
            etLastName.setText(activeUser.getString("lastName"))
            etPhone.setText(activeUser.getLong("phone").toString())
            etEmail.setText(activeUser.getString("email"))

            // Cargar imagen del usuario
            cargarImagenDeUsuario()
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

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("Lifecycle", "onDestroyView llamado")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Lifecycle", "onDestroy llamado")
    }
}