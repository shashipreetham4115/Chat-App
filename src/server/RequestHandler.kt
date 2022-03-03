package server

import entities.Request
import server.services.*

class RequestHandler(
    private val authHandler: AuthHandlerServices,
    private val chatHandler: ChatHandlerServices,
    private val groupHandler: GroupHandlerServices,
    private val userHandler: UserHandlerServices
) : RequestHandlerServices {
    override fun authRoute(request: Request) {
        try {
            when (request.route) {
                "signup" -> authHandler.signUp(request.data)
                "signin" -> authHandler.signIn(request.data)
            }
        } catch (_: Exception) {
        }
    }

    override fun chatRoute(request: Request) {
        try {
            when (request.route) {
                "broadcast" -> chatHandler.broadcastMessage(request.data)
                "getchat" -> chatHandler.getChat(request.data)
            }
        } catch (_: Exception) {
        }
    }

    override fun groupRoute(request: Request) {
        try {
            when (request.route) {
                "join" -> groupHandler.joinGroup(request.data)
                "create" -> groupHandler.createGroup(request.data)
                "get" -> groupHandler.getGroup(request.data)
                "update" -> groupHandler.updateGroup(request.data)
                "leave" -> groupHandler.leaveGroup(request.data)
                "checkAvailability" -> groupHandler.checkGroupNameAvailability(request.data)
                "adduser" -> groupHandler.addUser(request.data)
            }
        } catch (_: Exception) {
        }
    }

    override fun userRoute(request: Request) {
        try {
            when (request.route) {
                "changeName" -> userHandler.changeName(request.data)
                "getProfiles" -> userHandler.getProfiles(request.data)
                "getUserGroups" -> userHandler.getUserGroups(request.data)
                "updateProfile" -> userHandler.updateProfile(request.data)
                "StartPrivateChat" -> userHandler.startPrivateChat(request.data)
            }
        } catch (_: Exception) {
        }
    }
}