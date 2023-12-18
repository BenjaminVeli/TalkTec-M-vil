package com.benjaminveli.integrador.SQLite

import java.text.SimpleDateFormat
import java.util.*

class Comentario {
    var id: Int = 0
    var contenido: String = ""
    var fechaCreacion: String = ""
    var idPublicacion: Int = 0

    constructor(content: String) {
        this.contenido = content
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
}



