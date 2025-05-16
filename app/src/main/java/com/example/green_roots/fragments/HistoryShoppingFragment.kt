package com.example.green_roots.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.green_roots.R
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale

class HistoryShoppingFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferencesUser: SharedPreferences
    private lateinit var containerLayout: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_history_shopping, container, false)

        sharedPreferences = requireActivity().getSharedPreferences("ShoppingCart", Context.MODE_PRIVATE)
        sharedPreferencesUser = requireActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE)
        containerLayout = view.findViewById(R.id.cartItemsContainer)

        loadCartItems()

        return view
    }

    private fun loadCartItems() {
        containerLayout.removeAllViews()

        val productsJson = sharedPreferences.getString("purchased_products", "[]")
        val productsArray = JSONArray(productsJson)

        val activeUserJson = sharedPreferencesUser.getString("activeUser", null)
        val activeUser = JSONObject(activeUserJson)

        for (i in productsArray.length() - 1 downTo 0) {
            val product = productsArray.getJSONObject(i)

            if (product.getString("emailUser").equals(activeUser.getString("email"))){
                val name = product.getString("name")
                var amount = product.getInt("amount")
                val price = product.getInt("price")

                addCartItemView(name, amount, price)
                addDivider()
            }
        }
    }

    private fun addCartItemView(name: String, amount: Int, price: Int) {
        val itemView = LayoutInflater.from(requireContext())
            .inflate(R.layout.item_cart_product, containerLayout, false)

        itemView.findViewById<TextView>(R.id.tvProductName).text = name
        itemView.findViewById<TextView>(R.id.tvProductPrice).text = formatPrice(price * amount)

        val tvAmount = itemView.findViewById<TextView>(R.id.tvProductAmount)
        tvAmount.text = amount.toString()

        itemView.findViewById<Button>(R.id.btnDecrease).visibility = View.GONE
        itemView.findViewById<Button>(R.id.btnIncrease).visibility = View.GONE

        containerLayout.addView(itemView)
    }

    private fun addDivider() {
        // Crear un divisor como se muestra en tu código
        val dividerView = View(requireContext())
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            1 // 1dp de altura
        )
        params.setMargins(0, 0, 0, 8) // 8dp de margen inferior
        dividerView.layoutParams = params
        dividerView.setBackgroundResource(R.color.gray)

        containerLayout.addView(dividerView)
    }


    private fun formatPrice(price: Int): String {
        return "$${String.format(Locale.US, "%,d", price)}"
    }


}