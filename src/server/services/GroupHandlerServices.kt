package server.services

interface GroupHandlerServices {
    fun leaveGroup(data: Any)
    fun updateGroup(data: Any)
    fun getGroup(data: Any)
    fun createGroup(data: Any)
    fun joinGroup(data: Any)
    fun checkGroupNameAvailability(data: Any)
}