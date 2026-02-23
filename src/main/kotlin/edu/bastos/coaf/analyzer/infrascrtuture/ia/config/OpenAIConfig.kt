package edu.bastos.coaf.analyzer.infrascrtuture.ia.config

import org.springframework.ai.openai.OpenAiChatClient  // Note: é OpenAiChatClient, não ChatModel
import org.springframework.ai.openai.api.OpenAiApi
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.retry.support.RetryTemplate

@Configuration
class OpenAIConfig {

    @Value("\${spring.ai.openai.api-key}")
    private lateinit var apiKey: String

    @Bean
    fun chatClient(): OpenAiChatClient {
        val retryTemplate = RetryTemplate.builder()
            .maxAttempts(3)
            .exponentialBackoff(1000, 2.0, 10000)
            .build()

        return OpenAiChatClient(OpenAiApi(apiKey))
    }
}