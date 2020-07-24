/*
 * Created by irotsoma on 7/7/2020.
 */
package com.irotsoma.homeinventorymanager.webui.models

import com.irotsoma.web.validation.OneNotBlank
import org.springframework.stereotype.Component
import javax.validation.constraints.NotBlank

@Component
@OneNotBlank(fields=["purchasePrice", "estimatedValue"], message = "At least one of Estimated Value or Purchase Price must be populated.")
class InventoryItemForm {
    @NotBlank
    var name: String = ""
    var description: String? = null
    var estimatedValue: String? = null
    var purchaseDate: String? = null
    var purchasePrice: String? = null
    var manufacturer: String? = null
    var serialNumber: String? = null
    var properties: String? = null
    var rooms: String? = null
    var categories: String? = null
    var attachments: Set<Int> = hashSetOf()
}