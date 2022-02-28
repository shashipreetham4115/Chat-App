package entities

import java.io.Serializable
import java.util.*

data class User(
    var name: String,
    val number: Long,
    var password: String,
    val groups: MutableList<String> = mutableListOf(),
    val id: String = UUID.randomUUID().toString()
) : Serializable