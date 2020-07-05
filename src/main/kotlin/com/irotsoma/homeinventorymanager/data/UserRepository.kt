package com.irotsoma.homeinventorymanager.data

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * JPA repository object for storing user accounts
 *
 * @author Justin Zak
 */
@Repository
interface UserRepository : JpaRepository<User, Int> {
    /**
     * retrieve a record by the username of the user
     *
     * @param username The username of the user to retrieve
     * @returns An instance of [User] representing the database record or null if the username was not found
     */
    fun findByUsername(username: String): User?
}