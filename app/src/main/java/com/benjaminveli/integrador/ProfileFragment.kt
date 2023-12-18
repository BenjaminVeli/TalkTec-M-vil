package com.benjaminveli.integrador

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.benjaminveli.integrador.API.PostApiService
import kotlinx.coroutines.launch



class ProfileFragment(private val service: PostApiService) : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Accede a los elementos de la vista
        val navHeaderTextView = view.findViewById<TextView>(R.id.navHeaderTextView)
        val codenavHeaderTextView = view.findViewById<TextView>(R.id.codenavHeaderTextView)
        val departmentHeaderTextView = view.findViewById<TextView>(R.id.departmentHeaderTextView)

        // Obtén los datos del bundle
        val bundle = arguments
        val username = bundle?.getString("username")
        val usercode = bundle?.getString("usercode")
        val departmentId = bundle?.getString("departmentview")

        // Obtener el nombre del departamento desde la API
        lifecycleScope.launch {
            try {
                val departments = service.getDepartments()
                val departmentsMap = departments.associateBy { it.id.toString() }
                val departmentName = departmentsMap[departmentId.orEmpty()]?.name_depart ?: "Departamento Desconocido"

                // Muestra los datos en los TextViews
                navHeaderTextView.text = username
                departmentHeaderTextView.text = departmentName
                codenavHeaderTextView.text = "Código: $usercode"
            } catch (e: Exception) {
                e.printStackTrace()
                // Manejar errores al obtener la información del departamento.
                departmentHeaderTextView.text = "Departamento Desconocido"
            }
        }

        return view
    }



}