package server

import entities.AuthData
import entities.Profile
import entities.User
import server.services.AuthHandlerServices

class AuthHandler(private val clientHandler: ClientHandler) : AuthHandlerServices {

    override fun signIn(data: Any) {
        val authData = data as AuthData
        if (Server.users.contains(authData.number)) {
            val user = Server.users[authData.number]
            if (user?.password == authData.password) {
                val profile = Profile(user.name, user.number, "", user.groups)
                clientHandler.client = profile
                Server.clients[profile.number] = clientHandler
                return clientHandler.writer.writeObject(profile)
            }
        }
        clientHandler.writer.writeObject(null)
    }

    override fun signUp(data: Any) {
        val authData = data as AuthData
        if (!Server.users.contains(authData.number)) {
            val user = User(authData.name, authData.number, authData.password)
            val profile = Profile(user.name, user.number, "", user.groups)
            Server.users[authData.number] = user
            clientHandler.client = profile
            Server.clients[profile.number] = clientHandler
            return clientHandler.writer.writeObject(profile)
        }
        clientHandler.writer.writeObject(null)
    }

}
