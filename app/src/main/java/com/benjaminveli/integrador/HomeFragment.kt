package com.benjaminveli.integrador

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.benjaminveli.integrador.Adapter.PublicacionAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.benjaminveli.integrador.SQLite.BaseDatos
import com.benjaminveli.integrador.SQLite.Publicacion
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel


class HomeFragment : Fragment() {
    lateinit var content: EditText
    lateinit var adapter: PublicacionAdapter
    lateinit var imageSlider: ImageSlider

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        content = view.findViewById(R.id.txtNombre)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        adapter = PublicacionAdapter()
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // Ver datos al crear la vista
        verDatos()

        // Obtén una referencia al botón (ajusta el ID del botón según tu diseño)
        val guardarButton: Button = view.findViewById(R.id.btnGuardar)

        // Asigna un OnClickListener al botón
        guardarButton.setOnClickListener {
            // Llama a la función GuardarDatos cuando se hace clic en el botón
            GuardarDatos(view)
        }

        imageSlider = view.findViewById(R.id.image_slider)

        val slideModelList = ArrayList<SlideModel>()
        slideModelList.add(SlideModel(R.drawable.tecsup1, ScaleTypes.FIT))
        slideModelList.add(SlideModel(R.drawable.tecsup2, ScaleTypes.FIT))
        slideModelList.add(SlideModel(R.drawable.tecsup3, ScaleTypes.FIT))

        imageSlider.setImageList(slideModelList)

        return view
    }

    private fun verDatos() {
        val db = BaseDatos(requireContext())
        val datos = db.listarDatos()

        adapter.setDatos(datos)
    }

    private fun GuardarDatos(view: View) {
        var db = BaseDatos(requireContext())  // Usa requireContext() para obtener el contexto del fragmento
        var usu = Publicacion()
        var resultado: String = ""
        if (content.text.toString().isNotEmpty()) {
            usu.content = content.text.toString()
            resultado = db.insertarPublicacion(usu)

            if (resultado > "") {
                Toast.makeText(requireContext(), "Se guardó la publicación", Toast.LENGTH_LONG).show()
                content.setText("")
            } else {
                Toast.makeText(requireContext(), "Error al guardar la publicación", Toast.LENGTH_LONG).show()
            }
            verDatos()
        }
    }
}

