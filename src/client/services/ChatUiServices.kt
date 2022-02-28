package client.services

import java.io.ObjectInputStream
import java.io.ObjectOutputStream

interface ChatUiServices {
    fun fetchMessages(groupId: String)
}