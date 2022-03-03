package client

import client.services.ErrorHandlerServices
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.Socket
import kotlin.system.exitProcess

class ErrorHandler(
    private val socket: Socket?,
    private val writer: ObjectOutputStream?,
    private val reader: ObjectInputStream?
) : ErrorHandlerServices {
    override fun closeConnection() {
        try {
            if (socket?.isConnected == true) socket.close()
            reader?.close()
            writer?.close()
        } catch (_: Exception) {
        } finally {
            println("\nServer is not responding!")
            exitProcess(0)
        }
    }
}