package com.benjaminveli.integrador.SQLite

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

var BD = "baseDatos"

class BaseDatos(contexto: Context): SQLiteOpenHelper(contexto, BD,null,1) {

    override fun onCreate(db: SQLiteDatabase?) {
        var sqlUsuario = "CREATE TABLE IF NOT EXISTS Usuario(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "content VARCHAR(250), " +
                "fechaCreacion VARCHAR(20)" +
                ")"
        db?.execSQL(sqlUsuario)

        var sqlComentario = "CREATE TABLE IF NOT EXISTS Comentario(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "contenido VARCHAR(250), " +
                "fechaCreacion VARCHAR(20), " +
                "idPublicacion INTEGER, " +  // Columna para establecer la relación con la publicación
                "FOREIGN KEY (idPublicacion) REFERENCES Usuario(id)" +
                ")"
        db?.execSQL(sqlComentario)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, oldVersion: Int, newVertsion: Int) {
        TODO("Not yet implemented")
    }

    fun insertarPublicacion(usuario: Publicacion): String {
        val db = this.writableDatabase
        var contenedorValores = ContentValues()

        contenedorValores.put("content", usuario.content)
        contenedorValores.put("fechaCreacion", usuario.fechaCreacion)

        var resultado = db.insert("Usuario", null, contenedorValores)

        if (resultado == -1.toLong()) {
            Log.e("BaseDatos", "Inserción Fallida")
            return "Inserción Fallida"
        } else {
            Log.d("BaseDatos", "Datos Insertados (ok)")
            return "Datos Insertados (ok)"
        }
    }

    fun insertarComentario(comentario: Comentario): String {
        val db = this.writableDatabase
        var contenedorValores = ContentValues()

        contenedorValores.put("contenido", comentario.contenido)
        contenedorValores.put("fechaCreacion", comentario.fechaCreacion)
        contenedorValores.put("idPublicacion", comentario.idPublicacion)

        var resultado = db.insert("Comentario", null, contenedorValores)

        if (resultado == -1.toLong()) {
            Log.e("BaseDatos", "Inserción de Comentario Fallida")
            return "Inserción de Comentario Fallida"
        } else {
            Log.d("BaseDatos", "Comentario Insertado (ok)")
            return "Comentario Insertado (ok)"
        }
    }


    fun listarDatos(): MutableList<Publicacion> {
        val lista: MutableList<Publicacion> = ArrayList()
        val db = this.readableDatabase

        val sql = "SELECT * FROM Usuario"
        val resultado = db?.rawQuery(sql, null)

        if (resultado != null && resultado.moveToFirst()) {
            do {
                val usu = Publicacion()
                usu.id = resultado.getString(resultado.getColumnIndexOrThrow("id")).toInt()
                usu.content = resultado.getString(resultado.getColumnIndexOrThrow("content"))
                usu.fechaCreacion = resultado.getString(resultado.getColumnIndexOrThrow("fechaCreacion"))
                lista.add(usu)
            } while (resultado.moveToNext())

            resultado.close()
        }

        db?.close()
        return lista
    }

    fun actualizarDatosPublicacion(id:String, content:String):String{
        val db = this.writableDatabase
        var contenedorValores = ContentValues()
        contenedorValores.put("content", content)

        var resultado = db.update("Usuario", contenedorValores, "id=?", arrayOf(id))

        db.close()

        if(resultado > 0){
            return "Actualizacion Realizada"
        }else{
            return "Error en Actualizacion"
        }

    }

    fun eliminarDatosPublicacion(id:String):Int{
        val db = this.writableDatabase
        var resultado = 0
        if(id.length > 0) {
            resultado = db.delete("Usuario", "id=?", arrayOf(id))
            db.close()
        }
        return resultado
    }



}
