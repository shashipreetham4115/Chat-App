package entities

import java.io.Serializable

data class Profile(
    var name: String,
    val number: Long,
    var active: String = "",
    val groups: MutableList<String> = mutableListOf()
) : Serializable
