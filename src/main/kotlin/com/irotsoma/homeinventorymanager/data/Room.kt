package com.irotsoma.homeinventorymanager.data

import mu.KLogging
import org.hibernate.annotations.*
import java.util.*
import javax.persistence.*
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "room")
@SQLDelete(sql = "UPDATE room SET state = 'deleted' WHERE id = ?", check = ResultCheckStyle.COUNT)
@Where(clause = "state = 'active'")
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

    @PostLoad
    fun calculateIsActivelyUsed(){
        isActivelyUsed = if (inventoryItems?.isNotEmpty() == true) { true } else { null }
    }

    override fun toString(): String {
        return name;
    }
}

