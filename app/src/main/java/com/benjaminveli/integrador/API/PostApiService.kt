package com.benjaminveli.integrador.API

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

interface PostApiService {
    @GET("api/usuarios")
    suspend fun getUserPost():ArrayList<PostModelResponse>

    @GET("api/usuario/{id}")
    suspend fun getUserPostById(@Path("id") id:String):ArrayList<PostModelResponse>

    @POST("api/adduser")
    fun createUser(@Body user: User): Call<User>

    @PUT("api/usuario/{id}")
    fun updateUser(
        @Path("id") id: String,
        @Body updatedUser: UpdatedUser
    ): Call<User>

    data class UpdatedUser(
        val email: String,
        val password: String
    )

    data class User(
        val codigo: String,
        val usuario: String,
        val email: String,
        val password: String,
        val department_id: String,
    )


    @GET("api/departamentos")
    suspend fun getDepartments(): ArrayList<DepartmentModelResponse>

    data class DepartmentModelResponse (
        @SerializedName("id")
        var id: Int,
        @SerializedName("name_depart")
        var name_depart: String,
    )


}