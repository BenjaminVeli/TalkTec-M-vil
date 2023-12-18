package com.benjaminveli.integrador.Adapter

import android.app.AlertDialog
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.benjaminveli.integrador.SQLite.BaseDatos
import com.benjaminveli.integrador.R
import com.benjaminveli.integrador.SQLite.Publicacion
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.benjaminveli.integrador.SQLite.Comentario

class PublicacionAdapter : RecyclerView.Adapter<PublicacionAdapter.ViewHolder>() {

    private var datos: List<Publicacion> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cardview, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val usuario = datos[position]
        holder.bind(usuario)
    }

    override fun getItemCount(): Int {
        return datos.size
    }

    fun setDatos(nuevosDatos: List<Publicacion>) {
        datos = nuevosDatos
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nombreTextView: TextView = itemView.findViewById(R.id.nombreTextView)
        private val fechaCreacionTextView: TextView = itemView.findViewById(R.id.fechaCreacionTextView)
        private val btnMostrarMenu: ImageView = itemView.findViewById(R.id.btnMostrarMenu)
        private val btnComentar: Button = itemView.findViewById(R.id.btnComentar)
        private val btnMeGusta: Button = itemView.findViewById(R.id.btnMeGusta)

        init {
            // Configurar el evento de clic para mostrar el menú
            btnMostrarMenu.setOnClickListener {
                mostrarMenu()
            }

            // Configurar el evento de clic para el botón Comentar
            btnComentar.setOnClickListener {
                // Lógica para escribir y mostrar comentarios
                escribirYMostrarComentario(adapterPosition)
            }

            btnMeGusta.setOnClickListener {
                incrementarMeGusta(adapterPosition)
            }
        }

        fun bind(publicacion: Publicacion) {
            nombreTextView.text = publicacion.content

            // Configurar el texto de la fecha de creación
            if (!publicacion.fechaCreacion.isNullOrEmpty()) {
                fechaCreacionTextView.text = publicacion.fechaCreacion
            } else {
                fechaCreacionTextView.text = "Fecha y hora no disponibles"
                Log.w("com.benjaminveli.integrador.Adapter.TuAdapterPersonalizado", "fechaHora is null or empty for publicacion.id: ${publicacion.id}")
            }

            // Mostrar comentarios usando un RecyclerView
            val comentarios = publicacion.obtenerComentarios()

            val recyclerViewComentarios: RecyclerView = itemView.findViewById(R.id.recyclerViewComentarios)
            val layoutManager = LinearLayoutManager(itemView.context)
            val adapter = ComentariosAdapter(comentarios.toMutableList())
            recyclerViewComentarios.layoutManager = layoutManager
            recyclerViewComentarios.adapter = adapter

            val likesTextView: TextView = itemView.findViewById(R.id.likesTextView)
            likesTextView.text = "${publicacion.likes} Me gusta"
        }

        private fun mostrarMenu() {
            val popupMenu = PopupMenu(itemView.context, btnMostrarMenu)
            val inflater = popupMenu.menuInflater
            inflater.inflate(R.menu.menu_main, popupMenu.menu)

            // Configurar eventos de clic para las opciones del menú
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_eliminar -> {
                        eliminarItem(adapterPosition)
                        true
                    }
                    R.id.action_actualizar -> {
                        actualizarItem(adapterPosition)
                        true
                    }
                    R.id.action_reportar -> {
                        reportarItem(adapterPosition)
                        true
                    }
                    else -> false
                }
            }

            // Mostrar el menú emergente
            popupMenu.show()
        }

        private fun reportarItem(position: Int) {
            val usuario = datos[position]

            val alertDialogBuilder = AlertDialog.Builder(itemView.context, R.style.CustomAlertDialog)

            val title = SpannableString("Denunciar Publicación")
            title.setSpan(ForegroundColorSpan(ContextCompat.getColor(itemView.context, R.color.white)), 0, title.length, 0)

            val message = SpannableString("¿Estás seguro que deseas denunciar esta publicación?")
            message.setSpan(ForegroundColorSpan(ContextCompat.getColor(itemView.context, R.color.white)), 0, message.length, 0)

            alertDialogBuilder.setTitle(title)
            alertDialogBuilder.setMessage(message)

            alertDialogBuilder.setPositiveButton("Aceptar") { dialog, which ->
                Toast.makeText(itemView.context, "Publicación denunciada", Toast.LENGTH_SHORT).show()
            }

            alertDialogBuilder.setNegativeButton("Cancelar") { dialog, which ->
                dialog.dismiss()
            }

            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()

            // Personaliza el diseño de los botones del cuadro de diálogo si es necesario
            val positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            val negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)

            // Ejemplo de personalización de los botones (ajusta según sea necesario)
            positiveButton.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorAccent))
            negativeButton.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorAccent))
        }


        private fun escribirYMostrarComentario(position: Int) {
            val usuario = datos[position]

            // Mostrar un cuadro de diálogo para el comentario
            val alertDialogBuilder = AlertDialog.Builder(itemView.context)
            alertDialogBuilder.setTitle("Comentar Publicación")

            val inputComentario = EditText(itemView.context)
            inputComentario.hint = "Escribe tu comentario aquí"
            alertDialogBuilder.setView(inputComentario)

            alertDialogBuilder.setPositiveButton("Comentar") { dialog, which ->
                val nuevoComentario = inputComentario.text.toString()

                if (nuevoComentario.isNotEmpty()) {
                    // Crea un nuevo comentario y agrégalo a la publicación
                    val comentario = Comentario(nuevoComentario)
                    usuario.agregarComentario(comentario)

                    // Insertar el comentario en la base de datos
                    val db = BaseDatos(itemView.context)
                    val resultado = db.insertarComentario(comentario)

                    // Notificar al adaptador que los datos han cambiado
                    notifyDataSetChanged()

                    Toast.makeText(itemView.context, "Comentario agregado", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(itemView.context, "Por favor, escribe tu comentario", Toast.LENGTH_SHORT).show()
                }
            }

            alertDialogBuilder.setNegativeButton("Cancelar") { dialog, which ->
                dialog.dismiss()
            }

            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }


        private fun eliminarItem(position: Int) {
            // Obtener el usuario correspondiente a la posición
            val usuario = datos[position]

            // Lógica para eliminar el usuario de la base de datos
            val db = BaseDatos(itemView.context)
            val resultado = db.eliminarDatosPublicacion(usuario.id.toString())

            if (resultado > 0) {
                // Eliminación exitosa, actualizar la lista y notificar al adaptador
                datos = datos.filterIndexed { index, _ -> index != position }
                notifyDataSetChanged()
                Toast.makeText(itemView.context, "Publicación eliminada", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    itemView.context,
                    "Error al eliminar la publicación",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        private fun incrementarMeGusta(position: Int) {
            val publicacion = datos[position]
            publicacion.incrementarLikes()
            notifyDataSetChanged()
        }

        private fun actualizarItem(position: Int) {
            // Obtener el usuario correspondiente a la posición
            val usuario = datos[position]

            // Mostrar un cuadro de diálogo para la actualización
            val alertDialogBuilder = AlertDialog.Builder(itemView.context)
            alertDialogBuilder.setTitle("Actualizar Descripción")

            val inputNombre = EditText(itemView.context)
            inputNombre.hint = "¿Qué estás pensando?"
            alertDialogBuilder.setView(inputNombre)

            alertDialogBuilder.setPositiveButton("Actualizar") { dialog, which ->
                val nuevoNombre = inputNombre.text.toString()

                if (nuevoNombre.isNotEmpty()) {
                    // Realizar la actualización en la base de datos
                    val db = BaseDatos(itemView.context)
                    val resultado = db.actualizarDatosPublicacion(usuario.id.toString(), nuevoNombre)

                    if (resultado == "Actualizacion Realizada") {
                        // Notificar al adaptador que los datos han cambiado
                        notifyDataSetChanged()
                        Toast.makeText(itemView.context, "Publicación actualizada", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(itemView.context, "Error al actualizar la publicación", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(itemView.context, "¿Qué estás pensando?", Toast.LENGTH_SHORT).show()
                }
            }

            alertDialogBuilder.setNegativeButton("Cancelar") { dialog, which ->
                dialog.dismiss()
            }

            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }
    }
}


