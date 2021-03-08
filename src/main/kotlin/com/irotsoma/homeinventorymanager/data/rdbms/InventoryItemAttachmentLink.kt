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

import mu.KLogging
import org.hibernate.annotations.CreationTimestamp
import java.util.*
import javax.persistence.*

/**
 * JPA Object representing the link between an inventory item and an attachment
 *
 * @author Justin Zak
 * @property id Database-generated ID for the record.
 * @property inventoryItemId ID of the inventory item.
 * @property attachmentId ID of the attachment.
 * @property created Indicates the date the entry was created. (read-only)
 */
@Entity
@Table(name = "inventory_item_attachment")
class InventoryItemAttachmentLink(@Column(name = "inventory_item_id", nullable = false) var inventoryItemId: Int,
                                  @Column(name = "attachment_id", nullable = false) var attachmentId: Int) {
    /** kotlin-logging implementation */
    companion object : KLogging()
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    var id: Int? = null

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created", nullable = false)
    var created: Date? = null
        private set
}