package client.services

import java.io.ObjectInputStream
import java.io.ObjectOutputStream

interface AuthUiServices {
    fun authenticate()
    fun signOut()
    fun updateProfile()
}