package com.example.green_roots.adapter

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.green_roots.R
import com.example.green_roots.model.Product
import org.json.JSONArray
import org.json.JSONObject

class ProductAdapter(
    private val context: Context,
    private val productList: MutableList<Product>
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProduct: ImageView = itemView.findViewById(R.id.imgProduct)
        val txtName: TextView = itemView.findViewById(R.id.txtName)
        val txtPrice: TextView = itemView.findViewById(R.id.txtPrice)
        val btnCarrito: ImageView = itemView.findViewById(R.id.btnCarrito)
        val btnEdit: ImageView = itemView.findViewById(R.id.btnEdit)
        val btnDelete: ImageView = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]

        if (product.imageUri.isNotEmpty()) {
            holder.imgProduct.setImageURI(Uri.parse(product.imageUri))
        } else {
            holder.imgProduct.setImageResource(product.imageResId)
        }

        holder.txtName.text = product.name
        holder.txtPrice.text = "$${product.price}"

        val sharedPreferences = context.getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val activeUserJson = sharedPreferences.getString("activeUser", null)

        if (activeUserJson != null) {
            val activeUser = JSONObject(activeUserJson)
            val rol = activeUser.getString("rol")

            if (rol == "admin") {
                holder.btnEdit.visibility = View.VISIBLE
                holder.btnDelete.visibility = View.VISIBLE
                holder.btnCarrito.visibility = View.GONE
            } else {
                holder.btnEdit.visibility = View.GONE
                holder.btnDelete.visibility = View.GONE
                holder.btnCarrito.visibility = View.VISIBLE
            }
        }

        holder.btnEdit.setOnClickListener {
            val bundle = Bundle().apply {
                putString("name", product.name)
                putInt("price", product.price)
                putInt("imageResId", product.imageResId)
                putString("imageUri", product.imageUri)
                putString("description", product.description)
                putString("reason", product.reason)
                putString("category", product.category)
                putString("company", product.company)
            }
            val navController = Navigation.findNavController(holder.itemView)
            navController.navigate(R.id.AddProductFragment, bundle)
        }

        holder.btnDelete.setOnClickListener {
            val prefs = context.getSharedPreferences("ProductData", Context.MODE_PRIVATE)
            val jsonArray = JSONArray(prefs.getString("products", "[]"))
            val newArray = JSONArray()

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                if (obj.getString("name") != product.name) {
                    newArray.put(obj)
                }
            }

            prefs.edit().putString("products", newArray.toString()).apply()
            Toast.makeText(context, "${product.name} eliminado", Toast.LENGTH_SHORT).show()
            (context as? Activity)?.recreate()
        }

        holder.btnCarrito.setOnClickListener {
            addProductShoppingCart(product.name, 1, product.price)
        }
    }

    override fun getItemCount(): Int = productList.size

    fun updateList(newList: List<Product>) {
        productList.clear()
        productList.addAll(newList)
        notifyDataSetChanged()
    }

    private fun addProductShoppingCart(name: String, amount: Int, price: Int) {
        val sharedPreferences = context.getSharedPreferences("ShoppingCart", Context.MODE_PRIVATE)
        val control = sharedPreferences.edit()
        val productsJson = sharedPreferences.getString("products", "[]")
        val productsArray = JSONArray(productsJson)
        var productExists = false

        for (i in 0 until productsArray.length()) {
            val currentProduct = productsArray.getJSONObject(i)
            if (currentProduct.getString("name") == name) {
                val currentAmount = currentProduct.getInt("amount")
                currentProduct.put("amount", currentAmount + amount)
                productsArray.put(i, currentProduct)
                productExists = true
                break
            }
        }

        if (!productExists) {
            val productObject = JSONObject().apply {
                put("name", name)
                put("amount", amount)
                put("price", price)
            }
            productsArray.put(productObject)
        }

        control.putString("products", productsArray.toString())
        control.apply()

        Toast.makeText(context, "Se agregó al carrito", Toast.LENGTH_SHORT).show()
    }
}
