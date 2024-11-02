package network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

val client by lazy { getClient() }

fun getClient(): HttpClient {
    return HttpClient {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    explicitNulls = false
                }
            )
        }
        install(Logging) {
            level = LogLevel.ALL
        }
        defaultRequest {
            header("content-type", "application/json")
            header("x-rapidapi-host", "claude-3-haiku-ai.p.rapidapi.com")
            header("x-rapidapi-key", "e151ba345bmshade52e55f3b5599p104313jsn506dedc049ff")
            url("https://claude-3-haiku-ai.p.rapidapi.com/")
        }
    }
}