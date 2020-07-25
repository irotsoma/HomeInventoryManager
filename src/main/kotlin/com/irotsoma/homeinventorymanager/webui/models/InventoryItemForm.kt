/*
 *  Copyright (C) 2020  Justin Zak
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 */

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