package com.benjaminveli.integrador.SQLite

import com.benjaminveli.integrador.SQLite.Comentario
import java.text.SimpleDateFormat
import java.util.*

class Publicacion  {
    var id: Int = 0
    var content: String = ""
    var fechaCreacion: String = ""
    var comentarios: MutableList<Comentario> = mutableListOf()
    var likes: Int = 0
    constructor(content: String) {
        this.content = content
        this.fechaCreacion = obtenerFechaHoraActual()
    }

    constructor() {
        this.fechaCreacion = obtenerFechaHoraActual()
    }

    private fun obtenerFechaHoraActual(): String {
        val formato = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val fechaHoraActual = Date()
        return formato.format(fechaHoraActual)
    }

    fun agregarComentario(comentario: Comentario) {
        comentarios.add(comentario)
    }

    fun obtenerComentarios(): List<Comentario> {
        return comentarios
    }

    fun incrementarLikes() {
        likes++
    }
}


