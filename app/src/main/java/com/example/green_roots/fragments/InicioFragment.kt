package com.example.green_roots.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.green_roots.R
import org.json.JSONObject

class InicioFragment : Fragment() {

    private lateinit var textWelcomeAdmin: TextView
    private lateinit var textWelcomeClient: TextView
    private lateinit var textWelcomeSeller: TextView
    private lateinit var buttonSeeProducts: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_inicio, container, false)

        textWelcomeAdmin= view.findViewById(R.id.tvWelcomeAdmin)
        textWelcomeClient= view.findViewById(R.id.tvWelcomeClient)
        textWelcomeSeller= view.findViewById(R.id.tvWelcomeSeller)


        // Accedemos al SharedPreferences
        val sharedPreferences = requireActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val activeUserJson = sharedPreferences.getString("activeUser", null)

        if (activeUserJson != null) {
            val activeUser = JSONObject(activeUserJson)
            val rol = activeUser.getString("rol")

            when (rol) {
                "admin" -> {
                    Toast.makeText(requireContext(), "Bienvenido Admin", Toast.LENGTH_SHORT).show()
                    textWelcomeClient.visibility = View.GONE  // Opcion para ocultar contenido
                    textWelcomeSeller.visibility = View.GONE
                    textWelcomeAdmin.visibility = View.VISIBLE // Opcion para hacerlo visible
                }
                "cliente" -> {
                    Toast.makeText(requireContext(), "Bienvenido Cliente", Toast.LENGTH_SHORT).show()
                    textWelcomeClient.visibility = View.VISIBLE
                    textWelcomeSeller.visibility = View.GONE
                    textWelcomeAdmin.visibility = View.GONE
                }
                "vendedor" -> {
                    Toast.makeText(requireContext(), "Bienvenido Vendedor", Toast.LENGTH_SHORT).show()
                    textWelcomeClient.visibility = View.GONE  // Opcion para ocultar contenido
                    textWelcomeSeller.visibility = View.VISIBLE
                    textWelcomeAdmin.visibility = View.GONE // Opcion para hacerlo visible
                }
                else -> {
                    Toast.makeText(requireContext(), "Rol desconocido", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(requireContext(), "No hay usuario activo", Toast.LENGTH_SHORT).show()
        }


        return view
    }
}
