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
 * JPA repository object for storing categories
 *
 * @author Justin Zak
 */
@Repository
interface CategoryRepository: JpaRepository<Category, Int> {
    /**
     * Retrieve a record all records associated with a user by user ID
     *
     * @param userId The user id of the categories to retrieve
     * @returns A collection of [Category] representing the database records
     */
    fun findByUserId(userId: Int): Collection<Category>
    /**
     * Retrieve a record by name for the given user ID
     *
     * @param name The name of the category record to retrieve
     * @param userId The user id of the category to retrieve
     * @returns A [Category] representing the database record or null
     */
    fun findByNameAndUserId(name: String, userId: Int): Category?
}