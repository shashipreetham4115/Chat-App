package server

import entities.Message
import server.services.ChatHandlerServices

class ChatHandler(private val clientHandler: ClientHandler) : ChatHandlerServices {

    override fun broadcastMessage(data: Any) {
        val message = data as Message
        val userNumbers = Server.groups[message.group]?.users
        if (userNumbers != null) {
            if (message.content == "--q" || (message.sender != 0L && !userNumbers.contains(message.sender))) {
                clientHandler.client.active = ""
                return clientHandler.writer.writeObject(message)
            }
            Server.messages.add(message)
            for (i in userNumbers) {
                val userHandler = Server.clients[i] ?: continue
                if (userHandler.client.active == message.group && userHandler.client.number != message.sender) {
                    userHandler.writer.writeObject(message)
                    userHandler.writer.flush()
                }
            }
        }
    }

    override fun getChat(data: Any) {
        val groupId = data as String
        val messages = Server.messages.filter { it.group == groupId }
        clientHandler.writer.writeObject(messages)
        clientHandler.writer.reset()
        val userNames = mutableMapOf<Long, String>()
        val users = Server.groups[groupId]?.users
        if (users != null) {
            for (number in users) {
                userNames[number] = Server.clients[number]?.client?.name ?: ""
            }
        }
        clientHandler.writer.writeObject(userNames)
        clientHandler.writer.reset()
        clientHandler.client.active = groupId
    }

}
