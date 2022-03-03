package server.services

import entities.Request

interface RequestHandlerServices {
    fun authRoute(request: Request)
    fun chatRoute(request: Request)
    fun groupRoute(request: Request)
    fun userRoute(request: Request)
}