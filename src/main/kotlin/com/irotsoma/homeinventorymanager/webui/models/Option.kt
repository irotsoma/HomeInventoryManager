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
 * Class representing an html option item in a select form field.
 *
 * @author Justin Zak
 * @property id The id of the item.
 * @property name The name of the item.
 * @property selected "selected" if the item is selected, empty string if not
 */
class Option()  {
    var id: String = ""
    var name: String = ""
    var selected: String = ""

    /**
     * Secondary constructor that sets the name and sets selected to true
     *
     * @param name The name of the item.
     */
    constructor(name: String): this(name,true)

    /**
     * Secondary constructor that sets the name and if the option is selected
     *
     * @param name The name of the item.
     * @param selected true if item is selected, false if not
     */
    constructor(name: String, selected: Boolean) : this() {
        this.name = name
        this.selected = if (selected) "selected" else ""
    }
    /**
     * Secondary constructor that sets all of the properties
     *
     * @param id The id of the item.
     * @param name The name of the item.
     * @param selected true if item is selected, false if not
     */
    constructor(id: String, name: String, selected: Boolean) : this() {
        this.id = id
        this.name = name
        this.selected = if (selected) "selected" else ""
    }

    /**
     * Override of method for determining if two Option instances are equal. Allows for using standard comparison operators.
     *
     * @param other The instance to compare to the current instance.
     * @return true if equal, false if not
     */
    override fun equals(other: Any?): Boolean{
        return if (other is Option){
            (id == other.id && name == other.name && selected == other.selected)
        } else {
            false
        }
    }
    /**
     * Override of method for creating a hash code for an instance used for determining equality. Allows for using standard comparison operators.
     *
     * @return returns the hash code as an integer
     */
    override fun hashCode(): Int {
        var result = id.hashCode() + name.hashCode()
        result = 31 * result + selected.hashCode()
        return result
    }

}