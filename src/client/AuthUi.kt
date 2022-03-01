package client

import client.services.AuthUiServices
import client.utils.InputUtil
import entities.AuthData
import entities.Profile
import entities.Request
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class AuthUi(
    private var writer: ObjectOutputStream,
    private var reader: ObjectInputStream,
    private val errHandler: ErrorHandler
) : AuthUiServices {
    companion object {
        var loggedInUser: Profile? = null
            private set
    }

    override fun authenticate() {
        try {
            val request = """
            1) Sign in
            2) Sign up
            3) Exit
            Please choose your choice
        """.trimIndent()
            while (loggedInUser == null) {
                when (InputUtil.getInt(request)) {
                    1 -> signIn()
                    2 -> signUp()
                    -1, 3 -> errHandler.closeConnection()
                    4 -> println("Please Enter Valid Option")
                }
            }
        } catch (ex: Exception) {
            println(ex.message)
            errHandler.closeConnection()
        }
    }

    override fun signOut() {
        loggedInUser = null
        authenticate()
    }

    override fun updateProfile() {
        try {
            writer.writeObject(Request("user", "updateProfile", loggedInUser?.number ?: ""))
            loggedInUser = reader.readObject() as Profile
        } catch (_: Exception) {
            errHandler.closeConnection()
        }
    }

    private fun signIn() {
        val number = InputUtil.getPhoneNumber()
        if (number == -1L) return
        var password = InputUtil.getPassword()
        while (true) {
            if (password == "--q") return
            writer.writeObject(
                Request("auth", "signin", AuthData(number, password, ""))
            )
            loggedInUser = try {
                reader.readObject() as Profile
            } catch (_: Exception) {
                null
            }
            if (loggedInUser != null) break
            println("Incorrect Number or Password")
            password = InputUtil.getPassword()
        }
        if (loggedInUser != null) println("\nWelcome back ${loggedInUser?.name}")
    }

    private fun signUp() {
        val number = InputUtil.getPhoneNumber()
        if (number == -1L) return
        var name: String = InputUtil.getString("Please Enter Your Name")
        while (name == "") {
            println("Please Enter Valid Name")
            name = InputUtil.getString("Please Enter Your Name")
        }
        if (name == "--q") return
        val password = InputUtil.getPassword()
        if (password == "--q") return
        writer.writeObject(Request("auth", "signup", AuthData(number, password, name)))
        loggedInUser = try {
            reader.readObject() as Profile
        } catch (_: Exception) {
            println("\nPhone Number Already in Use Please Use Another")
            null
        }
        if (loggedInUser != null) println("\nWelcome ${loggedInUser?.name}")
    }
}