package network.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageDto(
    @SerialName("aduio")
    val aduio: AduioDto? = null,
    @SerialName("content")
    val content: String = "",
    @SerialName("role")
    val role: String = ""
)