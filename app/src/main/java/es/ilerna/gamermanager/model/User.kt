package es.ilerna.gamermanager.model

data class User(
    val name: String,
    val password: String,
    val userid: Int = 0,
    val follower: Boolean? = null,
    val following: Boolean? = null
)
