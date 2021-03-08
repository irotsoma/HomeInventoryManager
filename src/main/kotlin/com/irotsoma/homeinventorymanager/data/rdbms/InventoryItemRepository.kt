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

package com.irotsoma.homeinventorymanager.data.rdbms

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * JPA repository object for inventory items
 *
 * @author Justin Zak
 */
@Repository
interface InventoryItemRepository : JpaRepository<InventoryItem, Int> {
    /**
     * Finds all inventory items associated with a user by user ID
     *
     * @param userId User ID to search for.
     * @return A collection of [InventoryItem]
     */
    fun findByUserId(userId: Int): Collection<InventoryItem>
}
