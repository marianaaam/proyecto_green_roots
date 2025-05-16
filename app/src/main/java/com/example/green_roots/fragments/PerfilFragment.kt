package com.example.green_roots.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.green_roots.R
import org.json.JSONException
import org.json.JSONObject

class PerfilFragment : Fragment() {

    private lateinit var tvName: TextView
    private lateinit var tvLastName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvPhone: TextView
    private lateinit var buttonEdit: Button
    private lateinit var imageViewProfile: ImageView
    private var imagenBitmap: Bitmap? = null

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        tvName = view.findViewById(R.id.tvNameProfile)
        tvLastName = view.findViewById(R.id.tvLastNameProfile)
        tvEmail = view.findViewById(R.id.tvEmailProfile)
        tvPhone = view.findViewById(R.id.tvPhoneProfile)
        buttonEdit = view.findViewById(R.id.buttonEditProfileAc)
        imageViewProfile = view.findViewById(R.id.imageProfile)

        sharedPreferences = requireContext().getSharedPreferences("UserData", Context.MODE_PRIVATE)

        loadData()

        buttonEdit.setOnClickListener {
            findNavController().navigate(R.id.EditarPerfilFragment)
        }

        return view
    }

    private fun loadData() {
        try {
            val activeUserJson = sharedPreferences.getString("activeUser", null)

            if (activeUserJson != null) {
                val activeUser = JSONObject(activeUserJson)

                // Cargar datos básicos
                tvName.text = activeUser.getString("name")
                tvLastName.text = activeUser.getString("lastName")
                tvPhone.text = activeUser.getLong("phone").toString()
                tvEmail.text = activeUser.getString("email")

                // Cargar imagen si existe
                if (activeUser.has("profileImage")) {
                    val imagenBase64 = activeUser.getString("profileImage")
                    if (imagenBase64.isNotEmpty()) {
                        val bitmap = convertirBase64ABitmap(imagenBase64)
                        bitmap?.let {
                            imageViewProfile.setImageBitmap(it)
                        } ?: run {
                            // Mostrar imagen por defecto si la conversión falla
                            imageViewProfile.setImageResource(R.drawable.icon_user)
                        }
                    } else {
                        // Imagen vacía - mostrar placeholder
                        imageViewProfile.setImageResource(R.drawable.icon_user)
                    }
                } else {
                    // No existe campo profileImage - mostrar placeholder
                    imageViewProfile.setImageResource(R.drawable.icon_user)
                }
            }
        } catch (e: JSONException) {
            Toast.makeText(requireContext(), "Error al cargar los datos", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun convertirBase64ABitmap(base64String: String): Bitmap? {
        return try {
            val decodedString = Base64.decode(base64String, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}