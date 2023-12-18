package com.benjaminveli.integrador.Adapter
import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.benjaminveli.integrador.R
import com.benjaminveli.integrador.SQLite.Comentario

class ComentariosAdapter(private val comentarios: MutableList<Comentario>) : RecyclerView.Adapter<ComentariosAdapter.ComentarioViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComentarioViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comentario, parent, false)
        return ComentarioViewHolder(view)
    }


    override fun onBindViewHolder(holder: ComentarioViewHolder, position: Int) {
        val comentario = comentarios[position]
        holder.bind(comentario)
    }

    override fun getItemCount(): Int {
        return comentarios.size
    }

    inner class ComentarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val contenidoTextView: TextView = itemView.findViewById(R.id.contenidoTextView)
        private val fechaCreacionTextView: TextView = itemView.findViewById(R.id.fechaCreacionTextView)
        private val btnMostrarMenuComentario: ImageView = itemView.findViewById(R.id.btnMostrarMenuComentario)

        init {
            // Configurar el evento de clic para mostrar el menú del comentario
            btnMostrarMenuComentario.setOnClickListener {
                mostrarMenuComentario()
            }
        }

        fun bind(comentario: Comentario) {
            contenidoTextView.text = comentario.contenido
            fechaCreacionTextView.text = comentario.fechaCreacion
        }

        private fun mostrarMenuComentario() {
            val popupMenu = PopupMenu(itemView.context, btnMostrarMenuComentario)
            val inflater = popupMenu.menuInflater
            inflater.inflate(R.menu.menu_comentario, popupMenu.menu)

            // Configurar eventos de clic para las opciones del menú del comentario
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_editar_comentario -> {
                        // Lógica para editar el comentario
                        editarComentario(adapterPosition)
                        true
                    }
                    R.id.action_eliminar_comentario -> {
                        // Lógica para eliminar el comentario
                        eliminarComentario(adapterPosition)
                        true
                    }
                    else -> false
                }
            }

            // Mostrar el menú emergente del comentario
            popupMenu.show()
        }

        private fun editarComentario(position: Int) {
            val comentario = comentarios[position]

            // Mostrar un cuadro de diálogo para editar el comentario
            val alertDialogBuilder = AlertDialog.Builder(itemView.context)
            alertDialogBuilder.setTitle("Editar Comentario")

            val inputComentario = EditText(itemView.context)
            inputComentario.setText(comentario.contenido)
            alertDialogBuilder.setView(inputComentario)

            alertDialogBuilder.setPositiveButton("Guardar") { dialog, which ->
                val nuevoComentario = inputComentario.text.toString()

                if (nuevoComentario.isNotEmpty()) {
                    // Actualizar el comentario en la lista y notificar al adaptador
                    comentario.contenido = nuevoComentario
                    notifyDataSetChanged()
                    Toast.makeText(itemView.context, "Comentario actualizado", Toast.LENGTH_SHORT).show()
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

        private fun eliminarComentario(position: Int) {
            // Eliminar el comentario de la lista y notificar al adaptador
            comentarios.removeAt(position)
            notifyItemRemoved(position)
            Toast.makeText(itemView.context, "Comentario eliminado", Toast.LENGTH_SHORT).show()
        }
    }
}



