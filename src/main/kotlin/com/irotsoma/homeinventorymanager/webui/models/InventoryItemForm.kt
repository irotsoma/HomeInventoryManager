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

package com.irotsoma.homeinventorymanager.webui.models

import com.irotsoma.web.validation.OneNotBlank
import javax.validation.constraints.NotBlank

/**
 * Used to hold values and set validation rules using annotations for an HTML form for inventory Items
 *
 * @author Justin Zak
 * @property name The name of the item
 * @property description A description of the item
 * @property estimatedValue An formatted version of the estimated value. Must be parsed into a decimal value.
 * @property purchasePrice The purchase price of the item
 * @property purchaseDate The purchase date of the item
 * @property manufacturer The manufacturer of the item
 * @property serialNumber The serial number of the item
 * @property properties The property the item is stored at.
 * @property rooms The room the property is stored in.
 * @property category The category the item belongs to.
 * @property attachments A list of attachment IDs associated with the item
 */
@OneNotBlank(fields=["purchasePrice", "estimatedValue"], message = "At least one of Estimated Value or Purchase Price must be populated.") //TODO: change once validation library allows for translation
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