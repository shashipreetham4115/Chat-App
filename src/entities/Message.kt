package entities

import java.io.Serializable
import java.time.LocalDateTime
import java.util.*

data class Message(
    val sender: Long,
    val senderName: String,
    var content: String,
    val group: String,
    val isServer: Boolean = false,
    val sendTime: LocalDateTime = LocalDateTime.now(),
    val id: String = UUID.randomUUID().toString()
) : Serializable
