package network.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiRequestDto(
    @SerialName("messages")
    val messages: List<MessageDto> = listOf(),
    @SerialName("model")
    val model: String = ""
)