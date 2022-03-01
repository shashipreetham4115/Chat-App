package server

import entities.Group
import entities.Profile
import server.services.UserHandlerServices

class UserHandler(private val clientHandler: ClientHandler) : UserHandlerServices {

    override fun getUserGroups(data: Any) {
        val groupIds = data as List<*>
        val groupNames = mutableListOf<String>()
        for (id in groupIds) {
            val group = Server.groups[id]
            val groupName =
                if (group?.isPrivate == true) {
                    val userNumber =
                        if (group.users.first() == clientHandler.client.number) group.users.last() else group.users.first()
                    Server.users[userNumber]?.name
                } else (group?.groupName ?: "Unknown") + " Group"
            groupNames.add(groupName!!)
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

    override fun updateProfile(data: Any) {
        val number = data as Long
        val user = Server.users[number]
        val profile = user?.let { Profile(it.name, user.number, "", user.groups) }
        clientHandler.writer.writeObject(profile)
        clientHandler.writer.reset()
    }

    override fun startPrivateChat(data: Any) {
        val number = data as Long
        if (Server.users.contains(number) && number != clientHandler.client.number) {
            val newGroup = Group("", true)
            newGroup.users.add(number)
            newGroup.users.add(clientHandler.client.number)
            Server.groups[newGroup.id] = newGroup
            clientHandler.client.groups.add(newGroup.id)
            Server.users[number]?.groups?.add(newGroup.id)
            clientHandler.writer.writeObject(newGroup.id)
        } else clientHandler.writer.writeObject(null)
    }

}
