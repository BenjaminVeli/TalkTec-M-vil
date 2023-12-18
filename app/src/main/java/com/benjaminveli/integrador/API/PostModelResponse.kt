package com.benjaminveli.integrador.API

import com.google.gson.annotations.SerializedName

data class PostModelResponse (
    @SerializedName("id")
    var id: Int,
    @SerializedName("codigo")
    var codigo:String,
    @SerializedName("usuario")
    var usuario:String,
    @SerializedName("email")
    var email:String,
    @SerializedName("password")
    var password:String,
    @SerializedName("department_id")
    var department_id:String,
)