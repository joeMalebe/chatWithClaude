package network.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChoiceDto(
    @SerialName("finish_reason")
    val finishReason: String = "",
    @SerialName("index")
    val index: Int = 0,
    @SerialName("message")
    val message: MessageDto = MessageDto()
)