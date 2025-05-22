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
import android.content.Intent
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore

class ProductAdapter(
    private val context: Context,
    private val productList: List<Product>
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
        holder.txtName.text = product.name
        holder.txtPrice.text = "$${product.price}"

        // Improved image loading for all users
        if (!product.imageUri.isNullOrEmpty()) {
            try {
                val uri = Uri.parse(product.imageUri)
                // Use ContentResolver to take() the persistable permission
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(context.contentResolver, uri)
                    val bitmap = ImageDecoder.decodeBitmap(source)
                    holder.imgProduct.setImageBitmap(bitmap)
                } else {
                    @Suppress("DEPRECATION")
                    val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                    holder.imgProduct.setImageBitmap(bitmap)
                }
            } catch (Exception: Exception) {
                // If loading fails, set default image
                holder.imgProduct.setImageResource(R.drawable.ic_default)
            }
        } else {
            holder.imgProduct.setImageResource(R.drawable.ic_default)
        }

        // Accedemos al SharedPreferences
        val sharedPreferences = context.getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val activeUserJson = sharedPreferences.getString("activeUser", null)

        if (activeUserJson != null) {
            val activeUser = JSONObject(activeUserJson)
            val rol = activeUser.getString("rol")

            when (rol) {
                "admin" -> {
                    // Aquí mostramos las opciones del administrador
                    holder.btnEdit.visibility = View.VISIBLE  // Opcion para ocultar visible
                    holder.btnDelete.visibility = View.VISIBLE // Opcion para hacerlo visible
                    holder.btnCarrito.visibility = View.GONE // Opcion para hacerlo ocultarlo
                }
                "cliente" -> {
                    // Aquí mostramos las opciones del cliente
                    holder.btnEdit.visibility = View.GONE  // Opcion para ocultar ocultarlo
                    holder.btnDelete.visibility = View.GONE // Opcion para hacerlo ocultarlo
                    holder.btnCarrito.visibility = View.VISIBLE // Opcion para hacerlo visible
                }
                else -> {
                    Toast.makeText(context, "Rol desconocido", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(context, "No hay usuario activo", Toast.LENGTH_SHORT).show()
        }

        // Eventos de los botones
        holder.btnEdit.setOnClickListener {
            val bundle = Bundle().apply {
                putString("name", product.name)
                putInt("price", product.price)
                putInt("imageResId", product.imageResId)
                putString("imageUri", product.imageUri)
                putString("description", product.description)
                putString("reason", product.reason)
                putString("category", product.category)
                putString("seller", product.seller)
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


    private fun addProductShoppingCart(name: String, amount: Int, price: Int){
        var sharedPreferences = context.getSharedPreferences("ShoppingCart", Context.MODE_PRIVATE)
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

        Toast.makeText(context,"Se agrego correctamente al carrito de compras", Toast.LENGTH_SHORT).show()
    }

    override fun getItemCount(): Int = productList.size
}
