package client

import client.services.*
import client.utils.InputUtil
import entities.Request
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ClientUi(
    private val writer: ObjectOutputStream,
    private val reader: ObjectInputStream,
    private val authUi: AuthUiServices,
    private val groupUi: GroupUiServices,
    private val errHandler: ErrorHandler
) : ClientServices {

    companion object {
        val executor: ExecutorService = Executors.newSingleThreadExecutor()
    }

    override fun displayMenu() {
        try {
            while (true) {
                val groupIds = AuthUi.loggedInUser?.groups ?: mutableListOf()
                writer.writeObject(Request("user", "getUserGroups", groupIds))
                writer.reset()
                val groups = try {
                    reader.readObject() as List<String>
                } catch (_: Exception) {
                    println("Error in Fetching the groups")
                    listOf<String>()
                }
                val length = groups.size
                println()
                for ((sno, group) in groups.withIndex()) {
                    println("${sno + 1}) $group")
                }
                println("${length + 1}) Join New Group")
                println("${length + 2}) Create New Group")
                println("${length + 3}) Change Your Name")
                println("${length + 4}) Sign out")
                when (val input = InputUtil.getInt("Please Enter Your Choice")) {
                    -1 -> return
                    in 1..groups.size -> groupUi.displayMenu(groupIds[input - 1])
                    length + 1 -> joinNewGroup()
                    length + 2 -> createNewGroup()
                    length + 3 -> changeUserName()
                    length + 4 -> return authUi.signOut()
                    else -> println("Please Enter Valid Option")
                }
            }
        } catch (_: Exception) {
            errHandler.closeConnection()
        }
    }

    private fun changeUserName() {
        var name = InputUtil.getString("Please Enter New Name")
        if (name == "--q") return
        while (true) {
            writer.writeObject(Request("user", "changeName", name.trim()))
            if (reader.readObject() as Boolean) break
            println("Please Enter Valid Name")
            name = InputUtil.getString("Please Enter New Name")
            if (name == "--q") return
        }
        AuthUi.loggedInUser?.name = name
    }

    private fun joinNewGroup() {
        var groupName = InputUtil.getString("Please Enter Group Name")
        if (groupName == "--q") return
        while (true) {
            writer.writeObject(Request("group", "join", groupName.trim()))
            if (reader.readObject() as Boolean) break
            println("Group Name is invalid or You have already joined.")
            groupName = InputUtil.getString("Please Enter Group Name")
            if (groupName == "--q") return
        }
        val groupId = reader.readObject() as String
        AuthUi.loggedInUser?.groups?.add(groupId)
        groupUi.displayMenu(groupId)
    }

    private fun createNewGroup() {
        var groupName = InputUtil.getString("Please Enter Group Name")
        if (groupName == "--q") return
        while (true) {
            writer.writeObject(Request("group", "create", groupName.trim()))
            if (reader.readObject() as Boolean) break
            println("Group Name Already Taken Please Use Another")
            groupName = InputUtil.getString("Please Enter Group Name")
            if (groupName == "--q") return
        }
        val groupId = reader.readObject() as String
        AuthUi.loggedInUser?.groups?.add(groupId)
        groupUi.displayMenu(groupId)
    }
}