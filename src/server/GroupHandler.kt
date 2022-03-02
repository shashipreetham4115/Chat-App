package server

import entities.Group
import entities.Message
import server.services.ChatHandlerServices
import server.services.GroupHandlerServices

class GroupHandler(private val clientHandler: ClientHandler, private val chatHandler: ChatHandlerServices) :
    GroupHandlerServices {

    override fun leaveGroup(data: Any) {
        val group = data as Group
        if (Server.groups.contains(group.id)) {
            Server.groups[group.id] = group
            clientHandler.client.groups.remove(group.id)
            Server.users[clientHandler.client.number]?.groups?.remove(group.id)
            broadcastServerMessage("left the group", group.id)
            if (group.admins.isEmpty() && group.users.isNotEmpty()) {
                group.admins.add(group.users.first())
                val user = Server.users[group.users.first()]
                broadcastServerMessage("is group admin now", group.id, group.users.first(), user?.name!!)
            }
        }
        clientHandler.writer.writeObject(false)
    }

    override fun addUser(data: Any) {
        val (number, groupId) = data as List<String>
        val group = Server.groups[groupId]
        if (Server.users.contains(number.toLong()) &&
            clientHandler.client.number != number.toLong() &&
            group?.users?.contains(number.toLong()) == false
        ) {
            val user = Server.users[number.toLong()]
            user?.groups?.add(groupId)
            group.users.add(number.toLong())
            clientHandler.writer.writeObject(group)
            broadcastServerMessage("added ${user?.name} in the group", groupId)
        } else clientHandler.writer.writeObject(null)
    }

    override fun updateGroup(data: Any) {
        val group = data as Group
        if (Server.groups.contains(group.id)) {
            val oldGroup = Server.groups[group.id]
            if (oldGroup != null) broadcastGroupUpdateMessage(oldGroup, group)
            Server.groups[group.id] = group
        }
    }

    override fun checkGroupNameAvailability(data: Any) {
        val groupName = data as String
        if (checkGroupNameAvailability(groupName) != "") {
            clientHandler.writer.writeObject(false)
        } else {
            clientHandler.writer.writeObject(true)
        }
    }

    override fun getGroup(data: Any) {
        val groupId = data as String
        if (Server.groups.contains(groupId)) {
            val group = Server.groups[groupId]
            clientHandler.writer.writeObject(group)
            clientHandler.writer.reset()
        } else {
            clientHandler.writer.writeObject(null)
        }
    }

    override fun createGroup(data: Any) {
        val groupName = data as String
        if (checkGroupNameAvailability(groupName) != "") {
            clientHandler.writer.writeObject(false)
        } else {
            val group = Group(groupName)
            group.users.add(clientHandler.client.number)
            group.admins.add(clientHandler.client.number)
            Server.groups[group.id] = group
            Server.users[clientHandler.client.number]?.groups?.add(group.id)
            clientHandler.writer.writeObject(true)
            clientHandler.writer.writeObject(group.id)
            broadcastServerMessage("created the group", group.id)
        }
    }

    override fun joinGroup(data: Any) {
        val groupName = data as String
        val groupId = checkGroupNameAvailability(groupName)
        if (groupId == "" || clientHandler.client.groups.contains(groupId)) {
            clientHandler.writer.writeObject(false)
        } else {
            val group = Server.groups[groupId] ?: return clientHandler.writer.writeObject(false)
            group.users.add(clientHandler.client.number)
            if (group.admins.isEmpty()) group.admins.add(clientHandler.client.number)
            Server.users[clientHandler.client.number]?.groups?.add(group.id)
            clientHandler.writer.writeObject(true)
            clientHandler.writer.writeObject(groupId)
            broadcastServerMessage("joined in the group", groupId)
        }
    }

    private fun checkGroupNameAvailability(groupName: String): String {
        if (groupName == "") return groupName
        val groupId = Server.groups.values.find { i -> i.groupName == groupName }?.id
        return groupId ?: ""
    }

    private fun broadcastGroupUpdateMessage(oldGroup: Group, group: Group) {
        val message: String = if (oldGroup.groupName != group.groupName) {
            "changed the group name from ${oldGroup.groupName} to ${group.groupName}"
        } else if (oldGroup.users.size > group.users.size) {
            val removedUsers = (oldGroup.users - group.users).map { Server.users[it]?.name }.joinToString(", ")
            "removed $removedUsers from group"
        } else if (oldGroup.admins.size > group.admins.size) {
            val removedUsers =
                (oldGroup.admins - group.admins).map { Server.users[it]?.name }.joinToString(", ")
            "changed role of $removedUsers from admin to user"
        } else {
            val removedUsers = (group.admins - oldGroup.admins).map { Server.users[it]?.name }.joinToString(", ")
            "changed role of $removedUsers from user to admin"
        }
        broadcastServerMessage(message, group.id)
    }

    private fun broadcastServerMessage(
        message: String,
        groupId: String,
        sender: Long = clientHandler.client.number,
        senderName: String = clientHandler.client.name
    ) {
        chatHandler.broadcastMessage(Message(sender, senderName, message, groupId, true))
    }

}
