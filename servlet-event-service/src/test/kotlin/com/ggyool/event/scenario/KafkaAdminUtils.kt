package com.ggyool.event.scenario

import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.NewTopic

object KafkaAdminUtils {

    fun clearTopics(vararg topics: String) {
        val admin = AdminClient.create(
            mapOf(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG to "localhost:9094,localhost:9095"
            )
        )
        admin.deleteTopics(topics.toList()).all().get()
        val newTopics = topics.map { it -> NewTopic(it, 2, 2) }
        admin.createTopics(newTopics).all().get()
        admin.close()
    }
}