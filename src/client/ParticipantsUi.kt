package client

import client.services.ErrorHandlerServices
import client.services.ParticipantsUiServices
import client.utils.InputUtil
import entities.Group
import entities.Profile
import entities.Request
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class ParticipantsUi(
    private val writer: ObjectOutputStream,
    private val reader: ObjectInputStream,
    private val errHandler: ErrorHandlerServices
) : ParticipantsUiServices {

    private lateinit var groupId: String
    private lateinit var profiles: List<Profile>
    private var number = AuthUi.loggedInUser?.number ?: 0L
    private lateinit var group: Group

    override fun displayMenu(groupId: String) {
        try {
            this.groupId = groupId
            this.number = AuthUi.loggedInUser?.number ?: 0L
            while (true) {
                getGroup()
                if (!group.users.contains(number)) return
                val isAdmin = group.admins.contains(number)
                getProfiles()
                displayParticipants()
                val request1 = "1) Go Back"
                val request2 = "\n2) Add User"
                val request3 = "\n3) Remove Users\n4) Make Group Admin\n5) Dismiss as Group Admin"
                val request4 = "\nPlease Enter Your Choice"
                val request =
                    "$request1 ${if (isAdmin) request2 + if (profiles.isNotEmpty()) request3 else "" else ""} $request4"
                when (val input = InputUtil.getInt(request)) {
                    -1, 1 -> return
                    3, 4, 5 -> if (profiles.isNotEmpty() && isAdmin) adminOptions(input)
                    2 -> if (isAdmin) addUsers()
                    else -> println("Please Enter Valid Option")
                }
            }
        } catch (_: Exception) {
            errHandler.closeConnection()
        }
    }

    private fun displayParticipants() {
        val strFormatter = "%1\$-5s%2\$-13s%3\$-20s%4\$-5s"
        println()
        println(String.format(strFormatter, "S.No", "Number", "Name", "Role"))
        if (profiles.isEmpty()) return println("\nYou are only user in group")
        for ((sno, profile) in profiles.withIndex()) {
            println(
                String.format(
                    strFormatter,
                    sno + 1,
                    profile.number,
                    profile.name,
                    if (group.admins.contains(profile.number)) "Admin" else "User"
                )
            )
        }
    }

    private fun updateGroup() {
//        if (group.admins.contains(number)) {
        writer.writeObject(Request("group", "update", group))
        writer.reset()
//        } else {
//            println("Sorry you are not group admin now")
//        }
    }

    private fun dismissAsGroupAdmin(userNumbers: List<Long>) {
        getGroup()
        group.admins.removeAll(userNumbers.toSet())
        updateGroup()
    }

    private fun makeGroupAdmin(userNumbers: List<Long>) {
        group.admins.addAll(userNumbers)
        updateGroup()
    }

    private fun removeUsers(userNumbers: List<Long>) {
        group.users.removeAll(userNumbers.toSet())
        group.admins.removeAll(userNumbers.toSet())
        updateGroup()
    }

    private fun adminOptions(i: Int) {
        val profileSnoStr =
            InputUtil.getString("Please Enter Profile S.No, You Can Enter Multiple S.No's by Comma(,) Separator")
        if (profileSnoStr == "--q") return
        val userNumbers = getUsers(profileSnoStr)
        getGroup()
        if (group.admins.contains(number)) {
            when (i) {
                3 -> removeUsers(userNumbers)
                4 -> makeGroupAdmin(userNumbers)
                5 -> dismissAsGroupAdmin(userNumbers)
            }
        } else println("\nSorry you are not group admin now")
    }

    private fun getUsers(profileSnoStr: String): List<Long> {
        val profileSnoArr = profileSnoStr.replace(" ", "").split(",")
        val userNumbers = mutableListOf<Long>()
        for (i in profileSnoArr) {
            try {
                userNumbers.add(profiles[i.toInt() - 1].number)
            } catch (_: Exception) {
            }
        }
        return userNumbers.toList()
    }

    private fun addUsers() {
        val numberInp = InputUtil.getPhoneNumber()
        if (numberInp == -1L) return
        getGroup()
        if (group.admins.contains(number)) {
            writer.writeObject(Request("group", "adduser", listOf(numberInp.toString(), groupId)))
            group = try {
                reader.readObject() as Group
            } catch (_: Exception) {
                group
            }
        } else println("\nSorry you are not group admin now")
    }

    private fun getProfiles() {
        writer.writeObject(Request("user", "getProfiles", groupId))
        profiles = try {
            reader.readObject() as List<Profile>
        } catch (_: Exception) {
            listOf()
        }
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
