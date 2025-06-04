package it.polito.waii_24.g20.analytics.consumer

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class AnalyticsConsumer {

    @KafkaListener(topics = ["crm-analytics-topic"], groupId = "analytics_group")
    fun consume(message: String) {
        // Process the message, e.g., store in a database or perform analytics
    }
}