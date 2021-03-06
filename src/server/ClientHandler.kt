package server

import entities.Profile
import entities.Request
import server.services.RequestHandlerServices
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.Socket

class ClientHandler(private val socket: Socket) : Runnable {

    var client: Profile = Profile("", 0)
    val writer = ObjectOutputStream(socket.getOutputStream())
    private val reader = ObjectInputStream(socket.getInputStream())
    private val requestHandler: RequestHandlerServices = RequestHandler(
        AuthHandler(this),
        ChatHandler(this),
        GroupHandler(this, ChatHandler(this)),
        UserHandler(this)
    )

    override fun run() {
        try {
            while (socket.isConnected) {
                try {
                    val obj = reader.readObject() as Request
                    requestHandler(obj)
                } catch (_: Exception) {
                    writer.writeObject(null)
                }
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
            if (reader != null) reader.close()
            if (writer != null) writer.close()
            if (socket.isConnected) socket.close()
            if (Server.clients.contains(client.number)) {
                if (Server.clients[client.number]?.size!! > 1) Server.clients[client.number]?.remove(this)
                else Server.clients.remove(client.number)
            }
        } catch (ex: IOException) {
        }
    }
}
