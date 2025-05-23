package com.example.green_roots.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.green_roots.R
import com.example.green_roots.adapter.ProductAdapter
import com.example.green_roots.model.Product
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONArray
import org.json.JSONObject

class ProductsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductAdapter
    private lateinit var sharedPrefs: SharedPreferences
    private val productList = mutableListOf<Product>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_products, container, false)
        sharedPrefs = requireContext().getSharedPreferences("ProductData", Context.MODE_PRIVATE)

        recyclerView = view.findViewById(R.id.recyclerProducts)
        recyclerView.layoutManager = LinearLayoutManager(context)

        loadProducts()

        adapter = ProductAdapter(requireContext(), productList)
        recyclerView.adapter = adapter

        val sharedPreferences = requireActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val activeUserJson = sharedPreferences.getString("activeUser", null)

        if (activeUserJson != null) {
            val activeUser = JSONObject(activeUserJson)
            val rol = activeUser.getString("rol")

            when (rol) {
                "admin" -> {
                    // Aquí mostramos las opciones del administrador
                    view.findViewById<FloatingActionButton>(R.id.bt_add).visibility = View.VISIBLE // Opcion para hacerlo visible
                }
                "cliente" -> {
                    view.findViewById<FloatingActionButton>(R.id.bt_add).visibility = View.GONE
                }
                "vendedor" -> {
                    // Aquí mostramos las opciones del vendedor
                    view.findViewById<FloatingActionButton>(R.id.bt_add).visibility = View.VISIBLE // Opcion para hacerlo visible
                }
                else -> {
                    Toast.makeText(requireContext(), "Rol desconocido", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(requireContext(), "No hay usuario activo", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<FloatingActionButton>(R.id.bt_add).setOnClickListener {
            findNavController().navigate(R.id.AddProductFragment)
        }

        return view
    }

    private fun loadProducts() {
        productList.clear() // Limpia la lista antes de llenarla

        val json = sharedPrefs.getString("products", "[]") ?: "[]"
        val arr = JSONArray(json)

        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            val name = obj.getString("name")
            val price = obj.getInt("price")
            val imageUri = obj.optString("imageUri", "")
            val description = obj.optString("description", "")
            val reason = obj.optString("reason", "")
            val category = obj.optString("category", "")
            val seller = obj.optString("seller", "")

            val product = Product(
                name = name,
                price = price,
                imageResId = R.drawable.ic_default,
                imageUri = imageUri,
                description = description,
                reason = reason,
                category = category,
                seller = seller
            )
            productList.add(product)
        }
    }
}
