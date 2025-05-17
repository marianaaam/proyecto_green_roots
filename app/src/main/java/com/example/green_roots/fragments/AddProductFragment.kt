package com.example.green_roots.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.green_roots.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AddProductFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_product, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val etTitulo = view.findViewById<EditText>(R.id.etTitulo)
        val etPrecio = view.findViewById<EditText>(R.id.etPrecio)
        val etDescripcion = view.findViewById<EditText>(R.id.etDescripcion)
        val etRazon = view.findViewById<EditText>(R.id.etRazon)
        val etCategoria = view.findViewById<EditText>(R.id.etCategoria)
        val etEmpresa = view.findViewById<EditText>(R.id.etEmpresa)
        val btnGuardar = view.findViewById<Button>(R.id.btnGuardarProducto)

        btnGuardar.setOnClickListener {
            val bundle = Bundle().apply {
                putString("titulo", etTitulo.text.toString())
                putDouble("precio", etPrecio.text.toString().toDoubleOrNull() ?: 0.0)
                putString("descripcion", etDescripcion.text.toString())
                putString("razon", etRazon.text.toString())
                putString("categoria", etCategoria.text.toString())
                putString("empresa", etEmpresa.text.toString())
            }

            val add = view.findViewById<FloatingActionButton>(R.id.bt_add)
            add.setOnClickListener {
                findNavController().navigate(R.id.ProductsFragment, bundle)
            }
        }
    }
}

