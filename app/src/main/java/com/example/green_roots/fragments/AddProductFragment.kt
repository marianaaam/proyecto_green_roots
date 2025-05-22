package com.example.green_roots.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.green_roots.R
import org.json.JSONArray
import org.json.JSONObject

class AddProductFragment : Fragment() {

    private lateinit var imgPreview: ImageView
    private lateinit var btnSeleccionarImagen: Button
    private var selectedImageUri: String? = null

    private lateinit var tituloPrincipal: TextView
    private lateinit var etTitulo: EditText
    private lateinit var etPrecio: EditText
    private lateinit var etDescripcion: EditText
    private lateinit var etRazon: EditText
    private lateinit var etCategoria: AutoCompleteTextView
    private lateinit var etSeller: AutoCompleteTextView
    private lateinit var btnGuardar: Button
    private lateinit var sharedPrefs: SharedPreferences

    private var isEditMode = false
    private var oldProductName: String? = null

    // ActivityResult para galería
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it.toString()
            imgPreview.setImageURI(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_product, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        sharedPrefs = requireContext().getSharedPreferences("ProductData", Context.MODE_PRIVATE)

        etTitulo = view.findViewById(R.id.etTitulo)
        tituloPrincipal = view.findViewById(R.id.tituloPrincipal)
        etPrecio = view.findViewById(R.id.etPrecio)
        etDescripcion = view.findViewById(R.id.etDescripcion)
        etRazon = view.findViewById(R.id.etRazon)
        etCategoria = view.findViewById(R.id.etCategoria)
        etSeller = view.findViewById(R.id.etSeller)
        btnGuardar = view.findViewById(R.id.btnGuardarProducto)
        imgPreview = view.findViewById(R.id.imgPreview)
        btnSeleccionarImagen = view.findViewById(R.id.btnSeleccionarImagen)

        // Modo edición
        val args = arguments
        if (args != null && args.containsKey("name")) {
            isEditMode = true
            oldProductName = args.getString("name")
            etTitulo.setText(args.getString("name"))
            etPrecio.setText(args.getInt("price").toString())
            etDescripcion.setText(args.getString("description") ?: "")
            etRazon.setText(args.getString("reason") ?: "")
            etCategoria.setText(args.getString("category") ?: "")
            etSeller.setText(args.getString("seller") ?: "")
            selectedImageUri = args.getString("imageUri")
            selectedImageUri?.let {
                imgPreview.setImageURI(Uri.parse(it))
            }
            btnGuardar.text = "Actualizar Producto"
            btnSeleccionarImagen.text = "Cambiar Imagen"
            tituloPrincipal.text = "Editar Producto"
        }

        btnSeleccionarImagen.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        btnGuardar.setOnClickListener {
            saveProduct()
            findNavController().navigate(R.id.ProductsFragment)
        }
    }

    private fun saveProduct() {
        val titulo = etTitulo.text.toString().trim()
        val precioInt = etPrecio.text.toString().toIntOrNull() ?: 0
        val descripcion = etDescripcion.text.toString().trim()
        val reason = etRazon.text.toString().trim()
        val categoria = etCategoria.text.toString().trim()
        val vendedor = etSeller.text.toString().trim()

        val json = sharedPrefs.getString("products", "[]") ?: "[]"
        val jsonArray = JSONArray(json)
        val newArray = JSONArray()

        // Si es edición, reemplaza el producto viejo
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            if (isEditMode && obj.getString("name") == oldProductName) {
                val updatedProduct = JSONObject().apply {
                    put("name", titulo)
                    put("price", precioInt)
                    put("imageUri", selectedImageUri ?: "")
                    put("description", descripcion)
                    put("reason", reason)
                    put("category", categoria)
                    put("seller", vendedor)
                }
                newArray.put(updatedProduct)
            } else {
                newArray.put(obj)
            }
        }

        // Si es nuevo producto, agregarlo
        if (!isEditMode) {
            val newProduct = JSONObject().apply {
                put("name", titulo)
                put("price", precioInt)
                put("imageUri", selectedImageUri ?: "")
                put("description", descripcion)
                put("reason", reason)
                put("category", categoria)
                put("seller", vendedor)
            }
            newArray.put(newProduct)
        }

        sharedPrefs.edit()
            .putString("products", newArray.toString())
            .apply()
    }
}
