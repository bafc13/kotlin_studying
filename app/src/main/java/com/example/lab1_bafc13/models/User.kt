package com.example.lab1_bafc13.models

data class User (
    val uuid: String,
    val name: String
)

data class UsersResponse (
    val items: List<User>
)