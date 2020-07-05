package com.irotsoma.homeinventorymanager

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

/**
 * Primary class
 *
 * @author Justin Zak
 */
@SpringBootApplication
@EnableJpaRepositories
@EnableConfigurationProperties
class HomeInventoryManager

/**
 * Startup function
 */
fun main(args: Array<String>) {
    SpringApplication.run(HomeInventoryManager::class.java, *args)
}
