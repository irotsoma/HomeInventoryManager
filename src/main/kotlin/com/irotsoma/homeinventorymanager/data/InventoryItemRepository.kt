/*
 * Created by irotsoma on 7/2/2020.
 */
package com.irotsoma.homeinventorymanager.data

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface InventoryItemRepository : JpaRepository<InventoryItem, Int> {

}
