package com.example.green_roots.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.green_roots.R
import com.example.green_roots.adapter.ProductAdapter
import com.example.green_roots.model.Product
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ProductsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductAdapter
    private lateinit var productList: MutableList<Product>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_products, container, false)

        recyclerView = view.findViewById(R.id.recyclerProducts)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Lista inicial de productos
        productList = mutableListOf(
            Product("Cepillo", 10000, R.drawable.im_cepillo),
            Product("Camisa", 10000, R.drawable.im_camisa),
            Product("Utensilios", 10000, R.drawable.im_utensilios),
            Product("Sombras", 10000, R.drawable.im_sombras)
        )

        val args = arguments
        val titulo = args?.getString("titulo")
        val priceDouble = args?.getDouble("precio")
        val nuevoProductoImg = R.drawable.ic_default

        // Si título y precio existen, agrega nuevo producto a la lista
        if (titulo != null && priceDouble != null) {

            val precioInt = priceDouble.toInt()
            val nuevoProducto = Product(titulo, precioInt, nuevoProductoImg)
            productList.add(nuevoProducto)
        }

        adapter = ProductAdapter(requireContext(), productList)
        recyclerView.adapter = adapter

        val fabAdd = view.findViewById<FloatingActionButton>(R.id.bt_add)
        fabAdd.setOnClickListener {
            findNavController().navigate(R.id.AddProductFragment)
        }

        return view
    }

}

