package com.irotsoma.homeinventorymanager

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

/**
 * A bean to register the Kotlin module for Jackson
 *
 * @author Justin Zak
 */
@Configuration
class ObjectMapperConfiguration {
    /**
     * Function to generate the necessary Jackson factories for parsing JSON
     */
    @Bean
    @Primary
    fun objectMapper() = ObjectMapper().apply {
        registerModule(KotlinModule())
    }
}