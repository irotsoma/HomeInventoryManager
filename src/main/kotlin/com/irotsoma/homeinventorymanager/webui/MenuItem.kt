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

package com.irotsoma.homeinventorymanager.webui

/**
 * Object representing items in a menu
 *
 * @author Justin Zak
 */
class MenuItem(){
    var nameProperty: String =""
    var name: String = ""
    var path: String = ""
    var validUserRoles: ArrayList<String> = ArrayList()
    var disabled = true
    constructor(nameProperty:String, name:String, path:String, validUserRoles: ArrayList<String>, disabled: Boolean = true):this(){
        this.nameProperty=nameProperty
        this.name=name
        this.path=path
        this.validUserRoles=validUserRoles
        this.disabled = disabled
    }
}