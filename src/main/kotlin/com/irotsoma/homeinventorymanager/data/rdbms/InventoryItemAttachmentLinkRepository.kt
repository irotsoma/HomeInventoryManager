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

/*
 * Created by irotsoma on 7/17/2020.
 */
package com.irotsoma.homeinventorymanager.data.rdbms

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * JPA repository object for storing attachment links
 *
 * @author Justin Zak
 */
@Repository
interface InventoryItemAttachmentLinkRepository : JpaRepository<InventoryItemAttachmentLink, Int> {
    /**
     * Find a list of link records associated with an attachment.
     *
     * @param attachmentId ID of the attachment.
     * @return A collection of [InventoryItemAttachmentLink] representing the links.
     */
    fun findByAttachmentId(attachmentId: Int): Collection<InventoryItemAttachmentLink>
    /**
     * Find a specific link record between an inventory item and an attachment.
     *
     * @param inventoryItemId ID of the inventory item
     * @param attachmentId ID of the attachment.
     * @return An [InventoryItemAttachmentLink] representing the link or null if not found.
     */
    fun findByInventoryItemIdAndAttachmentId(inventoryItemId: Int, attachmentId: Int): InventoryItemAttachmentLink?
}