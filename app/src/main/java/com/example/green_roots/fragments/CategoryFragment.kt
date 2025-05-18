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
import com.example.green_roots.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONArray
import org.json.JSONObject

class CategoryFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var containerLayout: LinearLayout
    private lateinit var tvEmptyCategories: TextView
    private lateinit var btnAddCategory: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_category, container, false)

        sharedPreferences = requireActivity().getSharedPreferences("Category", Context.MODE_PRIVATE)
        containerLayout = view.findViewById(R.id.cartItemsContainer)
        tvEmptyCategories = view.findViewById(R.id.tvEmptyCategories)
        btnAddCategory = view.findViewById(R.id.bt_add)

        btnAddCategory.setOnClickListener {
            showAddCategoryDialog()
        }

        loadCategories()

        return view
    }

    private fun showAddCategoryDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_add_category, null)

        val etName = dialogView.findViewById<EditText>(R.id.etCategoryName)
        val etDescription = dialogView.findViewById<EditText>(R.id.etCategoryDescription)
        val btnSave = dialogView.findViewById<Button>(R.id.buttonSaveCategory)

        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        btnSave.setOnClickListener {
            val name = etName.text.toString()
            val description = etDescription.text.toString()

            if (name.isNotEmpty() && description.isNotEmpty()) {
                if (!isCategoryExists(name)) {
                    saveCategory(name, description)
                    dialog.dismiss()
                } else {
                    Toast.makeText(requireContext(), "La categoría ya existe", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun isCategoryExists(name: String): Boolean {
        val categoriesJson = sharedPreferences.getString("categories", "[]")
        val categoriesArray = JSONArray(categoriesJson)

        for (i in 0 until categoriesArray.length()) {
            val category = categoriesArray.getJSONObject(i)
            if (category.getString("name").equals(name, ignoreCase = true)) {
                return true
            }
        }
        return false
    }

    private fun saveCategory(name: String, description: String) {
        val categoriesJson = sharedPreferences.getString("categories", "[]")
        val categoriesArray = JSONArray(categoriesJson)
        
        val newCategory = JSONObject().apply {
            put("name", name)
            put("description", description)
        }

        categoriesArray.put(newCategory)

        sharedPreferences.edit()
            .putString("categories", categoriesArray.toString())
            .apply()

        loadCategories()
        Toast.makeText(requireContext(), "Categoría guardada exitosamente", Toast.LENGTH_SHORT).show()
    }

    private fun loadCategories() {
        containerLayout.removeAllViews()

        val categoriesJson = sharedPreferences.getString("categories", "[]")
        val categoriesArray = JSONArray(categoriesJson)

        if (categoriesArray.length() == 0) {
            tvEmptyCategories.visibility = View.VISIBLE
            return
        }

        tvEmptyCategories.visibility = View.GONE

        for (i in 0 until categoriesArray.length()) {
            val category = categoriesArray.getJSONObject(i)
            val name = category.getString("name")
            val description = category.getString("description")

            addCategoryItemView(name, description)
            addDivider()
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

    private fun addCategoryItemView(name: String, description: String) {
        val itemView = LayoutInflater.from(requireContext())
            .inflate(R.layout.item_category, containerLayout, false)

        itemView.findViewById<TextView>(R.id.txtCategoryName).text = name
        itemView.findViewById<TextView>(R.id.txtCategoryDescription).text = description

        itemView.findViewById<ImageView>(R.id.btnEditCategory).setOnClickListener {
            showEditCategoryDialog(name)
        }

        itemView.findViewById<ImageView>(R.id.btnDeleteCategory).setOnClickListener {
            showDeleteConfirmationDialog(name)
        }

        containerLayout.addView(itemView)
    }

    private fun showEditCategoryDialog(categoryName: String) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_edit_category, null)

        val categoriesJson = sharedPreferences.getString("categories", "[]")
        val categoriesArray = JSONArray(categoriesJson)
        var currentCategory: JSONObject? = null

        for (i in 0 until categoriesArray.length()) {
            val category = categoriesArray.getJSONObject(i)
            if (category.getString("name") == categoryName) {
                currentCategory = category
                break
            }
        }

        if (currentCategory == null) return

        val etName = dialogView.findViewById<EditText>(R.id.etEditCategoryName)
        val etDescription = dialogView.findViewById<EditText>(R.id.etEditCategoryDescription)
        val btnSave = dialogView.findViewById<Button>(R.id.buttonEditCategory)

        etName.setText(currentCategory.getString("name"))
        etDescription.setText(currentCategory.getString("description"))

        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        btnSave.setOnClickListener {
            val newName = etName.text.toString()
            val newDescription = etDescription.text.toString()

            if (newName.isNotEmpty() && newDescription.isNotEmpty()) {
                if (newName != categoryName && isCategoryExists(newName)) {
                    Toast.makeText(requireContext(), "La categoría ya existe", Toast.LENGTH_SHORT).show()
                } else {
                    updateCategory(categoryName, newName, newDescription)
                    dialog.dismiss()
                }
            } else {
                Toast.makeText(requireContext(), "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun showDeleteConfirmationDialog(name: String) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_delete_category, null)

        val tvMessage = dialogView.findViewById<TextView>(R.id.tvDeleteMessage)
        val btnConfirm = dialogView.findViewById<Button>(R.id.btnConfirmDelete)

        tvMessage.text = "¿Está seguro que desea eliminar la categoría $name?"

        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        btnConfirm.setOnClickListener {
            removeCategory(name)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun updateCategory(oldName: String, newName: String, newDescription: String) {
        val categoriesJson = sharedPreferences.getString("categories", "[]")
        val categoriesArray = JSONArray(categoriesJson)
        val newCategoriesArray = JSONArray()

        for (i in 0 until categoriesArray.length()) {
            val category = categoriesArray.getJSONObject(i)
            if (category.getString("name") == oldName) {
                val updatedCategory = JSONObject().apply {
                    put("name", newName)
                    put("description", newDescription)
                }
                newCategoriesArray.put(updatedCategory)
            } else {
                newCategoriesArray.put(category)
            }
        }

        sharedPreferences.edit()
            .putString("categories", newCategoriesArray.toString())
            .apply()

        loadCategories()
        Toast.makeText(requireContext(), "Categoría actualizada exitosamente", Toast.LENGTH_SHORT).show()
    }

    private fun removeCategory(name: String) {
        val categoriesJson = sharedPreferences.getString("categories", "[]")
        val categoriesArray = JSONArray(categoriesJson)
        val newCategoriesArray = JSONArray()

        for (i in 0 until categoriesArray.length()) {
            val category = categoriesArray.getJSONObject(i)
            if (category.getString("name") != name) {
                newCategoriesArray.put(category)
            }
        }

        sharedPreferences.edit()
            .putString("categories", newCategoriesArray.toString())
            .apply()

        loadCategories()
        Toast.makeText(requireContext(), "Categoría eliminada exitosamente", Toast.LENGTH_SHORT).show()
    }
}