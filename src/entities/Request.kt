package entities

import java.io.Serializable

data class Request(
    val to: String,
    val route: String,
    val data: Any
) : Serializable
