/*
 * Created by irotsoma on 7/17/2020.
 */
package com.irotsoma.homeinventorymanager.data

import org.springframework.data.jpa.repository.JpaRepository



interface InventoryItemAttachmentLinkRepository : JpaRepository<InventoryItemAttachmentLink, Int> {
    fun findByInventoryItemIdAndAttachmentId(inventoryItemId: Int, attachmentId: Int): InventoryItemAttachmentLink?
}