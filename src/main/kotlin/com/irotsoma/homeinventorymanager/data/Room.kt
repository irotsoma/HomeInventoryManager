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
 * @property userId The user that owns the record.
 * @property state Indicates if a record is enabled in the system.
 * @property created Indicates the date the entry was created. (read-only)
 * @property updated Indicates the date the entry was last updated. (read-only)
 * @property inventoryItems A list of any inventory items currently using this record.
 * @property isActivelyUsed Calculated after loading the entity as a convenience for mustache templates to determine if the record is in use by an inventory item (true if true, null if false) (read-only) (transient)
 */
@Entity
@Table(name = "room")
@SQLDelete(sql = "UPDATE room SET state = 'DELETED', name = (SELECT CONCAT(name, '--DELETED--', CURRENT_TIMESTAMP)) WHERE id = ?", check = ResultCheckStyle.COUNT)
@Where(clause = "state = 'ACTIVE'")
class Room (@Column(name = "user_id", nullable = false) var userId: Int,
            @Column(name = "name", nullable = false) var name: String,
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
    @JoinColumn(name = "room_id")
    @LazyCollection(LazyCollectionOption.EXTRA)
    var inventoryItems : Collection<InventoryItem>? = null

    @Transient
    var isActivelyUsed: Boolean? = null
        private set

    @PostLoad
    fun calculateIsActivelyUsed(){
        isActivelyUsed = if (inventoryItems?.isNotEmpty() == true) { true } else { null }
    }

    override fun toString(): String {
        return name;
    }
}

