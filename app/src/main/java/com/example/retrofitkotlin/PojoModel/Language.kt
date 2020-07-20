package com.example.retrofitkotlin.PojoModel

data class Language(
    val id : Int,
    val name : String,
    val created_at : String,
    val updated_at: String)


data class LanguageStore(
    val name : String
)

