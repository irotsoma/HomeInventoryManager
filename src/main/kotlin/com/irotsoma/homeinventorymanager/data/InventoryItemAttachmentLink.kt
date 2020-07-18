/*
 * Created by irotsoma on 7/17/2020.
 */
package com.irotsoma.homeinventorymanager.data

import mu.KLogging
import org.hibernate.annotations.CreationTimestamp
import java.util.*
import javax.persistence.*

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