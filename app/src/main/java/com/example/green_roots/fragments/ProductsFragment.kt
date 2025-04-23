package com.example.green_roots.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.green_roots.R
import com.example.green_roots.adapter.ProductAdapter
import com.example.green_roots.model.Product

class ProductsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductAdapter
    private lateinit var productList: List<Product>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_products, container, false)

        recyclerView = view.findViewById(R.id.recyclerProducts)
        recyclerView.layoutManager = GridLayoutManager(context, 2)

        productList = listOf(
            Product("Cepillo", 10.000, R.drawable.im_cepillo),
            Product("Camisa", 10.000, R.drawable.im_camisa),
            Product("Utensilios", 10.000, R.drawable.im_utensilios)
        )

        adapter = ProductAdapter(productList)
        recyclerView.adapter = adapter

        return view
    }
}
