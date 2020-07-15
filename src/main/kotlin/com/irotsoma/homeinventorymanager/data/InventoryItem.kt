/*
 * Created by irotsoma on 6/30/2020.
 */
package com.irotsoma.homeinventorymanager.data

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
class InventoryItem(@Column(name = "name", nullable = false) var name: String,
                    @Column(name = "description") var description: String?,
                    @Column(name = "value") var value: BigDecimal?,
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

    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinTable(name="inventory_item_attachment",
                joinColumns = [JoinColumn(name="inventory_item_id")],
                inverseJoinColumns = [JoinColumn(name="attachment_id")])
    var attachments: Set<Attachment> = hashSetOf()

    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "property_id", referencedColumnName = "id")
    var property: Property? = null

    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    var user: User? = null

    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "room_id", referencedColumnName = "id")
    var room: Room? = null

    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    var category: Category? = null

    @Formula("(select r.name from room r where r.id = room_id)")
    var roomName: String = ""
    @Formula("(select c.name from category c where c.id = category_id)")
    var categoryName: String = ""
    @Formula("(select p.name from property p where p.id = property_id)")
    var propertyName: String = ""

    @Column(name="purchase_date", insertable = false, updatable=false)
    var purchaseDateFormatted: String? = null
        private set

    @PostLoad
    fun populateSubTableNames(){
        roomName = room?.name ?: "None"
        categoryName = category?.name ?: "None"
        propertyName = property?.name ?: "None"
    }

}