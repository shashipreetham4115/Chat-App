package client

import client.services.ChatUiServices
import client.services.ErrorHandlerServices
import entities.Message
import entities.Request
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.Socket
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.Future

class ChatUi(
    private val socket: Socket,
    private val writer: ObjectOutputStream,
    private val reader: ObjectInputStream,
    private val errHandler: ErrorHandlerServices
) : ChatUiServices {

    private lateinit var groupId: String
    private var number: Long = AuthUi.loggedInUser?.number ?: 0L
    private var name: String = AuthUi.loggedInUser?.name ?: ""
    private var active = true
    private lateinit var future: Future<*>

    override fun fetchMessages(groupId: String) {
        try {
            this.groupId = groupId
            active = true
            writer.writeObject(Request("message", "getchat", groupId))
            val messages = try {
                reader.readObject() as List<Message>
            } catch (_: Exception) {
                listOf<Message>()
            }
            val userNames = try {
                reader.readObject() as Map<Long, String>
            } catch (_: Exception) {
                mapOf<Long, String>()
            }
            println()
            for (message in messages) {
                printChat(message, userNames)
            }
            future = ClientUi.executor.submit(messageWriter())
            messageReader()
        } catch (_: Exception) {
            errHandler.closeConnection()
        }
    }

    private fun printChat(message: Message, userNames: Map<Long, String> = mapOf()) {
        val date = message.sendTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a"))
        val senderNumber = message.sender
        val senderName = message.senderName
        var name = if (userNames.contains(senderNumber)) userNames[senderNumber] else senderName
        if (message.isServer) {
            if (senderNumber == number)
                println("\r[$date] You ${if (message.content == "is group admin now") "are group admin now" else message.content}")
            else println("\r[$date] $name ${message.content}")
        } else if (senderNumber == number) {
            printDate(message.content, message.sendTime)
            println()
        } else {
            if (name == "") name = "\b"
            println("\r[$date $name] ${message.content}")
        }
    }


    // reads message from console
    private fun messageReader() {
        try {
            printDate("")
            while (socket.isConnected && !future.isDone) {
                var message = ""
                if (active) message = readLine() ?: ""
//                name = AuthUi.loggedInUser?.name ?: ""
                if (message == "--q") active = false
                if (message != "") {
                    val messageObj = Message(number, name, message, groupId)
                    writer.writeObject(Request("message", "broadcast", messageObj))
                    writer.flush()
                    if (message != "--q") printDate("")
                }
            }
        } catch (ex: IOException) {
            errHandler.closeConnection()
        }
    }

    // writes message to the console
    private fun messageWriter(): Runnable {
        return Runnable {
            try {
                while (socket.isConnected && active) {
                    val message = try {
                        reader.readObject() as Message
                    } catch (_: Exception) {
                        continue
                    }
                    if (message.content == "--q") active = false
                    if (!active) break
                    printChat(message)
                    printDate("")
                }
            } catch (ex: Exception) {
                errHandler.closeConnection()
            }
        }
    }

    private fun printDate(message: String, dateTime: LocalDateTime = LocalDateTime.now()) {
        val date = dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a"))
        val reset = "\u001B[0m"
        val green = "\u001B[32m"
        print("$green\r[$date] $message$reset")
    }
}
