package com.example.e_catapp.api

import com.example.e_catapp.models.FormResponse
import com.example.e_catapp.models.JenisAddResponse
import com.example.e_catapp.models.JenisResponse
import com.example.e_catapp.models.LoginResponse
import com.example.e_catapp.models.PetAddResponse
import com.example.e_catapp.models.PetResponse
import com.example.e_catapp.models.ProfileResponse
import com.example.e_catapp.models.TransactionResponse
import com.example.e_catapp.models.TransactionShowResponse
import com.example.e_catapp.models.UsersResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email:String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("register")
    fun tambah(
        @Field("name") name:String,
        @Field("alamat") alamat:String,
        @Field("no_telp") no_telp: String,
        @Field("email") email:String,
        @Field("password") password:String,
        @Field("role_id") role_id:Int
    ): Call<FormResponse>

    @GET("users")
    fun getUsers(
    ): Call<UsersResponse>

    @FormUrlEncoded
    @PUT("users/{id}")
    fun updateUser(
        @Path("id") id: Int,
        @Field("name") name:String,
        @Field("alamat") alamat:String,
        @Field("no_telp") no_telp: String,
        @Field("email") email:String
    ): Call<FormResponse>

    @DELETE("users/{id}")
    fun deleteUser(
        @Path("id") id: Int
    ): Call<FormResponse>

    @FormUrlEncoded
    @PUT("profile")
    fun updateProfile(
        @Field("name") name:String,
        @Field("alamat") alamat:String,
        @Field("no_telp") no_telp: String,
        @Field("email") email:String
    ): Call<ProfileResponse>

    @GET("profile")
    fun getProfile(
    ): Call<ProfileResponse>

    @FormUrlEncoded
    @PUT("profile/password")
    fun updatePassword(
        @Field("password") password: String,
        @Field("password_confirmation") password_confirmation: String
    ): Call<ProfileResponse>

    @Multipart
    @POST("hewans")
    fun addHewan(
        @Part("nama_hewan") nama_hewan: RequestBody,
        @Part("description") description: RequestBody,
        @Part("umur") umur: RequestBody,
        @Part("berat") berat: RequestBody,
        @Part("harga") harga: RequestBody,
        @Part("status_vaksin") status_vaksin: RequestBody,
        @Part("type_id") type_id: RequestBody,
        @Part("jenis_kelamin") jenis_kelamin: RequestBody,
        @Part images: Array<MultipartBody.Part>
    ): Call<PetAddResponse>

    @GET("hewans")
    fun getHewan(
    ): Call<PetResponse>

    @GET("hewans/{id}")
    fun getJenisHewan(
        @Path("id") id: Int
    ): Call<PetAddResponse>

    @GET("hewans/search/{keyword}")
    fun search(
        @Path("keyword") keyword: String
    ): Call<PetResponse>

    @Multipart
    @POST("hewans/{id}")
    fun updateHewan(
        @Path("id") id: Int,
        @Part("nama_hewan") nama_hewan: RequestBody,
        @Part("description") description: RequestBody,
        @Part("umur") umur: RequestBody,
        @Part("berat") berat: RequestBody,
        @Part("harga") harga: RequestBody,
        @Part("status_vaksin") status_vaksin: RequestBody,
        @Part("type_id") type_id: RequestBody,
        @Part("jenis_kelamin") jenis_kelamin: RequestBody,
        @Part images: Array<MultipartBody.Part>,
        @Part("_method") _method: RequestBody
    ): Call<PetAddResponse>

    @DELETE("hewans/{id}")
    fun deleteHewan(
        @Path("id") id: Int
    ): Call<PetResponse>

    @FormUrlEncoded
    @POST("types")
    fun addType(
        @Field("nama_type") nama_type:String
    ): Call<JenisAddResponse>

    @GET("types")
    fun getJenis(
    ): Call<JenisResponse>

    @FormUrlEncoded
    @PUT("types/{id}")
    fun updateJenis(
        @Path("id") id:Int,
        @Field("nama_type") nama_type:String
    ): Call<JenisAddResponse>

    @DELETE("types/{id}")
    fun deleteJenis(
        @Path("id") id:Int
    ): Call<JenisResponse>

    @FormUrlEncoded
    @POST("transactions")
    fun createTransaction(
        @Field("user_id") user_id:Int,
        @Field("hewan_id") hewan_id:Int,
        @Field("tanggal_pengambilan") tanggal_pengambilan: String
    ): Call<TransactionResponse>

    @FormUrlEncoded
    @PUT("transactions/{id}")
    fun updateTransaksiStatus(
        @Path("id") id: Int,
        @Field("status") status: String,
        @Field("status_penerimaan") status_penerimaan: String
    ): Call<TransactionResponse>

    @GET("transactions")
    fun getTransaction(
    ): Call<TransactionShowResponse>

    @GET("transactions/{id}")
    fun getTransactions(
        @Path("id") id: Int
    ): Call<TransactionResponse>

    @GET("transactions/history/{user_id}")
    fun getFragmentTransactions(
        @Path("user_id") user_id: Int
    ): Call<TransactionShowResponse>
}