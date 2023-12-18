package com.benjaminveli.integrador

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.benjaminveli.integrador.API.PostApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ConfigurationFragment : Fragment() {

    private val urlbase = "http://10.0.2.2:4000/"
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var userId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_configuration, container, false)

        // Obtén el userId del argumento y guárdalo
        userId = (arguments?.getInt("userId")?: "" ).toString()
        email = arguments?.getString("email") ?: ""
        password = arguments?.getString("password") ?: ""

        Log.d("ConfigurationFragment", "UserId: $userId, Email: $email, Password: $password")

        loadUserData(email, password, view)

        view.findViewById<Button>(R.id.updateButton)?.setOnClickListener {
            val updatedEmail = view.findViewById<EditText>(R.id.emailEditText)?.text.toString()
            val updatedPassword = view.findViewById<EditText>(R.id.passwordEditText)?.text.toString()

            Log.d(
                "ConfigurationFragment",
                "Updating user with UserId: $userId, Email: $updatedEmail, Password: $updatedPassword"
            )

            updateUser(userId.toString(), updatedEmail, updatedPassword)
        }

        return view
    }


    private fun loadUserData(email: String, password: String, view: View) {
        view.findViewById<EditText>(R.id.emailEditText)?.setText(email)
        view.findViewById<EditText>(R.id.passwordEditText)?.setText(password)
    }

    private fun updateUser(userId: String, updatedEmail: String, updatedPassword: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl(urlbase)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(PostApiService::class.java)

        lifecycleScope.launch(Dispatchers.Main) {
            try {

                val response = service.updateUser(userId, PostApiService.UpdatedUser(updatedEmail, updatedPassword))
                if (response.isExecuted) {
                    // Solicitud exitosa
                    Toast.makeText(
                        requireContext(),
                        "Datos actualizados correctamente",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // Manejar errores de la API
                    Toast.makeText(
                        requireContext(),
                        "Error al actualizar datos",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    requireContext(),
                    "Error connecting to the API: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}



