/*
 * Created by irotsoma on 6/30/2020.
 */
package com.irotsoma.homeinventorymanager.data

import com.irotsoma.homeinventorymanager.authentication.DataState
import mu.KLogging
import org.hibernate.annotations.*
import java.math.BigDecimal
import java.util.*
import javax.persistence.*
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "inventory_item")
@SQLDelete(sql = "UPDATE inventory_item SET state = 'deleted' WHERE id = ?", check = ResultCheckStyle.COUNT)
@Where(clause = "state = 'active'")
class InventoryItem(@Column(name = "name", nullable = false) val name: String,
                    @Column(name = "description") val description: String,
                    @Column(name = "value") val value: BigDecimal,
                    @Column(name = "state", nullable = false) @Enumerated(EnumType.STRING) var state: DataState
) {
    /** kotlin-logging implementation */
    companion object : KLogging()
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    var id: Int = -1


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

    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinTable(name="inventory_item-attachment",
                joinColumns = [JoinColumn(name="inventory_item_id")],
                inverseJoinColumns = [JoinColumn(name="attachment_id")])
    var attachments: Set<Attachment> = hashSetOf()

    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "property_id", referencedColumnName = "id")
    var property: Property? = null

    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    var user: User? = null

    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "room_id", referencedColumnName = "id")
    var room: Room? = null

}