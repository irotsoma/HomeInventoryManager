/*
 * Created by irotsoma on 7/7/2020.
 */
package com.irotsoma.homeinventorymanager.webui.models

import com.irotsoma.web.validation.OneNotBlank
import java.math.BigDecimal
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.PastOrPresent

@OneNotBlank(fields=["purchasePrice", "value"], message = "Either estimated value or purchase price must be populated.")
class InventoryItemForm {
    @NotBlank
    var name: String = ""
    var description: String? = null
    var value: BigDecimal? = null
    @PastOrPresent
    var purchaseDate: Date? = null
    var purchasePrice: BigDecimal? = null
    var manufacturer: String? = null
    var serialNumber: String? = null
    var properties: String? = null
    var rooms: String? = null
    var categories: String? = null
    var attachments: Set<Int> = hashSetOf()

}