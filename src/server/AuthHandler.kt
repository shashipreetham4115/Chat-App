package server

import entities.Profile
import entities.User
import server.services.AuthHandlerServices

class AuthHandler(private val clientHandler: ClientHandler) : AuthHandlerServices {

    override fun signIn(data: Any) {
        val authData = data as User
        if (Server.users.contains(authData.number)) {
            val user = Server.users[authData.number]
            if (user?.password == authData.password) {
                val profile = Profile(user.name, user.number, "", user.groups)
                clientHandler.client = profile
                if (Server.clients.contains(profile.number)) Server.clients[profile.number]?.add(clientHandler)
                else Server.clients[profile.number] = mutableListOf(clientHandler)
                return clientHandler.writer.writeObject(profile)
            }
        }
        clientHandler.writer.writeObject(null)
    }

    override fun signUp(data: Any) {
        val authData = data as User
        if (!Server.users.contains(authData.number)) {
            val profile = Profile(authData.name, authData.number, "", authData.groups)
            Server.users[authData.number] = authData
            clientHandler.client = profile
            Server.clients[profile.number] = mutableListOf(clientHandler)
            return clientHandler.writer.writeObject(profile)
        }
        clientHandler.writer.writeObject(null)
    }

}