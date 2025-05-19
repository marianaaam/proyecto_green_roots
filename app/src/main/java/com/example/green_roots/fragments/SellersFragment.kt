package com.example.green_roots.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.green_roots.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONArray
import org.json.JSONObject

class SellersFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var containerLayout: LinearLayout
    private lateinit var tvEmptySellers: TextView
    private lateinit var btnAddSeller: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_sellers, container, false)

        sharedPreferences = requireActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE)
        containerLayout = view.findViewById(R.id.cartItemsContainer)
        tvEmptySellers = view.findViewById(R.id.tvEmptySellers)
        btnAddSeller = view.findViewById(R.id.bt_add)

        btnAddSeller.setOnClickListener {
            showAddSellerDialog()
        }

        loadSellers()

        return view
    }

    private fun showAddSellerDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_add_seller, null)

        val etName = dialogView.findViewById<EditText>(R.id.etNameSeller)
        val etLastName = dialogView.findViewById<EditText>(R.id.etLastNameSeller)
        val etEmail = dialogView.findViewById<EditText>(R.id.etEmailSeller)
        val etPhone = dialogView.findViewById<EditText>(R.id.etPhoneSeller)
        val etPassword = dialogView.findViewById<EditText>(R.id.etPasswordSeller)
        val btnGuardar = dialogView.findViewById<Button>(R.id.buttonRegister)

        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        btnGuardar.setOnClickListener {
            val name = etName.text.toString()
            val lastName = etLastName.text.toString()
            val email = etEmail.text.toString()
            val phone = etPhone.text.toString()
            val password = etPassword.text.toString()

            if (name.isNotEmpty() && lastName.isNotEmpty() && email.isNotEmpty() && phone.isNotEmpty() && password.isNotEmpty()) {
                if (!isEmailExists(email)) {
                    saveSeller(name, lastName, email, phone, password)
                    dialog.dismiss()
                } else {
                    Toast.makeText(requireContext(), "El correo ya está registrado", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun isEmailExists(email: String): Boolean {
        val sellersJson = sharedPreferences.getString("users", "[]")
        val sellersArray = JSONArray(sellersJson)

        for (i in 0 until sellersArray.length()) {
            val seller = sellersArray.getJSONObject(i)
            if (seller.getString("email") == email) {
                return true
            }
        }
        return false
    }

    private fun saveSeller(id: String, name: String, email: String, phone: String, password : String) {
        val sellersJson = sharedPreferences.getString("users", "[]")
        val sellersArray = JSONArray(sellersJson)
        
        val newSeller = JSONObject().apply {
            put("name", id)
            put("lastName", name)
            put("email", email)
            put("phone", phone)
            put("password", password)
            put("rol", "vendedor")
        }

        sellersArray.put(newSeller)

        sharedPreferences.edit()
            .putString("users", sellersArray.toString())
            .apply()

        loadSellers()
    }

    private fun loadSellers() {
        containerLayout.removeAllViews()

        val sellersJson = sharedPreferences.getString("users", "[]")
        val sellersArray = JSONArray(sellersJson)

        tvEmptySellers.visibility = View.GONE
        var numSellers = 0

        for (i in 0 until sellersArray.length()) {
            val seller = sellersArray.getJSONObject(i)

            if(seller.getString("rol").equals("vendedor")){
                val name = seller.getString("name")
                val email = seller.getString("email")
                val phone = seller.getString("phone")

                numSellers++
                addSellerItemView(name, email, phone)
                addDivider()
            }
        }

        if (numSellers == 0) {
            tvEmptySellers.visibility = View.VISIBLE
            return
        }
    }

    private fun addDivider() {
        val dividerView = View(requireContext())
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            1
        )
        params.setMargins(0, 0, 0, 8)
        dividerView.layoutParams = params
        dividerView.setBackgroundResource(R.color.gray)

        containerLayout.addView(dividerView)
    }

    private fun addSellerItemView(name: String, email: String, phone: String) {
        val itemView = LayoutInflater.from(requireContext())
            .inflate(R.layout.item_seller, containerLayout, false)

        itemView.findViewById<TextView>(R.id.txtSellerName).text = name
        itemView.findViewById<TextView>(R.id.txtSellerEmail).text = email
        itemView.findViewById<TextView>(R.id.txtSellerPhone).text = phone

        itemView.findViewById<ImageView>(R.id.btnEditSeller).setOnClickListener {
            showEditSellerDialog(email)
        }

        itemView.findViewById<ImageView>(R.id.btnDeleteSeller).setOnClickListener {
            showDeleteConfirmationDialog(email, name)
        }

        containerLayout.addView(itemView)
    }

    private fun showDeleteConfirmationDialog(email: String, name: String) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_delete_seller, null)

        val tvMessage = dialogView.findViewById<TextView>(R.id.tvDeleteMessage)
        val btnConfirm = dialogView.findViewById<Button>(R.id.btnConfirmDelete)

        tvMessage.text = "¿Está seguro que desea eliminar al vendedor $name?"

        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        btnConfirm.setOnClickListener {
            removeSeller(email)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun removeSeller(email: String) {
        val sellersJson = sharedPreferences.getString("users", "[]")
        val sellersArray = JSONArray(sellersJson)
        val newSellersArray = JSONArray()

        for (i in 0 until sellersArray.length()) {
            val seller = sellersArray.getJSONObject(i)
            if (seller.getString("email") != email) {
                newSellersArray.put(seller)
            }
        }

        sharedPreferences.edit()
            .putString("users", newSellersArray.toString())
            .apply()

        loadSellers()
    }

    private fun showEditSellerDialog(email: String) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_edit_seller, null)

        // Get current seller data
        val sellersJson = sharedPreferences.getString("users", "[]")
        val sellersArray = JSONArray(sellersJson)
        var currentSeller: JSONObject? = null

        for (i in 0 until sellersArray.length()) {
            val seller = sellersArray.getJSONObject(i)
            if (seller.getString("email") == email) {
                currentSeller = seller
                break
            }
        }

        if (currentSeller == null) return

        // Initialize views
        val etName = dialogView.findViewById<EditText>(R.id.etEditNameSeller)
        val etLastName = dialogView.findViewById<EditText>(R.id.etEditLastNameSeller)
        val etEmail = dialogView.findViewById<EditText>(R.id.etEditEmailSeller)
        val etPhone = dialogView.findViewById<EditText>(R.id.etEditPhoneSeller)
        val etPassword = dialogView.findViewById<EditText>(R.id.etEditPasswordSeller)
        val btnEdit = dialogView.findViewById<Button>(R.id.buttonEdit)

        // Set current values
        etName.setText(currentSeller.getString("name"))
        etLastName.setText(currentSeller.getString("lastName"))
        etEmail.setText(currentSeller.getString("email"))
        etPhone.setText(currentSeller.getString("phone"))
        etPassword.setText(currentSeller.getString("password"))

        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        btnEdit.setOnClickListener {
            val newName = etName.text.toString()
            val newLastName = etLastName.text.toString()
            val newEmail = etEmail.text.toString()
            val newPhone = etPhone.text.toString()
            val newPassword = etPassword.text.toString()

            if (newName.isNotEmpty() && newLastName.isNotEmpty() && newEmail.isNotEmpty() && newPhone.isNotEmpty() && newPassword.isNotEmpty()) {
                if (newEmail != email && isEmailExists(newEmail)) {
                    Toast.makeText(requireContext(), "El correo ya está registrado", Toast.LENGTH_SHORT).show()
                } else {
                    updateSeller(email, newName, newLastName, newEmail, newPhone, newPassword)
                    dialog.dismiss()
                }
            } else {
                Toast.makeText(requireContext(), "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun updateSeller(oldEmail: String, name: String, lastName: String, newEmail: String, phone: String, newPassword: String) {
        val sellersJson = sharedPreferences.getString("users", "[]")
        val sellersArray = JSONArray(sellersJson)
        val newSellersArray = JSONArray()

        for (i in 0 until sellersArray.length()) {
            val seller = sellersArray.getJSONObject(i)
            if (seller.getString("email") == oldEmail) {
                val updatedSeller = JSONObject().apply {
                    put("name", name)
                    put("lastName", lastName)
                    put("email", newEmail)
                    put("phone", phone)
                    put("password", newPassword)
                    put("rol", "vendedor")
                }
                newSellersArray.put(updatedSeller)
            } else {
                newSellersArray.put(seller)
            }
        }

        sharedPreferences.edit()
            .putString("users", newSellersArray.toString())
            .apply()

        loadSellers()
        Toast.makeText(requireContext(), "Vendedor actualizado exitosamente", Toast.LENGTH_SHORT).show()
    }
}