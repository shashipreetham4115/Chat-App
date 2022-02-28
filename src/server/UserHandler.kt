package server

import entities.Profile
import server.services.UserHandlerServices

class UserHandler(private val clientHandler: ClientHandler) : UserHandlerServices {

    override fun getUserGroups(data: Any) {
        val groupIds = data as List<*>
        val groupNames = mutableListOf<String>()
        for (id in groupIds) {
            if (Server.groups.contains(id)) {
                Server.groups[id]?.groupName?.let { groupNames.add(it) }
            }
        }
        clientHandler.writer.writeObject(groupNames as List<String>)
        clientHandler.writer.reset()
    }

    override fun changeName(data: Any) {
        val name = data as String
        if (name == "" || clientHandler.client.name == name) {
            clientHandler.writer.writeObject(false)
        } else {
            clientHandler.client.name = name
            Server.users[clientHandler.client.number]?.name = name
            clientHandler.writer.writeObject(true)
        }
    }

    override fun getProfiles(data: Any) {
        val groupId = data as String
        val userNumbers = Server.groups[groupId]?.users
        val userProfiles = mutableListOf<Profile>()
        if (userNumbers != null) {
            for (number in userNumbers) {
                if (number != clientHandler.client.number) {
                    val user = Server.users[number]
                    val profile = user?.let { Profile(it.name, user.number, "", user.groups) }
                    if (profile != null) {
                        userProfiles.add(profile)
                    }
                }
            }
        }
        clientHandler.writer.writeObject(userProfiles as List<Profile>)
        clientHandler.writer.reset()
    }

}
