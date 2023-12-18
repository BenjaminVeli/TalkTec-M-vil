package com.benjaminveli.integrador.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.benjaminveli.integrador.API.PostApiService

class DepartmentAdapter(context: Context, resource: Int, private val departments: List<PostApiService.DepartmentModelResponse>) :
    ArrayAdapter<PostApiService.DepartmentModelResponse>(context, resource, departments) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    private fun getCustomView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val row = inflater.inflate(android.R.layout.simple_spinner_item, parent, false)

        // Modifica el TextView para mostrar solo el nombre del departamento
        val textView = row.findViewById<TextView>(android.R.id.text1)
        textView.text = departments[position].name_depart

        return row
    }
}

