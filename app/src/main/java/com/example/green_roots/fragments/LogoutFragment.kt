package com.example.green_roots.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.green_roots.activities.LoginActivity

class LogoutFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = requireContext().getSharedPreferences("sesion", 0)
        sharedPreferences.edit().clear().apply()

        // Redirige al login y termina la actividad actual
        startActivity(
            Intent(requireContext(), LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        )
        activity?.finish()
    }
}
