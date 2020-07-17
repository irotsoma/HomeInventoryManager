/*
 * Created by irotsoma on 7/3/2020.
 */
package com.irotsoma.homeinventorymanager.data

import mu.KLogging
import org.hibernate.annotations.*
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
 * @property name Name of the record.
 * @property addressStreet Street address
 * @property addressCity Address City
 * @property addressState Address State/Province
 * @property addressPostalCode Address Postal Code
 * @property addressCountry Address Country
 * @property userId The user that owns the record.
 * @property state Indicates if a record is enabled in the system.
 * @property created Indicates the date the entry was created. (read-only)
 * @property updated Indicates the date the entry was last updated. (read-only)
 * @property inventoryItems A list of any inventory items currently using this record.
 * @property isActivelyUsed Calculated after loading the entity as a convenience for mustache templates to determine if the record is in use by an inventory item (true if true, null if false) (read-only) (transient)
 */
@Entity
@Table(name = "property")
@SQLDelete(sql = "UPDATE property SET state = 'DELETED', name = (SELECT CONCAT(name, '--DELETED--', CURRENT_TIMESTAMP)) WHERE id = ?", check = ResultCheckStyle.COUNT)
@Where(clause = "state = 'ACTIVE'")
class Property(@Column(name = "user_id", nullable = false, updatable = false) val userId: Int,
               @Column(name = "name", nullable = false) var name: String,
               @Column(name = "address_street") var addressStreet: String?,
               @Column(name = "address_city") var addressCity: String?,
               @Column(name = "address_state") var addressState: String?,
               @Column(name = "address_postal_code") var addressPostalCode: String?,
               @Column(name = "address_country") var addressCountry: String?,
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

    @OneToMany(fetch=FetchType.LAZY)
    @JoinColumn(name = "property_id")
    @LazyCollection(LazyCollectionOption.EXTRA)
    var inventoryItems : Collection<InventoryItem>? = null

    @Transient
    var isActivelyUsed: Boolean? = null

    @PostLoad
    fun calculateIsActivelyUsed(){
        isActivelyUsed = if (inventoryItems?.isNotEmpty() == true) { true } else { null }
    }

    override fun toString(): String {
        return name;
    }
}