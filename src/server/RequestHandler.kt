package server

import entities.Request
import server.services.AuthHandlerServices
import server.services.ChatHandlerServices
import server.services.GroupHandlerServices
import server.services.UserHandlerServices

class RequestHandler(
    private val authHandler: AuthHandlerServices,
    private val chatHandler: ChatHandlerServices,
    private val groupHandler: GroupHandlerServices,
    private val userHandler: UserHandlerServices
) {
    fun authRoute(request: Request) {
        when (request.route) {
            "signup" -> authHandler.signUp(request.data)
            "signin" -> authHandler.signIn(request.data)
        }
    }

    fun chatRoute(request: Request) {
        when (request.route) {
            "broadcast" -> chatHandler.broadcastMessage(request.data)
            "getchat" -> chatHandler.getChat(request.data)
        }
    }

    fun groupRoute(request: Request) {
        when (request.route) {
            "join" -> groupHandler.joinGroup(request.data)
            "create" -> groupHandler.createGroup(request.data)
            "get" -> groupHandler.getGroup(request.data)
            "update" -> groupHandler.updateGroup(request.data)
            "leave" -> groupHandler.leaveGroup(request.data)
            "checkAvailability" -> groupHandler.checkGroupNameAvailability(request.data)
        }
    }

    fun userRoute(request: Request) {
        when (request.route) {
            "changeName" -> userHandler.changeName(request.data)
            "getProfiles" -> userHandler.getProfiles(request.data)
            "getUserGroups" -> userHandler.getUserGroups(request.data)
        }
    }
}