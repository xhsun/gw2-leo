package me.xhsun.gw2leo.storage.http.model

data class MaterialDTO(
    val id: Int,
    val category: Int,
    val binding: String, //Account or omitted
    val count: Int
)
