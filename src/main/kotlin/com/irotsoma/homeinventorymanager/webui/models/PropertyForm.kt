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

import javax.validation.constraints.NotBlank

/**
 * Used to hold values and set validation rules using annotations for an HTML form for properties
 *
 * @author Justin Zak
 * @property propertyName The name of the property
 * @property street The address street.
 * @property city The address city.
 * @property state The address state or province.
 * @property postalCode The address postal code.
 * @property country The address country.
 */
class PropertyForm {
    @NotBlank
    var propertyName: String = ""
    var street: String? = null
    var city: String? = null
    var state: String? = null
    var postalCode: String? = null
    var country: String? = null
}