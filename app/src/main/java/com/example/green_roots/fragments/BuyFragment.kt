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

class BuyFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferencesUser: SharedPreferences
    private lateinit var containerLayout: LinearLayout
    private lateinit var tvTotal: TextView
    private lateinit var btnDeploy: ImageView
    private lateinit var btnCheckout: Button
    private lateinit var etNumCard: EditText
    private lateinit var etCaducidad: EditText
    private lateinit var etCodSecurity: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_buy, container, false)

        sharedPreferences = requireActivity().getSharedPreferences("ShoppingCart", Context.MODE_PRIVATE)
        sharedPreferencesUser = requireActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE)
        containerLayout = view.findViewById(R.id.cartItemsContainer)
        tvTotal = view.findViewById(R.id.tvTotal)
        btnDeploy = view.findViewById(R.id.buttonDeploy)
        btnCheckout = view.findViewById(R.id.btnCheckout)
        etNumCard= view.findViewById(R.id.etNunCard)
        etCaducidad= view.findViewById(R.id.etCaducidad)
        etCodSecurity= view.findViewById(R.id.etCodSecurity)

        btnCheckout.setOnClickListener {
            checkout()
        }

        loadCartItems()

        return view
    }

    private fun loadCartItems() {
        containerLayout.removeAllViews()

        val productsJson = sharedPreferences.getString("products", "[]")
        val productsArray = JSONArray(productsJson)

        tvTotal.visibility = View.VISIBLE
        btnCheckout.visibility = View.VISIBLE

        var total = 0

        for (i in 0 until productsArray.length()) {
            val product = productsArray.getJSONObject(i)
            var amount = product.getInt("amount")
            val price = product.getInt("price")

            total += price * amount
        }

        var deploy=false
        btnDeploy.setOnClickListener({
            if(!deploy){
                for (i in 0 until productsArray.length()) {
                    val product = productsArray.getJSONObject(i)
                    val name = product.getString("name")
                    var amount = product.getInt("amount")
                    val price = product.getInt("price")

                    addCartItemView(name, amount, price)
                }

                btnDeploy.animate().rotation(180f).setDuration(300).start()
                deploy=true
            } else {
                containerLayout.removeAllViews()
                btnDeploy.animate().rotation(0f).setDuration(300).start()
                deploy=false
            }
        })



        updateTotal(total)
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


    private fun updateTotal(total: Int) {
        tvTotal.text = "Total: ${formatPrice(total)}"
    }

    private fun checkout() : Boolean {

        fun isFieldEmpty(field: EditText, errorMessage: String): Boolean {
            if (field.text.toString().trim().isEmpty()) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                return true
            }
            return false
        }

        if (isFieldEmpty(etNumCard, "El numero de tarjeta es requerido")) return false
        if (isFieldEmpty(etCaducidad, "La fecha de caducidad es requerido")) return false
        if (isFieldEmpty(etCodSecurity, "El codigo de seguridad es requerido")) return false


        // Obtener productos actuales del carrito
        val productsJson = sharedPreferences.getString("products", "[]")
        val productsArray = JSONArray(productsJson)

        // Obtener o crear la lista de compras realizadas
        val purchasedProductsJson = sharedPreferences.getString("purchased_products", "[]")
        val purchasedProductsArray = JSONArray(purchasedProductsJson)

        val activeUserJson = sharedPreferencesUser.getString("activeUser", null)
        val activeUser = JSONObject(activeUserJson)

        // Agregar los nuevos productos comprados
        for (i in 0 until productsArray.length()) {
            val product = productsArray.getJSONObject(i)

            // Aquí puedes agregar información adicional como fecha de compra, etc.
            val purchasedProduct = JSONObject().apply {
                put("name", product.getString("name"))
                put("amount", product.getInt("amount"))
                put("price", product.getInt("price"))
                put("purchase_date", System.currentTimeMillis()) // Fecha actual
                put("emailUser", activeUser.getString("email") )
            }

            purchasedProductsArray.put(purchasedProduct)
        }

        // Guardar los productos comprados
        sharedPreferences.edit()
            .putString("purchased_products", purchasedProductsArray.toString())
            .apply()

        //Limpiar el carrito
        sharedPreferences.edit().remove("products").apply()

        Toast.makeText(requireContext(), "Compra realizada por un ${tvTotal.text}", Toast.LENGTH_LONG).show()
        loadCartItems()
        return true
    }

    private fun formatPrice(price: Int): String {
        return "$${String.format(Locale.US, "%,d", price)}"
    }


}