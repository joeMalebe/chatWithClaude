package network.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UsageDto(
    @SerialName("completion_tokens")
    val completionTokens: Int = 0,
    @SerialName("completion_tokens_details")
    val completionTokensDetails: CompletionTokensDetailsDto = CompletionTokensDetailsDto(),
    @SerialName("prompt_tokens")
    val promptTokens: Int = 0,
    @SerialName("prompt_tokens_details")
    val promptTokensDetails: PromptTokensDetailsDto = PromptTokensDetailsDto(),
    @SerialName("total_tokens")
    val totalTokens: Int = 0
)