package server.services

interface ChatHandlerServices {
    fun broadcastMessage(data: Any)
    fun getChat(data: Any)
}