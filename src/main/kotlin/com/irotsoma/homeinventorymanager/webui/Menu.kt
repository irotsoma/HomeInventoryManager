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
 * Object representing a top level Menu
 *
 * @author Justin Zak
 */
class Menu() {
    var nameProperty: String = ""
    var name: String = ""
    var path: String? = null
    var validUserRoles: ArrayList<String> = ArrayList()
    var menuItems:ArrayList<MenuItem> = ArrayList()
    var containsMenuItems = false
    var disabled = true
    constructor(nameProperty:String, name:String, path:String?, validUserRoles: ArrayList<String>, menuItems:ArrayList<MenuItem>, disabled:Boolean = true): this() {
        this.nameProperty=nameProperty
        this.name = name
        this.path=path
        this.validUserRoles=validUserRoles
        this.menuItems=menuItems
        if (menuItems.size > 0){
            containsMenuItems = true
        }
        this.disabled = disabled
    }
}