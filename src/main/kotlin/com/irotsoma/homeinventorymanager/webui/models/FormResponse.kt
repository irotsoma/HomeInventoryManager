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

/**
 * Used to respond to ajax calls to send back errors for form fields.
 *
 * @author Justin Zak
 * @property name The name of the response. (optional)
 * @property validated True if no errors were found in the data, false if errors occurred.
 * @property errorMessages Map of field names and error messages for each field.
 */
class FormResponse() {
    /**
     * Secondary constructor with parameters for each property
     *
     * @param name The name of the response. (optional)
     * @param validated True if no errors were found in the data, false if errors occurred.
     * @param errorMessages Map of field names and error messages for each field.
     */
    constructor(name: String?, validated: Boolean, errorMessages:Map<String, String>?): this(){
        this.name = name
        this.validated = validated
        this.errorMessages = errorMessages
    }
    var name: String? = null
    var validated: Boolean = false
    var errorMessages: Map<String, String>? = null
}