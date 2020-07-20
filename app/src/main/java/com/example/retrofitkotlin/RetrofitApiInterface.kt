package com.example.retrofitkotlin

import com.example.retrofitkotlin.PojoModel.Language
import com.example.retrofitkotlin.PojoModel.LanguageStore
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.*
import retrofit2.http.PUT

interface RetrofitApiInterface {
    @GET(ApiSettings.GET_LANGUAGES)
    fun getData() : Observable<List<Language>>

    @POST(ApiSettings.STORE_LANGUAGE)
    fun storelanguage(@Body languageStore:LanguageStore): Call<Language>

    @DELETE(ApiSettings.DELETE_LANGUAGE+"/{id}")
    fun deleteLanguage(@Path("id") id: Int) : Call<Language>

    @PUT(ApiSettings.UPDATE_LANGUAGE+"/{id}")
    fun updateLanguage(@Path("id") id: Int, @Body languageStore: LanguageStore): Call<Language>

}

