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
 * Created by irotsoma on 6/30/2020.
 */
package com.irotsoma.homeinventorymanager.data

import mu.KLogging
import org.hibernate.annotations.*
import java.math.BigDecimal
import java.util.*
import javax.persistence.*
import javax.persistence.Entity
import javax.persistence.Table

/**
 * JPA User Account Object
 * Soft deletes and adds a deleted note to the name as well as timestamp to make it unique for the purposes of the uniqueness index so it can be reused
 *
 * @author Justin Zak
 * @property id Database-generated ID for the user.
 * @property name Name of the record. (required)
 * @property description Long form description of the item to be used for any details without detail fields
 * @property estimatedValue The estimated current monetary value of the item. (optional, but at least one of value and purchase price should be filled for full functionality) (use null if not used)
 * @property purchaseDate Date the item was purchased if applicable
 * @property purchasePrice The price of the item when purchased if applicable (optional, but at least one of value and purchase price should be filled for full functionality) (use null if not used)
 * @property manufacturer The name of the manufacturer of the item
 * @property serialNumber The serial number of the item if applicable
 * @property attachments List of attached files such as receipts, pictures, videos, etc.
 * @property property The property/location where the item is stored.
 * @property room The room where the room is usually stored when not in use.
 * @property category The category the item falls into.
 * @property propertyName Convenience property for storing the name of the property for use in reports. (read-only)
 * @property roomName Convenience property for storing the name of the room for use in reports. (read-only)
 * @property categoryName Convenience property for storing the name of the category for use in reports. (read-only)
 * @property purchaseDateFormatted Convenience property for storing a formatted version of the purchase date for use in the UI (read-only)
 * @property user The user that owns the record. (required)
 * @property state Indicates if a record is enabled in the system. (required)
 * @property created Indicates the date the entry was created. (read-only)
 * @property updated Indicates the date the entry was last updated. (read-only)
 */
@Entity
@Table(name = "inventory_item")
@SQLDelete(sql = "UPDATE inventory_item SET state = 'DELETED', name = (SELECT CONCAT(name, '--DELETED--', CURRENT_TIMESTAMP)) WHERE id = ?", check = ResultCheckStyle.COUNT)
@Where(clause = "state = 'ACTIVE'")
class InventoryItem(@Column(name = "name", nullable = false) var name: String,
                    @Column(name = "description") var description: String?,
                    @Column(name = "value") var estimatedValue: BigDecimal?,
                    @Column(name = "purchase_date") var purchaseDate: Date?,
                    @Column(name = "purchase_price") var purchasePrice: BigDecimal?,
                    @Column(name = "manufacturer") var manufacturer: String?,
                    @Column(name = "serial_number") var serialNumber: String?,
                    @Column(name = "state", nullable = false) @Enumerated(EnumType.STRING) var state: DataState
) {
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

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated")
    var updated: Date? = null
        private set

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name="inventory_item_attachment",
                joinColumns = [JoinColumn(name="inventory_item_id")],
                inverseJoinColumns = [JoinColumn(name="attachment_id")])
    var attachments: Set<Attachment> = hashSetOf()

    @OneToOne()
    @JoinColumn(name = "property_id", referencedColumnName = "id")
    var property: Property? = null

    @OneToOne()
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    var user: User? = null

    @OneToOne()
    @JoinColumn(name = "room_id", referencedColumnName = "id")
    var room: Room? = null

    @OneToOne()
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    var category: Category? = null

    @Formula("(select r.name from room r where r.id = room_id)")
    var roomName: String? = ""
        private set
    @Formula("(select c.name from category c where c.id = category_id)")
    var categoryName: String? = ""
        private set
    @Formula("(select p.name from property p where p.id = property_id)")
    var propertyName: String? = ""
        private set

    @Column(name="purchase_date", insertable = false, updatable=false)
    var purchaseDateFormatted: String? = null

    /**
     * If the room, category, or property associated with the record do not have a name, sets the appropriate name to "None".
     */
    @PostLoad
    fun populateSubTableNames(){
        if (room?.name?.trim().isNullOrBlank()){
            roomName = "None"
        }
        if (category?.name?.trim().isNullOrBlank()){
            categoryName = "None"
        }
        if (property?.name?.trim().isNullOrBlank()){
            propertyName = "None"
        }
    }

}