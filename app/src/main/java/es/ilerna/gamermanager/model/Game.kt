package es.ilerna.gamermanager.model

data class Game(
    val name: String,
    val status: String,
    val platform: String,
    val id: Int,
    val company: String,
    val genre: String,
    val valoration: Int,
    val image: String? = null
)
