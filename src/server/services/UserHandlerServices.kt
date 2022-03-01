package server.services

interface UserHandlerServices {
    fun changeName(data: Any)
    fun getProfiles(data: Any)
    fun getUserGroups(data: Any)
    fun updateProfile(data: Any)
    fun startPrivateChat(data: Any)
}