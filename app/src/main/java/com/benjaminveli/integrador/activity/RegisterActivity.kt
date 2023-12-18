package com.benjaminveli.integrador.activity

import com.benjaminveli.integrador.Adapter.DepartmentAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.benjaminveli.integrador.API.PostApiService
import com.benjaminveli.integrador.API.PostModelResponse
import com.benjaminveli.integrador.databinding.ActivityRegisterBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.benjaminveli.integrador.R

class RegisterActivity : AppCompatActivity() {

    private val urlbase = "http://10.0.2.2:4000/"
    private lateinit var retrofit: Retrofit
    private lateinit var service: PostApiService
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var departments: List<PostApiService.DepartmentModelResponse>
    private lateinit var departmentSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        retrofit = Retrofit.Builder()
            .baseUrl(urlbase)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        service = retrofit.create(PostApiService::class.java)

        departmentSpinner = findViewById(R.id.departmentSpinner)

        // Obtener la lista de departamentos cuando se crea la actividad
        GlobalScope.launch(Dispatchers.Main) {
            try {
                // Obtener la lista de departamentos
                departments = service.getDepartments()

                // Crear un adaptador para el Spinner
                val adapter = DepartmentAdapter(this@RegisterActivity, android.R.layout.simple_spinner_item, departments)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                // Establecer el adaptador en el Spinner
                departmentSpinner.adapter = adapter
            } catch (e: Exception) {
                // Manejar errores al obtener la lista de departamentos.
                Log.e("RegisterActivity", "Error al obtener la lista de departamentos", e)
            }
        }

        binding.registerButton.setOnClickListener {
            val codigo = binding.codeuserEditText.text.toString()
            val username = binding.usernameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            // Obtener el departamento seleccionado del Spinner
            val selectedDepartment: PostApiService.DepartmentModelResponse = departmentSpinner.selectedItem as PostApiService.DepartmentModelResponse

            if (codigo.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty() ) {
                Toast.makeText(this@RegisterActivity, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (codigo.length != 6) {
                Toast.makeText(this@RegisterActivity, "Por favor, ingrese su código estudiantil correctamente", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Llamada para verificar si el código ya existe
            GlobalScope.launch(Dispatchers.Main) {
                val user = getUser(codigo)
                if (user == null) {
                    // No hay usuarios con el mismo código, puedes proceder a registrar el nuevo usuario
                    val newUser = PostApiService.User(codigo, username, email, password, selectedDepartment.id.toString())
                    registerUser(newUser)
                } else {
                    // Ya existe un usuario con el mismo código
                    Toast.makeText(
                        this@RegisterActivity,
                        "El código ya pertenece a otro alumno",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding.logeoButton.setOnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private suspend fun getUser(codigo: String): PostModelResponse? {
        return withContext(Dispatchers.IO) {
            try {
                val response = service.getUserPostById(codigo)
                if (response.isNotEmpty()) {
                    return@withContext response.first()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return@withContext null
        }
    }

    private fun registerUser(user: PostApiService.User) {
        // Llamada para registrar el nuevo usuario
        service.createUser(user).enqueue(object : retrofit2.Callback<PostApiService.User> {
            override fun onResponse(call: retrofit2.Call<PostApiService.User>, response: retrofit2.Response<PostApiService.User>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@RegisterActivity, "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    val errorBody = response.errorBody()?.string() // Obtener el cuerpo de la respuesta de error
                    Log.e("RegisterActivity", "Error en la respuesta: $errorBody")
                    Toast.makeText(this@RegisterActivity, "Error al registrar el usuario", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: retrofit2.Call<PostApiService.User>, t: Throwable) {
                t.printStackTrace()
                Toast.makeText(this@RegisterActivity, "Error al conectar con la API", Toast.LENGTH_SHORT).show()
            }
        })
    }
}


