package network.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiResponseDto(
    @SerialName("choices")
    val choices: List<ChoiceDto> = listOf(),
    @SerialName("created")
    val created: Int = 0,
    @SerialName("id")
    val id: String = "",
    @SerialName("model")
    val model: String = "",
    @SerialName("object")
    val objectX: String = "",
    @SerialName("usage")
    val usage: UsageDto = UsageDto()
)