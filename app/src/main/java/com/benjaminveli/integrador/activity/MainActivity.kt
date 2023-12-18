package com.benjaminveli.integrador.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.benjaminveli.integrador.R

class MainActivity : AppCompatActivity() {

    private lateinit var imageViewTalkTec: ImageView
    private lateinit var textViewTalkTec: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.decorView.apply {
            systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }

        setContentView(R.layout.activity_main)

        imageViewTalkTec = findViewById(R.id.imageViewTalkTec)
        textViewTalkTec = findViewById(R.id.textViewTalkTec)

        // Configurar animación para la imagen
        val imageAlphaAnimator = ObjectAnimator.ofFloat(imageViewTalkTec, View.ALPHA, 0f, 1f)
        imageAlphaAnimator.duration = 2000 // Duración de la animación en milisegundos

        // Configurar animación para el texto
        val textAlphaAnimator = ObjectAnimator.ofFloat(textViewTalkTec, View.ALPHA, 0f, 1f)
        textAlphaAnimator.duration = 2000 // Duración de la animación en milisegundos

        // Crear un conjunto de animaciones
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(imageAlphaAnimator, textAlphaAnimator)

        // Iniciar la animación después de un retraso
        Handler().postDelayed({
            animatorSet.start()
        }, 0) // Retraso de 0 milisegundos antes de iniciar la animación

        Handler().postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000)
    }
}

