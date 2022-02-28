package entities

import java.io.Serializable

data class AuthData(val number: Long, val password: String, val name: String) : Serializable