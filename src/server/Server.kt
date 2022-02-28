package server

import entities.Group
import entities.Message
import entities.User
import server.services.ServerServices
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.Executors


class Server(private val serverSocket: ServerSocket) : ServerServices {

    companion object {
        val clients: MutableMap<Long, ClientHandler> = mutableMapOf()
        val messages: MutableList<Message> = mutableListOf()
        val users: MutableMap<Long, User> = mutableMapOf()
        val groups: MutableMap<String, Group> = mutableMapOf()
    }

    override fun start() {
        println("Server started on port 8000!")
        val executor = Executors.newCachedThreadPool()
        try {
            while (!serverSocket.isClosed) {
                val socket: Socket = serverSocket.accept()
                val clientHandler = ClientHandler(socket)
                executor.execute(clientHandler)
            }
        } catch (ex: Exception) {
            executor.shutdown()
            closeServerSocket()
        }
    }

    private fun closeServerSocket() {
        try {
            serverSocket.close()

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}

//fun main() {
//    val serverSocket = ServerSocket(3000)
//    val server = server.Server(serverSocket)
//    server.start()
//}