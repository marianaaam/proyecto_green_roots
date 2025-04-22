package com.example.green_roots.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.green_roots.R
import org.json.JSONObject

class PerfilFragment : Fragment() {

    private lateinit var tvName: TextView
    private lateinit var tvLastName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvPhone: TextView
    private lateinit var buttonEdit: Button

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

        sharedPreferences = requireContext().getSharedPreferences("UserData", Context.MODE_PRIVATE)

        loadData()

        buttonEdit.setOnClickListener {
            findNavController().navigate(R.id.EditarPerfilFragment)
        }

        return view
    }

    private fun loadData(){

        val activeUserJson = sharedPreferences.getString("activeUser", null)

        if (activeUserJson != null) {
            val activeUser = JSONObject(activeUserJson)

            tvName.setText(activeUser.getString("name"))
            tvLastName.setText(activeUser.getString("lastName"))
            tvPhone.setText(activeUser.getLong("phone").toString())
            tvEmail.setText(activeUser.getString("email"))
        }

    }
}