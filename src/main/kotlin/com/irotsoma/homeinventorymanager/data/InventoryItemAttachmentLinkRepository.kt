/*
 * Created by irotsoma on 7/17/2020.
 */
package com.irotsoma.homeinventorymanager.data

import org.springframework.data.jpa.repository.JpaRepository



interface InventoryItemAttachmentLinkRepository : JpaRepository<InventoryItemAttachmentLink, Int> {
    fun findByAttachmentId(attachmentId: Int): Set<InventoryItemAttachmentLink>
    fun findByInventoryItemIdAndAttachmentId(inventoryItemId: Int, attachmentId: Int): InventoryItemAttachmentLink?
}