/*
 *  Copyright (C) 2020  Justin Zak
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 */

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