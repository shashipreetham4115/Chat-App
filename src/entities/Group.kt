package entities

import java.io.Serializable
import java.util.*

data class Group(
    var groupName: String,
    val isPrivate: Boolean = false,
    val users: MutableSet<Long> = mutableSetOf(),
    val admins: MutableSet<Long> = mutableSetOf(),
    val id: String = UUID.randomUUID().toString()
) : Serializable
