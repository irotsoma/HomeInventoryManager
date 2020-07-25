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

class Option()  {
    var id: String = ""
    var name: String = ""
    var selected: String = ""
    constructor(name: String): this(name,true)
    constructor(name: String, selected: Boolean) : this() {
        this.name = name
        this.selected = if (selected) "selected" else ""
    }
    constructor(id: String, name: String, selected: Boolean) : this() {
        this.id = id
        this.name = name
        this.selected = if (selected) "selected" else ""
    }
    override fun equals(other: Any?): Boolean{
        return if (other is Option){
            (id == other.id && name == other.name && selected == other.selected)
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        var result = id.hashCode() + name.hashCode()
        result = 31 * result + selected.hashCode()
        return result
    }

}