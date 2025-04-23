package com.example.green_roots.fragments

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.green_roots.R
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class EditarPerfilFragment : Fragment() {

    private lateinit var etName: EditText
    private lateinit var etLastName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPhone: EditText
    private lateinit var buttonEdit: Button
    private lateinit var buttonChangePhoto: Button
    private lateinit var textGoBack: TextView

    private lateinit var sharedPreferences: SharedPreferences

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

        sharedPreferences = requireActivity().getSharedPreferences("UserData", android.content.Context.MODE_PRIVATE)

        loadData()

        buttonEdit.setOnClickListener {
            if (validateFields()) {
                editData()
                findNavController().navigate(R.id.PerfilFragment)
            }
        }

        buttonChangePhoto.setOnClickListener {
            Toast.makeText(requireContext(), "Esta funcionalidad todavia no esta implementada", Toast.LENGTH_SHORT).show()
        }

        textGoBack.setOnClickListener {
            findNavController().navigate(R.id.PerfilFragment)
        }

        return view
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
                    }
                    updatedUsersArray.put(updatedUser)
                    userFound = true
                } else {
                    updatedUsersArray.put(user) // Mantener otros usuarios sin cambios
                }
            }

            if (!userFound) {
                Toast.makeText(requireContext(), "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                return
            }

            // 5. Guardar todos los cambios
            sharedPreferences.edit().apply {
                putString("users", updatedUsersArray.toString())

                // Actualizar el activeUser con los nuevos datos
                putString("activeUser", updatedUser.toString())

                // Si cambió el email, actualizar la referencia
                if (currentEmail != email) {
                    putString("activeUserEmail", email) // Opcional: guardar referencia adicional
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

            etName.setText(activeUser.getString("name"))
            etLastName.setText(activeUser.getString("lastName"))
            etPhone.setText(activeUser.getLong("phone").toString())
            etEmail.setText(activeUser.getString("email"))
        }
    }

    private fun redirection(destinationActivity: Class<*>) {
        val intent = Intent(requireActivity(), destinationActivity)
        startActivity(intent)
        requireActivity().finish()
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