package com.example.green_roots.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.green_roots.R
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale

class CartFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var containerLayout: LinearLayout
    private lateinit var tvTotal: TextView
    private lateinit var btnCheckout: Button
    private lateinit var tvEmptyCart: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_cart, container, false)

        sharedPreferences = requireActivity().getSharedPreferences("ShoppingCart", Context.MODE_PRIVATE)
        containerLayout = view.findViewById(R.id.cartItemsContainer)
        tvTotal = view.findViewById(R.id.tvTotal)
        btnCheckout = view.findViewById(R.id.btnCheckout)
        tvEmptyCart = view.findViewById(R.id.tvEmptyCart)

        loadProduct("Cepillo para cabello", 1, 15000)
        loadProduct("Utensilios de cocina", 2, 12000)

        btnCheckout.setOnClickListener {
            findNavController().navigate(R.id.BuyFragment)
        }

        loadCartItems()

        return view
    }

    private fun loadCartItems() {
        containerLayout.removeAllViews()

        val productsJson = sharedPreferences.getString("products", "[]")
        val productsArray = JSONArray(productsJson)

        if (productsArray.length() == 0) {
            tvEmptyCart.visibility = View.VISIBLE
            tvTotal.visibility = View.GONE
            btnCheckout.visibility = View.GONE
            return
        }

        tvEmptyCart.visibility = View.GONE
        tvTotal.visibility = View.VISIBLE
        btnCheckout.visibility = View.VISIBLE

        var total = 0

        for (i in 0 until productsArray.length()) {
            val product = productsArray.getJSONObject(i)
            val name = product.getString("name")
            var amount = product.getInt("amount")
            val price = product.getInt("price")

            total += price * amount

            addCartItemView(name, amount, price)
        }

        updateTotal(total)
    }

    private fun addCartItemView(name: String, amount: Int, price: Int) {
        val itemView = LayoutInflater.from(requireContext())
            .inflate(R.layout.item_cart_product, containerLayout, false)

        itemView.findViewById<TextView>(R.id.tvProductName).text = name
        itemView.findViewById<TextView>(R.id.tvProductPrice).text = formatPrice(price * amount)

        val tvAmount = itemView.findViewById<TextView>(R.id.tvProductAmount)
        tvAmount.text = amount.toString()

        itemView.findViewById<Button>(R.id.btnDecrease).setOnClickListener {
            val currentAmount = tvAmount.text.toString().toInt()
            if (currentAmount > 1) {
                val newAmount = currentAmount - 1
                tvAmount.text = newAmount.toString()
                itemView.findViewById<TextView>(R.id.tvProductPrice).text = formatPrice(price * newAmount)
                updateProductAmount(name, newAmount)
            } else {
                removeProduct(name)
            }
        }

        itemView.findViewById<Button>(R.id.btnIncrease).setOnClickListener {
            val newAmount = tvAmount.text.toString().toInt() + 1
            tvAmount.text = newAmount.toString()
            itemView.findViewById<TextView>(R.id.tvProductPrice).text = formatPrice(price * newAmount)
            updateProductAmount(name, newAmount)
        }

        containerLayout.addView(itemView)
    }

    private fun updateProductAmount(name: String, newAmount: Int) {
        val productsJson = sharedPreferences.getString("products", "[]")
        val productsArray = JSONArray(productsJson)
        val newProductsArray = JSONArray()

        for (i in 0 until productsArray.length()) {
            val product = productsArray.getJSONObject(i)
            if (product.getString("name") == name) {
                if (newAmount > 0) {
                    product.put("amount", newAmount)
                    newProductsArray.put(product)
                }
            } else {
                newProductsArray.put(product)
            }
        }

        sharedPreferences.edit()
            .putString("products", newProductsArray.toString())
            .apply()

        loadCartItems()
    }

    private fun removeProduct(name: String) {
        updateProductAmount(name, 0)
    }

    private fun updateTotal(total: Int) {
        tvTotal.text = "Total: ${formatPrice(total)}"
    }


    private fun formatPrice(price: Int): String {
        return "$${String.format(Locale.US, "%,d", price)}"
    }

    //Funcion para cargar algunos productos
    private fun loadProduct(name: String, amount: Int, price: Int){
        val control= sharedPreferences.edit()

        val productsJson = sharedPreferences.getString("products", "[]")

        // Convertimos el string JSON a un JSONArray
        val productsArray = JSONArray(productsJson)

        // veficacion de producto existente
        var productExists = false

        // Recorremos los productos existentes para buscar coincidencias
        for (i in 0 until productsArray.length()) {
            val currentProduct = productsArray.getJSONObject(i)

            // Si encontramos un producto con el mismo nombre
            if (currentProduct.getString("name") == name) {
                // Aumentamos la cantidad
                val currentAmount = currentProduct.getInt("amount")
                currentProduct.put("amount", currentAmount + amount)

                // Actualizamos el producto en el array
                productsArray.put(i, currentProduct)

                productExists = true
                break
            }
        }

        // Si el producto no existe, lo añadimos como nuevo
        if (!productExists) {
            // Creamos un objeto JSON para el nuevo producto
            val productObject = JSONObject().apply {
                put("name", name)
                put("amount", amount)
                put("price", price)
            }

            // Agregamos el producto al array
            productsArray.put(productObject)
        }


        control.putString("products", productsArray.toString())
        control.apply()
    }


}