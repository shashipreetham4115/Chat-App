package server

import entities.Profile
import entities.Request
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.Socket

class ClientHandler(private val socket: Socket) : Runnable {

    var client: Profile = Profile("", 0)
    val writer = ObjectOutputStream(socket.getOutputStream())
    private val reader = ObjectInputStream(socket.getInputStream())
    private val requestHandler = RequestHandler(
        AuthHandler(this),
        ChatHandler(this),
        GroupHandler(this, ChatHandler(this)),
        UserHandler(this)
    )

    override fun run() {
        try {
            while (socket.isConnected) {
                val obj = reader.readObject() as Request
                requestHandler(obj)
            }
        } catch (ex: IOException) {
            closeConnection()
        }
    }

    private fun requestHandler(request: Request) {
        when (request.to) {
            "user" -> requestHandler.userRoute(request)
            "message" -> requestHandler.chatRoute(request)
            "group" -> requestHandler.groupRoute(request)
            "auth" -> requestHandler.authRoute(request)
        }
    }

    private fun closeConnection() {
        try {
            reader.close()
            writer.close()
            socket.close()
            if (Server.clients[client.number]?.size!! > 1) Server.clients[client.number]?.remove(this)
            else Server.clients.remove(client.number)
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
    }
}
