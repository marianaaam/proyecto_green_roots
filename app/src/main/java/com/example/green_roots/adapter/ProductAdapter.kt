package com.example.green_roots.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.green_roots.R
import com.example.green_roots.model.Product

class ProductAdapter(
    private val productList: List<Product>
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProduct: ImageView = itemView.findViewById(R.id.imgProduct)
        val txtName: TextView = itemView.findViewById(R.id.txtName)
        val txtPrice: TextView = itemView.findViewById(R.id.txtPrice)
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
        holder.imgProduct.setImageResource(product.imageResId)
        holder.txtName.text = product.name
        holder.txtPrice.text = "$${product.price}"

        // Eventos de los botones
        holder.btnEdit.setOnClickListener {
            Toast.makeText(it.context, "Editar ${product.name}", Toast.LENGTH_SHORT).show()
        }

        holder.btnDelete.setOnClickListener {
            Toast.makeText(it.context, "Eliminar ${product.name}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int = productList.size
}
