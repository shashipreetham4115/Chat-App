package client

import client.services.ChatUiServices
import client.services.GroupUiServices
import client.services.ParticipantsUiServices
import client.utils.InputUtil
import entities.Group
import entities.Request
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class GroupUi(
    private var writer: ObjectOutputStream,
    private var reader: ObjectInputStream,
    private val errHandler: ErrorHandler,
    private val chatUi: ChatUiServices,
    private val participantsUi: ParticipantsUiServices
) : GroupUiServices {

    private lateinit var groupId: String
    private lateinit var group: Group
    private var number = AuthUi.loggedInUser?.number ?: 0L
    private var active = true

    override fun displayMenu(groupId: String) {
        this.groupId = groupId
        active = true
        try {
            loop@ while (active) {
                getGroup()
                if (!group.users.contains(number)) {
                    println("\nYou are removed from the group")
//                AuthUi.loggedInUser?.groups?.remove(groupId)
                    break@loop
                }
                val isAdmin = group.admins.contains(number)
                val request = """
             
                    Group : ${group.groupName}
                    
                    1) Chat
                    2) View Participants
                    3) Leave Group
                    4) Go Back
                    ${if (isAdmin) "5) Change Group Name" else ""}
                    Please Enter Your Choice
                """.trimIndent()

                when (InputUtil.getInt(request)) {
                    -1, 4 -> (break@loop)
                    1 -> chatUi.fetchMessages(groupId)
                    2 -> participantsUi.displayMenu(groupId)
//                3 -> if (isAdmin) deleteGroup() else leaveGroup()
                    3 -> leaveGroup()
                    5 -> if (isAdmin) changeGroupName() else println("Please Enter Valid Option")
                    else -> println("Please Enter Valid Option")
                }
            }
        } catch (_: Exception) {
            errHandler.closeConnection()
        }
    }

    private fun changeGroupName() {
        var gName = InputUtil.getString("Please Enter New Group Name").trim()
        writer.writeObject(Request("group", "checkAvailability", gName))
        while (!(reader.readObject() as Boolean)) {
            println("Group Name Already Taken Please Use Another")
            gName = InputUtil.getString("Please Enter New Group Name").trim()
            writer.writeObject(Request("group", "checkAvailability", gName))
        }
        group.groupName = gName
        getGroup()
        if (group.admins.contains(number)) {
            writer.writeObject(Request("group", "update", group))
            writer.reset()
        } else println("\nSorry you are not group admin now")
    }

    private fun leaveGroup() {
        group.users.remove(number)
        group.admins.remove(number)
        AuthUi.loggedInUser?.groups?.remove(group.id)
        writer.writeObject(Request("group", "leave", group))
        writer.reset()
        active = reader.readObject() as Boolean
    }

    private fun deleteGroup() {
        writer.writeObject(Request("group", "delete", group.id))
        active = reader.readObject() as Boolean
    }

    private fun getGroup() {
        writer.writeObject(Request("group", "get", groupId))
        group = try {
            reader.readObject() as Group
        } catch (ex: Exception) {
            println(ex.toString())
            Group(groupId)
        }
    }

}
