package com.benjaminveli.integrador.activity

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.RemoteViews
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.lifecycleScope
import com.benjaminveli.integrador.API.PostApiService
import com.benjaminveli.integrador.R
import com.benjaminveli.integrador.databinding.ActivityLoginBinding
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {

    private val urlbase = "http://10.0.2.2:4000/"
    private lateinit var retrofit: Retrofit
    private lateinit var service: PostApiService
    private lateinit var binding: ActivityLoginBinding

    private val canalNombre = "Bienvenido"
    private val canalId = "canalId"
    private val notificacionId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        retrofit = Retrofit.Builder()
            .baseUrl(urlbase)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        service = retrofit.create(PostApiService::class.java)

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            validarCredenciales(email, password)
        }

        binding.registroButton.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun validarCredenciales(email: String, password: String) {
        lifecycleScope.launch {
            try {
                val response = service.getUserPost()

                if (response.isNotEmpty()) {
                    val usuarioEncontrado =
                        response.firstOrNull { it.email == email && it.password == password }

                    if (usuarioEncontrado != null) {
                        // Validar credenciales y mostrar notificación si son correctas
                        crearCanalNotificacion()
                        crearNotificacion()

                        val intent = Intent(this@LoginActivity, WelcomeActivity::class.java)
                        intent.putExtra("userId", usuarioEncontrado.id)
                        intent.putExtra("username", usuarioEncontrado.usuario)
                        intent.putExtra("usercode", usuarioEncontrado.codigo)
                        intent.putExtra("emailview", usuarioEncontrado.email)
                        intent.putExtra("passwordview", usuarioEncontrado.password)
                        intent.putExtra("departmentview", usuarioEncontrado.department_id)

                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Credenciales incorrectas",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "No se recibieron datos del servidor",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    this@LoginActivity,
                    "Error al conectar con la API",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun crearCanalNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canalImportancia = NotificationManager.IMPORTANCE_HIGH
            val canal = NotificationChannel(canalId, canalNombre, canalImportancia)

            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(canal)
        }
    }

    private fun crearNotificacion() {
        val resultIntent = Intent(applicationContext, LoginActivity::class.java)
        val resultPendingIntent = androidx.core.app.TaskStackBuilder.create(applicationContext).run {
            addNextIntentWithParentStack(resultIntent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        // Crear la vista personalizada
        val customNotificationView = RemoteViews(packageName, R.layout.custom_notification_layout)

        // Configurar la notificación con la vista personalizada
        val notificacion = NotificationCompat.Builder(this, canalId).also {
            it.setSmallIcon(R.drawable.icon_mensaje)
            it.priority = NotificationCompat.PRIORITY_HIGH
            it.setContentIntent(resultPendingIntent)
            it.setAutoCancel(true)
            it.setCustomContentView(customNotificationView)
        }.build()

        val notificationManager = NotificationManagerCompat.from(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        notificationManager.notify(notificacionId, notificacion)
    }


}
